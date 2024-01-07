package io.github.lightrailpassenger.sausage;

public interface Coercer<T> {
    public T coerce(String value);
}
