package malte0811.ferritecore.config;

import com.google.gson.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Config {
    private final String name;
    private final Path file;
    private final Map<String, ConfigEntry<?>> entries = new LinkedHashMap<>();
    private JsonObject root = new JsonObject();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public Config(String name, Path file) {
        this.name = Objects.requireNonNull(name);
        this.file = Objects.requireNonNull(file);
    }

    public String getName() {
        return name;
    }

    public Path getFile() {
        return file;
    }

    public <T> void register(String key, Class<T> type, T defaultValue, Supplier<T> getter, Consumer<T> setter) {
        ConfigEntry<T> entry = new ConfigEntry<>(key, type, defaultValue, getter, setter);
        entries.put(key, entry);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        if (!contains(key)) return null;
        JsonElement el = root.get(key);
        if (el == null || el.isJsonNull()) return null;
        return GSON.fromJson(el, (Type) type);
    }

    public <T> void set(String key, T value) {
        root.add(key, GSON.toJsonTree(value));
    }

    public boolean contains(String key) {
        return root.has(key);
    }

    public synchronized void load() throws IOException {
        if (Files.exists(file)) {
            try {
                String content = Files.readString(file, StandardCharsets.UTF_8);
                root = JsonParser.parseString(content).getAsJsonObject();
            } catch (Exception e) {
                throw new IOException("Failed to parse config file: " + file, e);
            }
        } else {
            root = new JsonObject();
        }

        // Apply registered entries
        for (ConfigEntry<?> entry : entries.values()) {
            JsonElement el = root.get(entry.getKey());
            if (el == null || el.isJsonNull()) {
                // use default and write it back
                Object def = entry.getDefault();
                if (def != null) {
                    root.add(entry.getKey(), GSON.toJsonTree(def));
                    // call setter
                    try {
                        // unchecked cast, but registration enforces types
                        @SuppressWarnings("unchecked")
                        Consumer<Object> setter = (Consumer<Object>) entry.getSetter();
                        setter.accept(def);
                    } catch (Exception ignored) {
                    }
                }
            } else {
                try {
                    Object val = GSON.fromJson(el, entry.getType());
                    @SuppressWarnings("unchecked")
                    Consumer<Object> setter = (Consumer<Object>) entry.getSetter();
                    setter.accept(val);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public synchronized void save() throws IOException {
        // Gather values from getters
        for (ConfigEntry<?> entry : entries.values()) {
            try {
                Object val = entry.getGetter().get();
                root.add(entry.getKey(), GSON.toJsonTree(val));
            } catch (Exception ignored) {
            }
        }

        // atomic write
        Path tmp = file.resolveSibling(file.getFileName().toString() + ".tmp");
        Files.createDirectories(file.getParent());
        Files.writeString(tmp, GSON.toJson(root), StandardCharsets.UTF_8);
        Files.move(tmp, file, java.nio.file.StandardCopyOption.REPLACE_EXISTING, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
    }

    public synchronized void exportTo(Path target) throws IOException {
        Files.createDirectories(target.getParent());
        Files.writeString(target, GSON.toJson(root), StandardCharsets.UTF_8);
    }

    public synchronized void importFrom(Path source) throws IOException {
        if (!Files.exists(source)) throw new IOException("Source does not exist: " + source);
        String content = Files.readString(source, StandardCharsets.UTF_8);
        JsonObject imported = JsonParser.parseString(content).getAsJsonObject();
        this.root = imported;
        // Apply to registered entries
        for (ConfigEntry<?> entry : entries.values()) {
            JsonElement el = root.get(entry.getKey());
            if (el == null || el.isJsonNull()) continue;
            try {
                Object val = GSON.fromJson(el, entry.getType());
                @SuppressWarnings("unchecked")
                Consumer<Object> setter = (Consumer<Object>) entry.getSetter();
                setter.accept(val);
            } catch (Exception ignored) {
            }
        }
    }

    public Map<String, Object> asMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> e : root.entrySet()) {
            m.put(e.getKey(), GSON.fromJson(e.getValue(), Object.class));
        }
        return m;
    }
}

