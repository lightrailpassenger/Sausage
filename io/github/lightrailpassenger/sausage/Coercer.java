package io.github.lightrailpassenger.sausage;

public interface Coercer<T> {
    public T coerce(String value);
    public default String getMissingValue(String key) {
        return null;
    }
    public default T coerce(String value, String key) {
        String defaultValueFromKey = this.getMissingValue(key);

        return this.coerce(value == null ? defaultValueFromKey : value);
    }
}
