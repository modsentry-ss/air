package malte0811.ferritecore.gui.controls;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.CatUI;
import malte0811.ferritecore.gui.GUIColors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Textbox {
    public String value;
    public Consumer<String> onChange;
    public Supplier<Integer> getX, getY;
    private final int width = 130, height = 15;
    private final int padding = 3;
    private boolean focused = false;
    private int cursorPosition = 0;
    private int cursorBlinkTimer = 0;

    public Textbox(String value, Supplier<Integer> x, Supplier<Integer> y, @Nullable Consumer<String> onChange) {
        this.value = value != null ? value : "";
        this.getX = x;
        this.getY = y;
        this.cursorPosition = this.value.length();
        if (onChange != null) {
            this.onChange = onChange;
        } else {
            this.onChange = (val) -> {};
        }
    }

    public void render(DrawContext ctx) {
        // Draw background
        int bgColor = focused ? GUIColors.headerBackground : GUIColors.headerBackgroundUnfocused;
        CatUI.rect(getX.get(), getY.get(), getX.get() + width, getY.get() + height, bgColor);

        // Draw border
        int borderColor = focused ? GUIColors.accent : GUIColors.border;
        ctx.drawStrokedRectangle(getX.get(), getY.get(), width, height, borderColor);

        // Draw text
        String displayText = value;
        int textX = getX.get() + padding;
        int textY = getY.get() + (height - 8) / 2;

        // Truncate text if it's too long to fit
        int maxTextWidth = width - (padding * 2) - 2;
        if (FerriteCore.mc.textRenderer.getWidth(displayText) > maxTextWidth) {
            // Show the end of the text when it's too long
            while (FerriteCore.mc.textRenderer.getWidth("..." + displayText) > maxTextWidth && displayText.length() > 0) {
                displayText = displayText.substring(1);
            }
            displayText = "..." + displayText;
        }

        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, Text.literal(displayText), textX, textY, GUIColors.text);

        // Draw cursor when focused
        if (focused) {
            cursorBlinkTimer++;
            if ((cursorBlinkTimer / 10) % 2 == 0) {
                String textBeforeCursor = value.substring(0, Math.min(cursorPosition, value.length()));
                int cursorX = textX + FerriteCore.mc.textRenderer.getWidth(textBeforeCursor);
                cursorX = Math.min(cursorX, getX.get() + width - padding - 1);
                CatUI.rect(cursorX, textY - 1, cursorX + 1, textY + 9, GUIColors.text);
            }
        }
    }

    public void handleClick(double clickX, double clickY, int mouseBtn) {
        boolean wasInside = Utils.isInside(getX.get(), getY.get(), getX.get() + width, getY.get() + height, clickX, clickY);

        if (wasInside) {
            focused = true;
            cursorBlinkTimer = 0;
            // Set cursor position based on click location
            int relativeX = (int) (clickX - getX.get() - padding);
            int pos = 0;
            for (int i = 0; i <= value.length(); i++) {
                int textWidth = FerriteCore.mc.textRenderer.getWidth(value.substring(0, i));
                if (textWidth <= relativeX) {
                    pos = i;
                }
            }
            cursorPosition = pos;
        } else {
            focused = false;
        }
    }

    public boolean handleKey(int keyCode, int scanCode, int modifiers) {
        if (!focused) {
            return false;
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE:
                if (cursorPosition > 0 && value.length() > 0) {
                    value = value.substring(0, cursorPosition - 1) + value.substring(cursorPosition);
                    cursorPosition--;
                    onChange.accept(value);
                }
                return true;
            case GLFW.GLFW_KEY_DELETE:
                if (cursorPosition < value.length()) {
                    value = value.substring(0, cursorPosition) + value.substring(cursorPosition + 1);
                    onChange.accept(value);
                }
                return true;
            case GLFW.GLFW_KEY_LEFT:
                if (cursorPosition > 0) {
                    cursorPosition--;
                }
                cursorBlinkTimer = 0;
                return true;
            case GLFW.GLFW_KEY_RIGHT:
                if (cursorPosition < value.length()) {
                    cursorPosition++;
                }
                cursorBlinkTimer = 0;
                return true;
            case GLFW.GLFW_KEY_HOME:
                cursorPosition = 0;
                cursorBlinkTimer = 0;
                return true;
            case GLFW.GLFW_KEY_END:
                cursorPosition = value.length();
                cursorBlinkTimer = 0;
                return true;
            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_ESCAPE:
                focused = false;
                return true;
        }
        return false;
    }

    public boolean handleChar(char character) {
        if (!focused) {
            return false;
        }

        // Only allow printable characters
        if (character >= 32 && character != 127) {
            value = value.substring(0, cursorPosition) + character + value.substring(cursorPosition);
            cursorPosition++;
            cursorBlinkTimer = 0;
            onChange.accept(value);
            return true;
        }
        return false;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) {
            cursorBlinkTimer = 0;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value != null ? value : "";
        this.cursorPosition = Math.min(cursorPosition, this.value.length());
        onChange.accept(this.value);
    }
}
