package malte0811.ferritecore.modules.client;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.CatUI;
import malte0811.ferritecore.gui.GUIColors;
import malte0811.ferritecore.gui.controls.options.ActionOption;
import malte0811.ferritecore.gui.controls.options.ColorOption;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.gui.controls.options.TextboxOption;
import malte0811.ferritecore.config.ConfigManager;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ClickGUIModule extends ToggleModule {
    @Override public String getId() { return "clickgui"; }
    @Override public String getName() { return "ClickGUI"; }

    private CatUI catUI;
    public ClickGUIModule() {
        setBind(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    @Override
    public List<Option> getOptions() {
        List<Option> options = new ArrayList<>();

        options.add(new ColorOption("accent", "Accent",
            () -> GUIColors.accent,
            (color) -> {
                GUIColors.accent = color;
                // Update the transparent variant as well
                GUIColors.accentTransparent = GUIColors.withAlpha(color, 0xee);
            }));

        options.add(new ColorOption("header_bg", "Header BG",
            () -> GUIColors.headerBackground,
            (color) -> {
                GUIColors.headerBackground = color;
                GUIColors.headerBackgroundUnfocused = GUIColors.withAlpha(color, 0x99);
            }));

        options.add(new ColorOption("module_bg", "Module BG",
            () -> GUIColors.moduleBackground,
            (color) -> GUIColors.moduleBackground = color));

        options.add(new ColorOption("enabled_color", "Enabled",
            () -> GUIColors.moduleEnabled,
            (color) -> GUIColors.moduleEnabled = color));

        options.add(new ColorOption("disabled_color", "Disabled",
            () -> GUIColors.moduleDisabled,
            (color) -> GUIColors.moduleDisabled = color));

        options.add(new ColorOption("border_color", "Border",
            () -> GUIColors.border,
            (color) -> GUIColors.border = color));

        options.add(new RangeOption("rounding", "Rounding", 0.0f, 1.0f, 0.1f,
            () -> GUIColors.cornerRounding,
            (value) -> GUIColors.cornerRounding = value));

        // Export/Import controls
//        TextboxOption pathBox = new TextboxOption("config_filename", "Config filename", "modules.json", false, null);
//        options.add(pathBox);
//
//        options.add(new ActionOption("export_modules", "Export modules", () -> {
//            try {
//                String name = pathBox.getValue();
//                Path out = Path.of(System.getProperty("user.home"), name);
//                ConfigManager.getInstance().exportConfig("modules", out);
//            } catch (Exception ignored) {}
//        }));
//
//        options.add(new ActionOption("import_modules", "Import modules", () -> {
//            try {
//                String name = pathBox.getValue();
//                Path in = Path.of(System.getProperty("user.home"), name);
//                ConfigManager.getInstance().importConfig("modules", in);
//            } catch (Exception ignored) {}
//        }));

        return options;
    }

    private boolean previousState = false;

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        FerriteCore.showUI = isEnabled();

        if (previousState != isEnabled()) {
            if (FerriteCore.showUI) {
                FerriteCore.mc.mouse.unlockCursor();
            } else {
                FerriteCore.mc.mouse.lockCursor();
            }
        }
        previousState = isEnabled();
    }

    @Override
    public void onRender(DrawContext ctx) {
        if (FerriteCore.catUI == null)
            return;

        FerriteCore.catUI.draw(ctx);
    }
}
