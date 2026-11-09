package malte0811.ferritecore.gui.controls;

import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.CatUI;
import malte0811.ferritecore.gui.GUIColors;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

public class Slider {
    public float value, min, max, interval;
    public Supplier<Integer> getX, getY;
    private final int width = 75, height = 8;
    private final int handleWidth = 4, handleHeight = 10;

    public float getReal() {
        return Math.clamp(value, min, max);
    }

    public float getPercent() {
        return (value - min) / (max - min);
    }

    public Slider(float value, float min, float max, float interval, Supplier<Integer> x, Supplier<Integer> y) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.interval = interval;
        this.getX = x;
        this.getY = y;
    }

    public void render(DrawContext ctx) {
        int x = getX.get();
        int y = getY.get();

        // Draw track background (thin bar)
        int trackY = y + (height / 2) - 2;
        int trackHeight = 4;
        CatUI.rect(x, trackY, x + width, trackY + trackHeight, GUIColors.border);

        // Draw filled portion up to current value
        int filledWidth = (int)(getPercent() * width);
        CatUI.rect(x, trackY, x + filledWidth, trackY + trackHeight, GUIColors.accentTransparent);

        // Draw handle (small rectangle)
        int handleX = x + (int)(getPercent() * (width - handleWidth));
        int handleY = y + (height / 2) - (handleHeight / 2);
        CatUI.rect(handleX, handleY, handleX + handleWidth, handleY + handleHeight, GUIColors.accent);
    }

    private boolean isMovingValue = false;

    public void handleClick(double clickX, double clickY, int mouseBtn, int clickType) {
        if (isMovingValue && clickType == GLFW.GLFW_RELEASE) {
            isMovingValue = false;
        }
        // Expand hitbox vertically for easier clicking
        int hitboxPadding = 4;
        if (!Utils.isInside(getX.get() - hitboxPadding, getY.get() - hitboxPadding,
                           (getX.get() + width + hitboxPadding), (getY.get() + height + hitboxPadding),
                           clickX, clickY)) {
            return;
        }

        isMovingValue = clickType == GLFW.GLFW_PRESS;
        if (isMovingValue) {
            // Immediately update value on click
            updateValueFromX(clickX);
        }
    }

    public void handleMove(double x, double y) {
        if (!isMovingValue) {
            return;
        }
        updateValueFromX(x);
    }

    private void updateValueFromX(double x) {
        float relativeX = (float) (x - getX.get());
        float percent = Math.max(0, Math.min(1, relativeX / (width - 1)));
        float rawValue = min + percent * (max - min);
        // Snap to interval if specified
        if (interval > 0) {
            rawValue = Math.round(rawValue / interval) * interval;
        }
        value = Math.clamp(rawValue, min, max);
    }

    public int getWidth() {
        return width;
    }
}
