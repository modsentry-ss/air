package malte0811.ferritecore.config;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ConfigEntry<T> {
    private final String key;
    private final Class<T> type;
    private final T defaultValue;
    private final Supplier<T> getter;
    private final Consumer<T> setter;

    public ConfigEntry(String key, Class<T> type, T defaultValue, Supplier<T> getter, Consumer<T> setter) {
        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
        this.getter = getter;
        this.setter = setter;
    }

    public String getKey() {
        return key;
    }

    public Class<T> getType() {
        return type;
    }

    public T getDefault() {
        return defaultValue;
    }

    public Supplier<T> getGetter() {
        return getter;
    }

    public Consumer<T> getSetter() {
        return setter;
    }
}

