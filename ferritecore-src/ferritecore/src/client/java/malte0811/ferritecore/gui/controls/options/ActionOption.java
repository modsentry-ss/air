package malte0811.ferritecore.gui.controls.options;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.CatUI;
import malte0811.ferritecore.gui.GUIColors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ActionOption implements Option {
    private final String label;
    private final Runnable action;
    private int xPos, yPos;
    private final String key;
    private final boolean explicitKey;

    public ActionOption(String label, Runnable action) {
        this.label = label;
        this.action = action;
        this.key = label;
        this.explicitKey = false;
    }

    public ActionOption(String configKey, String label, Runnable action) {
        this.label = label;
        this.action = action;
        this.key = configKey != null ? configKey : label;
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

        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, Text.literal(label), x + 5, y + 5, GUIColors.text);

        // Draw a simple button on the right
        int btnX = x + 110;
        int btnY = y + 4;
        int btnW = 40;
        int btnH = 12;
        CatUI.rect(btnX, btnY, btnX + btnW, btnY + btnH, GUIColors.accent);
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, Text.literal("Run"), btnX + 8, btnY + 2, GUIColors.text);
    }

    @Override
    public boolean handleClick(double mx, double my, int mouseBtn, int clickType) {
        if (clickType != org.lwjgl.glfw.GLFW.GLFW_PRESS) return false;
        int btnX = xPos + 110;
        int btnY = yPos + 4;
        int btnW = 40;
        int btnH = 12;
        if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
            try { action.run(); } catch (Exception ignored) {}
            return true;
        }
        return false;
    }

    @Override
    public void handleMove(double x, double y) { }

    @Override
    public String getConfigKey() { return key; }

    @Override
    public boolean isConfigKeyExplicit() { return explicitKey; }
}
