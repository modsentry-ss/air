package malte0811.ferritecore.modules.client;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.controls.options.ColorOption;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.TextboxOption;
import malte0811.ferritecore.gui.controls.options.ToggleOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;

public class HUDModule extends ToggleModule {
    @Override public String getId() { return "hud"; }
    @Override public String getName() { return "HUD"; }

    // Customizable options
    private int gradientStart = 0x9A64C1;  // Starting color of logo gradient
    private int gradientEnd = 0x30004E;    // Ending color of logo gradient
    private String logoText = "Judgment Client";
    private boolean showFPS = true;
    private boolean showPing = true;
    private boolean showTPS = true;

    // TPS tracking
    private static long lastTickTime = 0;
    private static float currentTPS = 20.0f;
    private static int tickCount = 0;
    private static long tickTimeAccumulator = 0;

    @Override
    public List<Option> getOptions() {
        return List.of(
            new ColorOption("gradient_start", "Gradient Start",
                () -> gradientStart | 0xFF000000,
                color -> gradientStart = color & 0x00FFFFFF),
            new ColorOption("gradient_end", "Gradient End",
                () -> gradientEnd | 0xFF000000,
                color -> gradientEnd = color & 0x00FFFFFF),
            new TextboxOption("logo_text", "Logo Text",
                () -> logoText,
                text -> logoText = text),
            new ToggleOption("show_fps", this, () -> showFPS, v -> showFPS = v, "Show FPS"),
            new ToggleOption("show_ping", this, () -> showPing, v -> showPing = v, "Show Ping"),
            new ToggleOption("show_tps", this, () -> showTPS, v -> showTPS = v, "Show TPS")
        );
    }

    @Override
    public void onRender(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;

        text(ctx, 2, 5, getLogo());

        var offset = mc.textRenderer.fontHeight * 3;

        if (showFPS) {
            text(ctx, 2, offset, Text.of("FPS: " + mc.getCurrentFps()));
            offset += mc.textRenderer.fontHeight;
        }

        if (showPing) {
            text(ctx, 2, offset, Text.of("Ping: " + getPing(mc)));
            offset += mc.textRenderer.fontHeight;
        }

        if (showTPS) {
            text(ctx, 2, offset, Text.of("TPS: " + String.format("%.1f", currentTPS)));
        }
    }

    private Text getLogo() {
        if (logoText == null || logoText.isEmpty()) {
            return Text.literal("");
        }

        var text = MutableText.of(Text.literal("").getContent());
        int len = logoText.length();

        // Extract RGB components from start and end colors
        int startR = (gradientStart >> 16) & 0xFF;
        int startG = (gradientStart >> 8) & 0xFF;
        int startB = gradientStart & 0xFF;
        int endR = (gradientEnd >> 16) & 0xFF;
        int endG = (gradientEnd >> 8) & 0xFF;
        int endB = gradientEnd & 0xFF;

        for (int i = 0; i < len; i++) {
            float ratio = len > 1 ? (float) i / (len - 1) : 0;

            // Interpolate each color component
            int r = (int) (startR + ratio * (endR - startR));
            int g = (int) (startG + ratio * (endG - startG));
            int b = (int) (startB + ratio * (endB - startB));

            int color = (r << 16) | (g << 8) | b;
            text = text.append(Text.literal(String.valueOf(logoText.charAt(i)))
                .setStyle(Style.EMPTY.withColor(color).withBold(true)));
        }

        return text;
    }

    private int getPing(MinecraftClient mc) {
        if (mc.player == null) return 0;
        if (mc.getNetworkHandler() == null) return 0;
        if (mc.getNetworkHandler().getPlayerListEntry(mc.player.getName().getString()) == null) return 0;
        return Objects.requireNonNull(mc.getNetworkHandler().getPlayerListEntry(mc.player.getName().getString())).getLatency();
    }

    // Call this from game tick to track TPS
    public static void    onGameTick() {
        long currentTime = System.currentTimeMillis();
        if (lastTickTime != 0) {
            long delta = currentTime - lastTickTime;
            tickTimeAccumulator += delta;
            tickCount++;

            // Update TPS every 20 ticks (1 second at normal speed)
            if (tickCount >= 20) {
                float avgTickTime = (float) tickTimeAccumulator / tickCount;
                currentTPS = Math.min(20.0f, 1000.0f / avgTickTime);
                tickCount = 0;
                tickTimeAccumulator = 0;
            }
        }
        lastTickTime = currentTime;
    }

    public static void rect(DrawContext ctx, int x, int y, int x2, int y2, int alphaRGBHex) {
        ctx.fill(x, y, x2, y2, alphaRGBHex);
    }
    public static void text(DrawContext ctx, int x, int y, Text text) {
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, text, x, y, 0xffffffff);
    }
}
