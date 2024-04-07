package io.github.lightrailpassenger.sausage;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import io.github.lightrailpassenger.sausage.constants.SettingKeys;

public class Settings {
    private final File file;
    private final Properties prop;
    private final List<SettingChangeListener> listeners;

    public Settings(File file) {
        this.file = file;
        this.prop = new Properties();
        this.listeners = new ArrayList<>();

        try (
            InputStream is = new FileInputStream(this.file)
        ) {
            this.prop.load(is);
        } catch (IOException ex) {
            try {
                this.file.createNewFile();
            } catch (IOException ex2) {
                // pass, TODO: Should we log it?
            }
        }
    }

    public void addListener(SettingChangeListener listener) {
        this.listeners.add(listener);
    }

    public void save() throws IOException {
        try (
            OutputStream os = new FileOutputStream(this.file)
        ) {
            this.prop.store(os, "");
        } catch (IOException ex) {
            throw ex;
        }
    }

    public String getProperty(SettingKeys key) {
        return this.prop.getProperty(key.toString());
    }

    public String getProperty(SettingKeys key, String defaultValue) {
        return this.prop.getProperty(key.toString(), defaultValue);
    }

    public int getInt(SettingKeys key, int defaultValue) {
        try {
            return Integer.parseInt(this.getProperty(key));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public <T> T get(SettingKeys key, Coercer<T> coercer) {
        String keyStr = key.toString();

        return coercer.coerce(this.prop.getProperty(keyStr), keyStr);
    }

    public void setProperty(SettingKeys key, Object value) {
        this.prop.setProperty(key.toString(), value.toString());

        SettingChangeEvent event = new SettingChangeEvent(this, key, value);

        for (SettingChangeListener listener: this.listeners) {
            listener.settingChanged(event);
        }
    }
}
