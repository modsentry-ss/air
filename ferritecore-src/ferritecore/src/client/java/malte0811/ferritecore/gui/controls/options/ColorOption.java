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
 * A color picker option with HSV sliders and alpha control.
 */
public class ColorOption implements Option {
    private final String label;
    private final String key;
    private final Supplier<Integer> getter;
    private final Consumer<Integer> setter;

    private int xPos, yPos;
    private boolean expanded = false;

    // HSV + Alpha values (cached from color)
    private float hue;        // 0-360
    private float saturation; // 0-1
    private float value;      // 0-1
    private float alpha;      // 0-1

    // Slider dimensions
    private static final int SLIDER_WIDTH = 110;
    private static final int SLIDER_HEIGHT = 10;
    private static final int SLIDER_MARGIN = 3;
    private static final int COLOR_PREVIEW_SIZE = 14;
    private static final int SLIDER_X_OFFSET = 18; // Offset from xPos for sliders

    // Track which slider is being dragged
    private int draggingSlider = -1; // -1=none, 0=hue, 1=sat, 2=val, 3=alpha

    // Flag to indicate if the key is explicitly set
    private final boolean explicitKey;

    public ColorOption(String label, Supplier<Integer> getter, Consumer<Integer> setter) {
        this.label = label;
        this.key = label;
        this.getter = getter;
        this.setter = setter;
        updateFromColor();
        this.explicitKey = false;
    }

    // New constructor with explicit config key
    public ColorOption(String configKey, String label, Supplier<Integer> getter, Consumer<Integer> setter) {
        this.label = label;
        this.key = configKey != null ? configKey : label;
        this.getter = getter;
        this.setter = setter;
        updateFromColor();
        this.explicitKey = configKey != null;
    }

    private void updateFromColor() {
        float[] hsva = GUIColors.toHSVA(getter.get());
        this.hue = hsva[0];
        this.saturation = hsva[1];
        this.value = hsva[2];
        this.alpha = hsva[3];
    }

    private void applyColor() {
        int color = GUIColors.fromHSVA(hue, saturation, value, alpha);
        setter.accept(color);
    }

    @Override
    public int getHeight() {
        return expanded ? 20 + 4 * (SLIDER_HEIGHT + SLIDER_MARGIN) + 8 : 20;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        this.xPos = x;
        this.yPos = y;

        int currentColor = getter.get();

        // Draw label
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer,
            Text.literal(label),
            x + 5, y + 5, GUIColors.text);

