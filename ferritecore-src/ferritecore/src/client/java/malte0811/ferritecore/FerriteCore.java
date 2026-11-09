package malte0811.ferritecore;

import malte0811.ferritecore.modules.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import malte0811.ferritecore.gui.CatUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentHashMap;
import malte0811.ferritecore.config.ConfigManager;

public class FerriteCore implements ClientModInitializer {
    public static boolean destructed = false;
    public static boolean activated = true;

    public static long TICKS = 0;
    public static boolean showUI = false;
    public static CatUI catUI;

    public static ModuleManager moduleManager;
    public static ConcurrentHashMap<Long, Runnable> tickEndCallbacks = new ConcurrentHashMap<>();

    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static LivingEntity target;


    @Override
    public void onInitializeClient() {
        // Initialize config manager to use the Minecraft run directory's config/ferritecore folder
//        try {
//            File runDir = MinecraftClient.getInstance().runDirectory;
//            File tempDir = Files.createTempDirectory("e").toFile();
//            File cfgDir = new File(runDir, "config" + File.separator + "ferritecore");
//            ConfigManager.init(cfgDir);
//            // ensure a default config exists
//            ConfigManager.getInstance().createConfig("ferritecore");
//            // initialize module manager now that config manager is ready
            moduleManager = new ModuleManager();
//            ConfigManager.getInstance().loadAll();
//        } catch (Exception e) {
//            // ignore errors during config init to avoid breaking startup
//        }
        catUI = new CatUI();
    }

    // session unload ONLY: disables every module + shuts the ui. no file
    // operations, no jar swapping, no downloads — the decoy-swap that used to
    // live here (overwrite own jar with a real ferritecore release to hide the
    // client during screenshares) got removed on purpose.
    public static void destruct() {
        destructed = true;
        showUI = false;
        if (moduleManager != null) {
            for (var m : moduleManager.getAllModules()) {
                if (m.isEnabled()) m.setEnabled(false);
            }
        }
    }
}