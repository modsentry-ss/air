package malte0811.ferritecore.gui.controls;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.CatUI;
import malte0811.ferritecore.gui.GUIColors;
import malte0811.ferritecore.gui.controls.options.BindOption;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.modules.Module;
import net.minecraft.client.gui.DrawContext;
import malte0811.ferritecore.Utils;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModuleButton {
    public String label;
    public Module module;
    public boolean waitingForBind;
    public Supplier<Boolean> getState;
    public Consumer<Boolean> setState;
    public Supplier<Integer> getBind;
    public Consumer<Integer> setBind;

    public Supplier<Integer> getX, getY;

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    private final int width = 145, height = 18;

    private final List<Option> options = new ArrayList<>();
    private boolean expanded = false;

    public ModuleButton(Module module) {
        this.module = module;
        this.label = module.getName();
        this.getState = module::isEnabled;
        this.setState = module::setEnabled;
        this.getBind = module::getBind;
        this.setBind = module::setBind;

        options.add(new BindOption(this));
        options.addAll(module.getOptions());
    }

    public int getTotalHeight() {
        return height + (expanded ? options.stream().mapToInt(Option::getHeight).sum() : 0) + 1;
    }

    public void render(DrawContext ctx) {
        int color = getState.get() ? GUIColors.moduleEnabled : GUIColors.moduleDisabled;
        String keyDisplay;

        String keyName = null;
        int keyCode = getBind.get();
        if (keyCode != GLFW.GLFW_KEY_UNKNOWN) {
            keyName = GLFW.glfwGetKeyName(keyCode, GLFW.glfwGetKeyScancode(keyCode));
            if (keyName == null) {
                // Handle special keys
                switch (keyCode) {
                    case GLFW.GLFW_KEY_LEFT_SHIFT: keyName = "LSHIFT"; break;
                    case GLFW.GLFW_KEY_RIGHT_SHIFT: keyName = "RSHIFT"; break;
                    case GLFW.GLFW_KEY_LEFT_CONTROL: keyName = "LCTRL"; break;
                    case GLFW.GLFW_KEY_RIGHT_CONTROL: keyName = "RCTRL"; break;
                    case GLFW.GLFW_KEY_LEFT_ALT: keyName = "LALT"; break;
                    case GLFW.GLFW_KEY_RIGHT_ALT: keyName = "RALT"; break;
                    case GLFW.GLFW_KEY_LEFT_SUPER: keyName = "LWIN"; break;
                    case GLFW.GLFW_KEY_RIGHT_SUPER: keyName = "RWIN"; break;
                    // Add more special keys as needed
                    default: keyName = null;
                }
            }
        }
        keyDisplay = keyName != null ? " [" + keyName.toUpperCase(Locale.ROOT) + "]" : "";

        Text labelText = Text.literal(label + keyDisplay).withColor(color);
        Text expandLabel = Text.literal(expanded ? "-" : "+").withColor(color);

        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, labelText, getX.get() + 5, getY.get() + 5, GUIColors.text);
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, expandLabel, getX.get() +width-10, getY.get() + 5, GUIColors.text);

        int finalHeight = height;
        if (expanded) {
            CatUI.rect(getX.get()+height-12, getY.get()+height-3, getX.get()+width-6, getY.get()+height-2, GUIColors.separator);
            int optionY = getY.get() + height;
            for (Option option : options) {
                option.render(ctx, getX.get(), optionY);
                optionY += option.getHeight();
                finalHeight+= option.getHeight();
            }
        }

        ctx.drawStrokedRectangle(getX.get(), getY.get(), width, finalHeight, GUIColors.border);
    }

    public boolean handleClick(double clickX, double clickY, int mouseBtn, int clickType) {
//        if (Utils.isInside(getX.get(), getY.get(), (getX.get()+width), (getY.get()+height), clickX, clickY)) {
//            if (mouseBtn == 0) {
//                setState.accept(!getState.get());
//                return true;
//            } else if (mouseBtn == 1) {
//                expanded = !expanded;
//                if (waitingForBind) {
//                    waitingForBind = false;
//                    setBind.accept(GLFW.GLFW_KEY_UNKNOWN);
//                    return true;
//                }
//                setBind.accept(GLFW.GLFW_KEY_UNKNOWN);
//                waitingForBind = true;
//                return true;
//            }
//        }
//        else if (waitingForBind) {
//            waitingForBind = false;
//        }
//        return false;
        if (Utils.isInside(getX.get(), getY.get(), getX.get()+width, getY.get()+height, clickX, clickY) && clickType == GLFW.GLFW_PRESS) {
            if (mouseBtn == 0) {
                setState.accept(!getState.get());
                return true;
            } else if (mouseBtn == 1) {
                expanded = !expanded;
                return true;
            }
        }

        if (expanded) {
            // For release events, forward to ALL options so dragging stops properly
            if (clickType == GLFW.GLFW_RELEASE) {
                for (Option option : options) {
                    option.handleClick(clickX, clickY, mouseBtn, clickType);
                }
                return false;
            }

            // For press events, only forward to the option under the mouse
            int optionY = getY.get() + height;
            for (Option option : options) {
                if (Utils.isInside(getX.get(), optionY, getX.get()+width, optionY+option.getHeight(), clickX, clickY)) {
                    return option.handleClick(clickX, clickY, mouseBtn, clickType);
                }
                optionY += option.getHeight();
            }
        }
        return false;
    }

    public void handleMove(double x, double y) {
        if (expanded) {
            int optionY = getY.get() + height;
            for (Option option : options) {
                option.handleMove(x, y);
                optionY += option.getHeight();
            }
        }
    }

    public boolean handleKeyPress(int keyCode, int scanCode, int modifiers) {
        if (expanded) {
            for (Option option : options) {
                if (option.handleKeyPress(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean handleCharTyped(char chr, int modifiers) {
        if (expanded) {
            for (Option option : options) {
                if (option.handleCharTyped(chr, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }
}