        // Draw expand/collapse indicator
        Text expandLabel = Text.literal(expanded ? "-" : "+");
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, expandLabel, x + 132, y + 5, GUIColors.text);

        // Draw color preview box (positioned to reach the end, similar to tickbox)
        int previewX = x + 113;
        int previewY = y + 3;
        CatUI.rect(previewX, previewY, previewX + COLOR_PREVIEW_SIZE, previewY + COLOR_PREVIEW_SIZE, currentColor);
        ctx.drawStrokedRectangle(previewX, previewY, COLOR_PREVIEW_SIZE, COLOR_PREVIEW_SIZE, 0x99ffffff);

        if (expanded) {
            int sliderX = x + SLIDER_X_OFFSET;
            int sliderY = y + 20;

            // Hue slider (rainbow gradient)
            renderHueSlider(ctx, sliderX, sliderY);
            sliderY += SLIDER_HEIGHT + SLIDER_MARGIN;

            // Saturation slider
            renderSaturationSlider(ctx, sliderX, sliderY);
            sliderY += SLIDER_HEIGHT + SLIDER_MARGIN;

            // Value/Brightness slider
            renderValueSlider(ctx, sliderX, sliderY);
            sliderY += SLIDER_HEIGHT + SLIDER_MARGIN;

            // Alpha slider
            renderAlphaSlider(ctx, sliderX, sliderY);
        }
    }

    private void renderHueSlider(DrawContext ctx, int x, int y) {
        // Draw rainbow hue bar
        int segments = 12;
        int segWidth = SLIDER_WIDTH / segments;
        for (int i = 0; i < segments; i++) {
            int color1 = GUIColors.fromHSVA((i * 360f / segments), 1, 1, 1);
            int color2 = GUIColors.fromHSVA(((i + 1) * 360f / segments), 1, 1, 1);
            int segX = x + i * segWidth;
            // Simple fill with first color (gradient would be better but this works)
            CatUI.rect(segX, y, segX + segWidth + 1, y + SLIDER_HEIGHT, color1);
        }

        // Draw handle
        int handleX = x + (int)(hue / 360f * SLIDER_WIDTH);
        drawHandle(ctx, handleX, y);

        // Label
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, Text.literal("H"), x - 10, y, GUIColors.text);
    }

    private void renderSaturationSlider(DrawContext ctx, int x, int y) {
        // Draw saturation gradient (white to full color)
        int fullColor = GUIColors.fromHSVA(hue, 1, value, 1);
        int lowColor = GUIColors.fromHSVA(hue, 0, value, 1);

        // Simple gradient approximation
        int segments = 10;
        int segWidth = SLIDER_WIDTH / segments;
        for (int i = 0; i < segments; i++) {
            float sat = i / (float) segments;
            int color = GUIColors.fromHSVA(hue, sat, value, 1);
            int segX = x + i * segWidth;
            CatUI.rect(segX, y, segX + segWidth + 1, y + SLIDER_HEIGHT, color);
        }

        // Draw handle
        int handleX = x + (int)(saturation * SLIDER_WIDTH);
        drawHandle(ctx, handleX, y);

        // Label
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, Text.literal("S"), x - 10, y, GUIColors.text);
    }

    private void renderValueSlider(DrawContext ctx, int x, int y) {
        // Draw value gradient (black to full color)
        int segments = 10;
        int segWidth = SLIDER_WIDTH / segments;
        for (int i = 0; i < segments; i++) {
            float val = i / (float) segments;
            int color = GUIColors.fromHSVA(hue, saturation, val, 1);
            int segX = x + i * segWidth;
            CatUI.rect(segX, y, segX + segWidth + 1, y + SLIDER_HEIGHT, color);
        }

        // Draw handle
        int handleX = x + (int)(value * SLIDER_WIDTH);
        drawHandle(ctx, handleX, y);

        // Label
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, Text.literal("V"), x - 10, y, GUIColors.text);
    }

    private void renderAlphaSlider(DrawContext ctx, int x, int y) {
        // Draw checkerboard pattern for alpha visualization
        int checkSize = 4;
        for (int i = 0; i < SLIDER_WIDTH / checkSize; i++) {
            for (int j = 0; j < SLIDER_HEIGHT / checkSize; j++) {
                int color = ((i + j) % 2 == 0) ? 0xffffffff : 0xff888888;
                int cx = x + i * checkSize;
                int cy = y + j * checkSize;
                CatUI.rect(cx, cy, Math.min(cx + checkSize, x + SLIDER_WIDTH), Math.min(cy + checkSize, y + SLIDER_HEIGHT), color);
            }
        }

        // Draw alpha gradient overlay
        int segments = 10;
        int segWidth = SLIDER_WIDTH / segments;
        for (int i = 0; i < segments; i++) {
            float a = i / (float) segments;
            int color = GUIColors.fromHSVA(hue, saturation, value, a);
            int segX = x + i * segWidth;
            CatUI.rect(segX, y, segX + segWidth + 1, y + SLIDER_HEIGHT, color);
        }

        // Draw handle
        int handleX = x + (int)(alpha * SLIDER_WIDTH);
        drawHandle(ctx, handleX, y);

        // Label
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, Text.literal("A"), x - 10, y, GUIColors.text);
    }

    private void drawHandle(DrawContext ctx, int x, int y) {
        int handleWidth = 4;
        int handleHeight = SLIDER_HEIGHT + 4;
        int handleY = y - 2;
        CatUI.rect(x - handleWidth / 2, handleY, x + handleWidth / 2, handleY + handleHeight, 0xffffffff);
        ctx.drawStrokedRectangle(x - handleWidth / 2, handleY, handleWidth, handleHeight, 0xff000000);
    }

    @Override
    public boolean handleClick(double x, double y, int mouseBtn, int clickType) {
        if (clickType == GLFW.GLFW_RELEASE) {
            draggingSlider = -1;
            return false;
        }

        if (clickType != GLFW.GLFW_PRESS) {
            return false;
        }

        // Check if clicking on expand/collapse area or color preview
        if (Utils.isInside(xPos, yPos, xPos + 140, yPos + 20, x, y)) {
            if (mouseBtn == GLFW.GLFW_MOUSE_BUTTON_LEFT || mouseBtn == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                expanded = !expanded;
                updateFromColor();
                return true;
            }
        }

        if (!expanded) return false;

        int sliderX = xPos + SLIDER_X_OFFSET;
        int sliderY = yPos + 20;

        // Check hue slider
        if (Utils.isInside(sliderX, sliderY, sliderX + SLIDER_WIDTH, sliderY + SLIDER_HEIGHT, x, y)) {
            draggingSlider = 0;
            updateHueFromMouse(x);
            return true;
        }
        sliderY += SLIDER_HEIGHT + SLIDER_MARGIN;

        // Check saturation slider
        if (Utils.isInside(sliderX, sliderY, sliderX + SLIDER_WIDTH, sliderY + SLIDER_HEIGHT, x, y)) {
            draggingSlider = 1;
            updateSaturationFromMouse(x);
            return true;
        }
        sliderY += SLIDER_HEIGHT + SLIDER_MARGIN;

        // Check value slider
        if (Utils.isInside(sliderX, sliderY, sliderX + SLIDER_WIDTH, sliderY + SLIDER_HEIGHT, x, y)) {
            draggingSlider = 2;
            updateValueFromMouse(x);
            return true;
        }
        sliderY += SLIDER_HEIGHT + SLIDER_MARGIN;

        // Check alpha slider
        if (Utils.isInside(sliderX, sliderY, sliderX + SLIDER_WIDTH, sliderY + SLIDER_HEIGHT, x, y)) {
            draggingSlider = 3;
            updateAlphaFromMouse(x);
            return true;
        }

        return false;
    }

    @Override
    public void handleMove(double x, double y) {
        if (draggingSlider == -1) return;

        switch (draggingSlider) {
            case 0 -> updateHueFromMouse(x);
            case 1 -> updateSaturationFromMouse(x);
            case 2 -> updateValueFromMouse(x);
            case 3 -> updateAlphaFromMouse(x);
        }
    }

    private void updateHueFromMouse(double mouseX) {
        int sliderX = xPos + SLIDER_X_OFFSET;
        float percent = (float) Math.max(0, Math.min(1, (mouseX - sliderX) / SLIDER_WIDTH));
        hue = percent * 360f;
        applyColor();
    }

    private void updateSaturationFromMouse(double mouseX) {
        int sliderX = xPos + SLIDER_X_OFFSET;
        float percent = (float) Math.max(0, Math.min(1, (mouseX - sliderX) / SLIDER_WIDTH));
        saturation = percent;
        applyColor();
    }

    private void updateValueFromMouse(double mouseX) {
        int sliderX = xPos + SLIDER_X_OFFSET;
        float percent = (float) Math.max(0, Math.min(1, (mouseX - sliderX) / SLIDER_WIDTH));
        value = percent;
        applyColor();
    }

    private void updateAlphaFromMouse(double mouseX) {
        int sliderX = xPos + SLIDER_X_OFFSET;
        float percent = (float) Math.max(0, Math.min(1, (mouseX - sliderX) / SLIDER_WIDTH));
        alpha = percent;
        applyColor();
    }

    @Override
    public String getConfigKey() { return key; }

    @Override
    public boolean isConfigKeyExplicit() { return explicitKey; }
}
