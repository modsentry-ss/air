package malte0811.ferritecore.gui.controls.options;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.GUIColors;
import malte0811.ferritecore.gui.controls.ModuleButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

public class BindOption implements Option {
    private final ModuleButton moduleBtn;
    private boolean waiting = false;

    public BindOption(ModuleButton moduleBtn) {
        this.moduleBtn = moduleBtn;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        String keyDisplay;
        if (waiting) {
            keyDisplay = "...";
            if (!moduleBtn.waitingForBind) {
                waiting = false;
            }
        } else {
            String keyName = null;
            int keyCode = moduleBtn.getBind.get();
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                keyCode = GLFW.GLFW_KEY_UNKNOWN;
            } else
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
                        default: keyName = "???"; break;
                    }
                }
            }
            keyDisplay = keyName != null ? keyName.toUpperCase(Locale.ROOT) : "None";
        }

        ctx.drawText(FerriteCore.mc.textRenderer, Text.literal("Bind: " + keyDisplay), x + 5, y + 4, GUIColors.text, true);
    }

    @Override
    public boolean handleClick(double x, double y, int mouseBtn, int clickType) {
        if (mouseBtn == 0 && clickType == GLFW.GLFW_PRESS) {
            waiting = !waiting;
            moduleBtn.waitingForBind = waiting;
            if (!waiting) {
                moduleBtn.module.setBind(GLFW.GLFW_KEY_UNKNOWN);
            }
            return true;
        } else if (mouseBtn == 1 && clickType == GLFW.GLFW_PRESS) {
            moduleBtn.module.setBind(GLFW.GLFW_KEY_UNKNOWN);
            waiting = false;
            moduleBtn.waitingForBind = false;
            return true;
        }
        return false;
    }

    @Override
    public void handleMove(double x, double y) {

    }
}