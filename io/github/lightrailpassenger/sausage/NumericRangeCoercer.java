package io.github.lightrailpassenger.sausage;

public class NumericRangeCoercer implements Coercer<Integer> {
    private final int min;
    private final int max;
    private final int defaultValue;

    public NumericRangeCoercer(int min, int max, int defaultValue) {
        this.min = min;
        this.max = max;
        this.defaultValue = defaultValue;
    }

    @Override
    public Integer coerce(String value) {
        try {
            int parsed = Integer.parseInt(value);

            if (parsed <= max && parsed >= min) {
                return parsed;
            }

            return defaultValue;
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}
