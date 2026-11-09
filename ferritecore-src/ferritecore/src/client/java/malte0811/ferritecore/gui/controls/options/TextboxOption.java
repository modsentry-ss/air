package malte0811.ferritecore.gui.controls.options;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.GUIColors;
import malte0811.ferritecore.gui.controls.Textbox;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TextboxOption implements Option {
    private final Textbox textbox;
    private int xPos, yPos;
    private final String label;
    private final String key;
    private final boolean isPassword;
    private final Supplier<String> getter;
    private final Consumer<String> setter;
    private final boolean explicitKey;

    public TextboxOption(String label, String defaultValue, boolean isPassword, @Nullable Consumer<String> onChange) {
        this.label = label;
        this.key = label;
        this.isPassword = isPassword;
        this.getter = null;
        this.setter = null;
        this.textbox = new Textbox(defaultValue,
            () -> xPos + 5,
            () -> yPos + 12,
            onChange
        );
        this.explicitKey = false;
    }

    // New constructor with explicit config key
    public TextboxOption(String configKey, String label, String defaultValue, boolean isPassword, @Nullable Consumer<String> onChange) {
        this.label = label;
        this.key = configKey != null ? configKey : label;
        this.isPassword = isPassword;
        this.getter = null;
        this.setter = null;
        this.textbox = new Textbox(defaultValue,
            () -> xPos + 5,
            () -> yPos + 12,
            onChange
        );
        this.explicitKey = configKey != null;
    }

    /**
     * Creates a TextboxOption with getter/setter pattern for cleaner module integration.
     */
    public TextboxOption(String label, Supplier<String> getter, Consumer<String> setter) {
        this.label = label;
        this.key = label;
        this.isPassword = false;
        this.getter = getter;
        this.setter = setter;
        this.textbox = new Textbox(getter.get(),
            () -> xPos + 5,
            () -> yPos + 12,
            setter
        );
        this.explicitKey = false;
    }

    // New constructor with explicit config key
    public TextboxOption(String configKey, String label, Supplier<String> getter, Consumer<String> setter) {
        this.label = label;
        this.key = configKey != null ? configKey : label;
        this.isPassword = false;
        this.getter = getter;
        this.setter = setter;
        this.textbox = new Textbox(getter.get(),
            () -> xPos + 5,
            () -> yPos + 12,
            setter
        );
        this.explicitKey = configKey != null;
    }

    @Override
    public int getHeight() {
        return 32;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        this.xPos = x;
        this.yPos = y;

        // Sync textbox value from getter if not focused
        if (getter != null && !textbox.isFocused()) {
            textbox.value = getter.get();
        }

        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer,
            Text.literal(label),
            x + 5, y + 2, GUIColors.text);

        // If password mode, temporarily mask the value for rendering
        if (isPassword) {
            String realValue = textbox.value;
            textbox.value = "*".repeat(realValue.length());
            textbox.render(ctx);
            textbox.value = realValue;
        } else {
            textbox.render(ctx);
        }
    }

    @Override
    public boolean handleClick(double x, double y, int mouseBtn, int clickType) {
        // Only handle on press, not release
        if (clickType == org.lwjgl.glfw.GLFW.GLFW_PRESS) {
            textbox.handleClick(x, y, mouseBtn);
        }
        return textbox.isFocused();
    }

    @Override
    public void handleMove(double x, double y) {
        // Textbox doesn't need move handling
    }

    @Override
    public boolean handleKeyPress(int keyCode, int scanCode, int modifiers) {
        return textbox.handleKey(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean handleCharTyped(char character, int modifiers) {
        return textbox.handleChar(character);
    }

    public boolean isFocused() {
        return textbox.isFocused();
    }

    public String getValue() {
        return textbox.getValue();
    }

    public Textbox getTextbox() {
        return textbox;
    }

    @Override
    public String getConfigKey() { return key; }

    @Override
    public boolean isConfigKeyExplicit() { return explicitKey; }
}
