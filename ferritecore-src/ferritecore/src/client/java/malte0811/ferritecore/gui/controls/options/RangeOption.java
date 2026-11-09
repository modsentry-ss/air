package malte0811.ferritecore.gui.controls.options;

import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.CatUI;
import malte0811.ferritecore.gui.GUIColors;
import malte0811.ferritecore.gui.controls.ModuleButton;
import malte0811.ferritecore.gui.controls.Slider;
import net.minecraft.client.gui.DrawContext;
import malte0811.ferritecore.FerriteCore;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RangeOption implements Option {
    private final ModuleButton moduleBtn;
    private final Slider slider;
    private int xPos, yPos;
    private final String label;
    private final String key;
    private final boolean explicitKey;
    private final Supplier<Float> getter;
    private final Consumer<Float> setter;

    // Textbox state
    private boolean textboxFocused = false;
    private String textboxContent = "";
    private static final int TEXTBOX_WIDTH = 38;
    private static final int TEXTBOX_HEIGHT = 12;

    public RangeOption(ModuleButton moduleBtn, String label, float min, float max, float step, float defaultValue) {
        this.label = label;
        this.key = label;
        this.explicitKey = false;
        this.moduleBtn = moduleBtn;
        this.getter = null;
        this.setter = null;
        this.slider = new Slider(defaultValue, min, max, step,
            () -> xPos + 5,  // Slider starts at left
            () -> yPos + 14   // Below the label
        );
    }

    /**
     * Creates a RangeOption with getter/setter pattern for cleaner module integration.
     */
    public RangeOption(String label, float min, float max, float step,
                       Supplier<Float> getter, Consumer<Float> setter) {
        this.label = label;
        this.key = label;
        this.explicitKey = false;
        this.moduleBtn = null;
        this.getter = getter;
        this.setter = setter;
        this.slider = new Slider(getter.get(), min, max, step,
            () -> xPos + 5,  // Slider starts at left
            () -> yPos + 14   // Below the label
        );
    }

    // New constructor with explicit config key
    public RangeOption(String configKey, String label, float min, float max, float step,
                       Supplier<Float> getter, Consumer<Float> setter) {
        this.label = label;
        this.key = configKey != null ? configKey : label;
        this.explicitKey = configKey != null;
        this.moduleBtn = null;
        this.getter = getter;
        this.setter = setter;
        this.slider = new Slider(getter.get(), min, max, step,
            () -> xPos + 5,  // Slider starts at left
            () -> yPos + 14   // Below the label
        );
    }

    @Override
    public int getHeight() {
        return 28;  // Increased height for two-line layout
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        this.xPos = x;
        this.yPos = y;

        // Sync slider value with getter if using getter/setter pattern
        if (getter != null) {
            slider.value = getter.get();
        }

        // Update textbox content if not focused
        if (!textboxFocused) {
            textboxContent = formatValue(slider.getReal());
        }

        // Draw label on top
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer,
            Text.literal(label),
            x + 5, y + 2, GUIColors.text);

        // Draw slider below the label
        slider.render(ctx);

        // Draw textbox to the right of slider (within bounds)
        int textboxX = x + 5 + slider.getWidth() + 8;
        int textboxY = y + 12;

        // Textbox background
        int bgColor = textboxFocused ? 0xff404060 : 0xff303040;
        CatUI.rect(textboxX, textboxY, textboxX + TEXTBOX_WIDTH, textboxY + TEXTBOX_HEIGHT, bgColor);

        // Textbox border
        int borderColor = textboxFocused ? GUIColors.accent : GUIColors.border;
        ctx.drawStrokedRectangle(textboxX, textboxY, TEXTBOX_WIDTH, TEXTBOX_HEIGHT, borderColor);

        // Textbox text
        String displayText = textboxFocused ? textboxContent + "_" : textboxContent;
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer,
            Text.literal(displayText),
            textboxX + 2, textboxY + 2, GUIColors.text);
    }

    @Override
    public boolean handleClick(double x, double y, int mouseBtn, int clickType) {
        if (clickType != GLFW.GLFW_PRESS) {
            slider.handleClick(x, y, mouseBtn, clickType);
            syncValueToSetter();
            return false;
        }

        // Check if clicking on textbox (new position: right of slider, below label)
        int textboxX = xPos + 5 + slider.getWidth() + 8;
        int textboxY = yPos + 12;

        if (Utils.isInside(textboxX, textboxY, textboxX + TEXTBOX_WIDTH, textboxY + TEXTBOX_HEIGHT, x, y)) {
            textboxFocused = true;
            textboxContent = formatValue(slider.getReal());
            return true;
        } else {
            // Clicked elsewhere, unfocus textbox and apply value
            if (textboxFocused) {
                applyTextboxValue();
                textboxFocused = false;
            }
        }

        slider.handleClick(x, y, mouseBtn, clickType);
        syncValueToSetter();
        return false;
    }

    @Override
    public void handleMove(double x, double y) {
        slider.handleMove(x, y);
        syncValueToSetter();
    }

    public boolean handleKeyPress(int keyCode, int scanCode, int modifiers) {
        if (!textboxFocused) return false;

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            applyTextboxValue();
            textboxFocused = false;
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            textboxFocused = false;
            textboxContent = formatValue(slider.getReal());
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (!textboxContent.isEmpty()) {
                textboxContent = textboxContent.substring(0, textboxContent.length() - 1);
            }
            return true;
        }
        return false;
    }

    public boolean handleCharTyped(char chr, int modifiers) {
        if (!textboxFocused) return false;

        // Only allow digits, decimal point, and minus sign
        if (Character.isDigit(chr) || chr == '.' || chr == '-') {
            textboxContent += chr;
            return true;
        }
        return false;
    }

    private void applyTextboxValue() {
        try {
            float newValue = Float.parseFloat(textboxContent);
            newValue = Math.clamp(newValue, slider.min, slider.max);
            slider.value = newValue;
            syncValueToSetter();
        } catch (NumberFormatException e) {
            // Invalid input, revert to current value
            textboxContent = formatValue(slider.getReal());
        }
    }

    private String formatValue(float value) {
        // Format based on whether the value has decimals
        if (value == (int) value) {
            return String.valueOf((int) value);
        } else {
            return String.format("%.2f", value);
        }
    }

    private void syncValueToSetter() {
        if (setter != null) {
            setter.accept(slider.getReal());
        }
    }

    public float getValue() {
        return slider.getReal();
    }

    @Override
    public String getConfigKey() { return key; }

    @Override
    public boolean isConfigKeyExplicit() { return explicitKey; }

    public boolean isTextboxFocused() {
        return textboxFocused;
    }
}