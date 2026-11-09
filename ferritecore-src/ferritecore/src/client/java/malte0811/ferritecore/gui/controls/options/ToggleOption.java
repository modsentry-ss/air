package malte0811.ferritecore.gui.controls.options;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.GUIColors;
import malte0811.ferritecore.gui.controls.Tickbox;
import malte0811.ferritecore.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ToggleOption implements Option {
    private final Module module;
    private final Tickbox tickbox;
    private int xPos, yPos;
    private final Supplier<Boolean> supplier;
    private final Consumer<Boolean> consumer;
    private final String label;
    private final String key;
    private final boolean explicitKey;

    // New constructor that accepts an explicit stable config key
    public ToggleOption(String configKey, Module module, Supplier<Boolean> supplier, Consumer<Boolean> consumer, String label) {
        this.supplier = supplier;
        this.consumer = consumer;
        this.label = label;
        this.key = configKey != null ? configKey : label;
        this.explicitKey = configKey != null;

        this.module = module;
        this.tickbox = new Tickbox(
                supplier.get(),
                () -> xPos + 125,  // Position tickbox on the right
                () -> yPos,
                (val) -> consumer.accept(val)
        );
    }

    public ToggleOption(Module module, Supplier<Boolean> supplier, Consumer<Boolean> consumer, String label) {
        this.supplier = supplier;
        this.consumer = consumer;
        this.label = label;
        this.key = label;
        this.explicitKey = false;

        this.module = module;
        this.tickbox = new Tickbox(
                supplier.get(),
                () -> xPos + 125,  // Position tickbox on the right
                () -> yPos,
                (val) -> consumer.accept(val)
        );
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        this.xPos = x;
        this.yPos = y;
        tickbox.status = supplier.get();

        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer,
                Text.literal(label),
                x + 5, y + 5, GUIColors.text);

        tickbox.render(ctx);
    }

    @Override
    public boolean handleClick(double x, double y, int mouseBtn, int clickType) {
        if (clickType == GLFW.GLFW_PRESS) {
            tickbox.handleClick(x, y, mouseBtn);
            consumer.accept(tickbox.status);
            return true;
        }
        return false;
    }

    @Override
    public void handleMove(double x, double y) {
        // Not needed for tickbox
    }

    @Override
    public String getConfigKey() {
        return key;
    }

    @Override
    public boolean isConfigKeyExplicit() { return explicitKey; }
}