package io.github.lightrailpassenger.sausage;

import java.util.EventListener;
import java.util.EventObject;

import io.github.lightrailpassenger.sausage.constants.SettingKeys;

public class SettingChangeEvent extends EventObject {
    private final SettingKeys key;
    private final Object value;

    public SettingChangeEvent(Settings settings, SettingKeys key, Object value) {
        super(settings);
        this.key = key;
        this.value = value;
    }

    public SettingKeys getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.value;
    }
}
