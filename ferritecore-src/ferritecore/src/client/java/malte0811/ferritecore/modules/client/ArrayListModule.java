package malte0811.ferritecore.modules.client;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.controls.options.ColorOption;
import malte0811.ferritecore.gui.controls.options.DropdownOption;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.modules.Module;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.List;

public class ArrayListModule extends ToggleModule {
    @Override public String getId() { return "arraylist"; }
    @Override public String getName() { return "ArrayList"; }

    // Customizable options
    private int moduleColor = 0xff7287fd;  // Default blue color
    private int sortMode = 0;  // 0 = by length (longest first), 1 = alphabetically

    private static final String[] SORT_OPTIONS = {"By Length", "Alphabetical"};

    @Override
    public List<Option> getOptions() {
        return List.of(
            new ColorOption("module_color", "Module Color",
                () -> moduleColor,
                color -> moduleColor = color),
            new DropdownOption("sort_mode", "Sort Mode", SORT_OPTIONS,
                () -> sortMode,
                mode -> sortMode = mode)
        );
    }

    @Override
    public void onRender(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;

        var offsetY = mc.textRenderer.fontHeight * 6;

        // Get and sort enabled modules
        List<Module> enabledModules = FerriteCore.moduleManager.getAllModules().stream()
            .filter(Module::isEnabled)
            .sorted(getComparator())
            .toList();

        for (Module module : enabledModules) {
            text(ctx, 2, offsetY, Text.of(module.getName()));
            offsetY += mc.textRenderer.fontHeight;
        }
    }

    private Comparator<Module> getComparator() {
        if (sortMode == 1) {
            // Alphabetical
            return Comparator.comparing(Module::getName);
        } else {
            // By length (longest first)
            return (a, b) -> Integer.compare(b.getName().length(), a.getName().length());
        }
    }

    public static void rect(DrawContext ctx, int x, int y, int x2, int y2, int alphaRGBHex) {
        ctx.fill(x, y, x2, y2, alphaRGBHex);
    }

    public void text(DrawContext ctx, int x, int y, Text text) {
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, text, x, y, moduleColor);
    }
}
