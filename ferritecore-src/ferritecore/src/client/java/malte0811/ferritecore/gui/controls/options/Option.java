package malte0811.ferritecore.gui.controls.options;

import net.minecraft.client.gui.DrawContext;

public interface Option {
    int getHeight();
    void render(DrawContext ctx, int x, int y);
    boolean handleClick(double x, double y, int mouseBtn, int clickType);
    void handleMove(double x, double y);

    // Keyboard handling for textbox input
    default boolean handleKeyPress(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    default boolean handleCharTyped(char chr, int modifiers) {
        return false;
    }

    /**
     * Optional stable key used for config persistence. Return null to fallback to index-based key.
     */
    default String getConfigKey() { return null; }

    /**
     * Returns true if the config key was explicitly provided by the option implementation/constructor.
     * If false, the config system should fall back to a stable index-based key.
     */
    default boolean isConfigKeyExplicit() { return false; }
}