package io.github.lightrailpassenger.sausage;

import java.util.EventListener;

interface SettingChangeListener extends EventListener {
    public void settingChanged(SettingChangeEvent ev);
}
