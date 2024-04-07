package io.github.lightrailpassenger.sausage;

import java.util.Map;
import java.util.HashMap;

import io.github.lightrailpassenger.sausage.utils.SettingUtil;

public class MapCoercer implements Coercer<Map<String, String[]>> {
    private final Map<String, Map<String, String[]>> cache;
    private final Map<String, String> missingValueMap;

    public MapCoercer() {
        this(new HashMap<>());
    }

    public MapCoercer(Map<String, String> missingValueMap) {
        this.cache = new HashMap<>();
        this.missingValueMap = missingValueMap;
    }

    @Override
    public Map<String, String[]> coerce(String value) {
        try {
            Map<String, String[]> result;

            if ((result = this.cache.get(value)) == null) {
                result = SettingUtil.parseValueAsMap(value);
                this.cache.put(value, result);
            }

            return result;
        } catch (Exception ex) {
            return new HashMap<>();
        }
    }

    @Override
    public String getMissingValue(String key) {
        return this.missingValueMap.get(key);
    }
}
