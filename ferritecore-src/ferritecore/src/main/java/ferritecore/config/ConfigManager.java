package malte0811.ferritecore.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ConfigManager {
    private static ConfigManager instance;

    private Path configDir;
    private final Map<String, Config> configs = new LinkedHashMap<>();

    private ConfigManager() {}

    public static synchronized ConfigManager getInstance() {
        if (instance == null) instance = new ConfigManager();
        return instance;
    }

    public static synchronized void init(File modConfigDir) {
        var mgr = getInstance();
        mgr.configDir = modConfigDir.toPath();
        try {
            java.nio.file.Files.createDirectories(mgr.configDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create config directory: " + mgr.configDir, e);
        }
    }

    public Path getConfigDir() {
        return configDir;
    }

    public synchronized Config createConfig(String name) {
        return configs.computeIfAbsent(name, n -> new Config(n, configDir.resolve(n + ".json")));
    }

    public synchronized Config getConfig(String name) {
        return configs.get(name);
    }

    public synchronized Collection<Config> getAllConfigs() {
        return configs.values();
    }

    public synchronized void loadAll() throws IOException {
        for (Config cfg : configs.values()) cfg.load();
    }

    public synchronized void saveAll() throws IOException {
        for (Config cfg : configs.values()) cfg.save();
    }

    public synchronized void exportConfig(String name, Path target) throws IOException {
        Config cfg = configs.get(name);
        if (cfg == null) throw new IOException("Config not found: " + name);
        cfg.exportTo(target);
    }

    public synchronized void importConfig(String name, Path source) throws IOException {
        Config cfg = configs.get(name);
        if (cfg == null) cfg = createConfig(name);
        cfg.importFrom(source);
        cfg.save();
    }
}

