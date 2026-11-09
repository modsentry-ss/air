package malte0811.ferritecore.gui.controls.options;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.CatUI;
import malte0811.ferritecore.gui.GUIColors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A dropdown/cycle option that cycles through predefined values.
 */
public class DropdownOption implements Option {
    private final String label;
    private final String key;
    private final String[] options;
    private final Supplier<Integer> getter;
    private final Consumer<Integer> setter;

    private int xPos, yPos;
    private final boolean explicitKey;

    public DropdownOption(String label, String[] options, Supplier<Integer> getter, Consumer<Integer> setter) {
        this.label = label;
        this.key = label;
        this.options = options;
        this.getter = getter;
        this.setter = setter;
        this.explicitKey = false;
    }

    // New constructor with explicit config key
    public DropdownOption(String configKey, String label, String[] options, Supplier<Integer> getter, Consumer<Integer> setter) {
        this.label = label;
        this.key = configKey != null ? configKey : label;
        this.options = options;
        this.getter = getter;
        this.setter = setter;
        this.explicitKey = configKey != null;
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        this.xPos = x;
        this.yPos = y;

        int currentIndex = getter.get();
        String currentValue = currentIndex >= 0 && currentIndex < options.length ? options[currentIndex] : "?";

        // Draw label
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer,
            Text.literal(label),
            x + 5, y + 5, GUIColors.text);

        // Draw current value with brackets to indicate it's clickable
        String displayValue = "< " + currentValue + " >";
        int valueWidth = FerriteCore.mc.textRenderer.getWidth(displayValue);
        int valueX = x + 140 - valueWidth;

        CatUI.rect(valueX - 2, y + 2, x + 142, y + 16, 0xff303040);
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer,
            Text.literal(displayValue),
            valueX, y + 5, GUIColors.accent);
    }

    @Override
    public boolean handleClick(double x, double y, int mouseBtn, int clickType) {
        if (clickType != GLFW.GLFW_PRESS) return false;

        if (!Utils.isInside(xPos, yPos, xPos + 145, yPos + 20, x, y)) {
            return false;
        }

        int currentIndex = getter.get();
        if (mouseBtn == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            // Cycle forward
            currentIndex = (currentIndex + 1) % options.length;
        } else if (mouseBtn == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            // Cycle backward
            currentIndex = (currentIndex - 1 + options.length) % options.length;
        }
        setter.accept(currentIndex);
        return true;
    }

    @Override
    public void handleMove(double x, double y) {
        // Not needed for dropdown
    }

    @Override
    public String getConfigKey() { return key; }

    @Override
    public boolean isConfigKeyExplicit() { return explicitKey; }
}
