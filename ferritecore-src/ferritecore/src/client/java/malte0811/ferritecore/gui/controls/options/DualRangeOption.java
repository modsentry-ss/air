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
 * A dual range option with two handles (from X to Y).
 * Useful for setting min/max ranges like chance, distance ranges, etc.
 */
public class DualRangeOption implements Option {
    private final String label;
    private final String key;
    private final float min, max, step;
    private final Supplier<Float> minValueGetter;
    private final Supplier<Float> maxValueGetter;
    private final Consumer<Float> minValueSetter;
    private final Consumer<Float> maxValueSetter;

    private int xPos, yPos;

    // Slider dimensions - smaller and cleaner
    private static final int SLIDER_WIDTH = 60;
    private static final int SLIDER_HEIGHT = 8;
    private static final int SLIDER_X_OFFSET = 55;
    private static final int SLIDER_Y_OFFSET = 16;
    private static final int HANDLE_WIDTH = 4;
    private static final int HANDLE_HEIGHT = 10;

    // Track which handle is being dragged (-1=none, 0=min, 1=max)
    private int draggingHandle = -1;

    // Indicates whether the config key is explicitly set (true) or derived from the label (false)
    private final boolean explicitKey;

    public DualRangeOption(String label, float min, float max, float step,
                           Supplier<Float> minValueGetter, Consumer<Float> minValueSetter,
                           Supplier<Float> maxValueGetter, Consumer<Float> maxValueSetter) {
        this.label = label;
        this.key = label;
        this.min = min;
        this.max = max;
        this.step = step;
        this.minValueGetter = minValueGetter;
        this.minValueSetter = minValueSetter;
        this.maxValueGetter = maxValueGetter;
        this.maxValueSetter = maxValueSetter;
        this.explicitKey = false;
    }

    // New constructor with explicit config key
    public DualRangeOption(String configKey, String label, float min, float max, float step,
                           Supplier<Float> minValueGetter, Consumer<Float> minValueSetter,
                           Supplier<Float> maxValueGetter, Consumer<Float> maxValueSetter) {
        this.label = label;
        this.key = configKey != null ? configKey : label;
        this.min = min;
        this.max = max;
        this.step = step;
        this.minValueGetter = minValueGetter;
        this.minValueSetter = minValueSetter;
        this.maxValueGetter = maxValueGetter;
        this.maxValueSetter = maxValueSetter;
        this.explicitKey = configKey != null;
    }

    @Override
    public int getHeight() {
        return 30;
    }

    private float getMinPercent() {
        return (minValueGetter.get() - min) / (max - min);
    }

    private float getMaxPercent() {
        return (maxValueGetter.get() - min) / (max - min);
    }

    private float snapToStep(float value) {
        if (step <= 0) return value;
        return Math.round(value / step) * step;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        this.xPos = x;
        this.yPos = y;

        float minVal = minValueGetter.get();
        float maxVal = maxValueGetter.get();

        // Draw label
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer,
            Text.literal(label),
            x + 5, y + 2, GUIColors.text);

        // Draw range values
        String rangeText = String.format("%.1f-%.1f", minVal, maxVal);
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer,
            Text.literal(rangeText),
            x + 55 + SLIDER_WIDTH + 5, y + 10, GUIColors.text);

        // Slider track
        int sliderX = x + SLIDER_X_OFFSET;
        int sliderY = y + SLIDER_Y_OFFSET;
        int trackY = sliderY + (SLIDER_HEIGHT / 2) - 2;
        int trackHeight = 4;

        // Draw the track background
        CatUI.rect(sliderX, trackY, sliderX + SLIDER_WIDTH, trackY + trackHeight, GUIColors.border);

        // Draw the filled region between min and max handles
        int minHandleX = sliderX + (int)(getMinPercent() * (SLIDER_WIDTH - HANDLE_WIDTH));
        int maxHandleX = sliderX + (int)(getMaxPercent() * (SLIDER_WIDTH - HANDLE_WIDTH));
        CatUI.rect(minHandleX, trackY, maxHandleX + HANDLE_WIDTH, trackY + trackHeight, GUIColors.accentTransparent);

        // Draw min handle (small rectangle)
        drawHandle(ctx, minHandleX, sliderY);

        // Draw max handle (small rectangle)
        drawHandle(ctx, maxHandleX, sliderY);
    }

    private void drawHandle(DrawContext ctx, int x, int y) {
        int handleY = y + (SLIDER_HEIGHT / 2) - (HANDLE_HEIGHT / 2);
        CatUI.rect(x, handleY, x + HANDLE_WIDTH, handleY + HANDLE_HEIGHT, GUIColors.accent);
    }

    @Override
    public boolean handleClick(double clickX, double clickY, int mouseBtn, int clickType) {
        if (clickType == GLFW.GLFW_RELEASE) {
            draggingHandle = -1;
            return false;
        }

        if (clickType != GLFW.GLFW_PRESS) {
            return false;
        }

        int sliderX = xPos + SLIDER_X_OFFSET;
        int sliderY = yPos + SLIDER_Y_OFFSET;

        // Check if click is within slider area
        if (!Utils.isInside(sliderX - 10, sliderY - 5, sliderX + SLIDER_WIDTH + 10, sliderY + SLIDER_HEIGHT + 5, clickX, clickY)) {
            return false;
        }

        // Determine which handle to drag based on proximity
        int minHandleX = sliderX + (int)(getMinPercent() * (SLIDER_WIDTH - 1));
        int maxHandleX = sliderX + (int)(getMaxPercent() * (SLIDER_WIDTH - 1));

        double distToMin = Math.abs(clickX - minHandleX);
        double distToMax = Math.abs(clickX - maxHandleX);

        if (distToMin <= distToMax) {
            draggingHandle = 0; // min handle
        } else {
            draggingHandle = 1; // max handle
        }

        updateValueFromMouse(clickX);
        return true;
    }

    @Override
    public void handleMove(double x, double y) {
        if (draggingHandle == -1) return;
        updateValueFromMouse(x);
    }

    private void updateValueFromMouse(double mouseX) {
        int sliderX = xPos + SLIDER_X_OFFSET;
        float percent = (float) Math.max(0, Math.min(1, (mouseX - sliderX) / (SLIDER_WIDTH - 1)));
        float rawValue = min + percent * (max - min);
        float snappedValue = snapToStep(rawValue);
        snappedValue = Math.max(min, Math.min(max, snappedValue));

        if (draggingHandle == 0) {
            // Moving min handle - don't let it go past max
            float maxVal = maxValueGetter.get();
            if (snappedValue > maxVal) {
                snappedValue = maxVal;
            }
            minValueSetter.accept(snappedValue);
        } else if (draggingHandle == 1) {
            // Moving max handle - don't let it go below min
            float minVal = minValueGetter.get();
            if (snappedValue < minVal) {
                snappedValue = minVal;
            }
            maxValueSetter.accept(snappedValue);
        }
    }

    @Override
    public String getConfigKey() { return key; }

    @Override
    public boolean isConfigKeyExplicit() { return explicitKey; }
}
