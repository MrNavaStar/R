package me.mrnavastar.r;

import java.util.Optional;

public class WrappedR {

    @FunctionalInterface
    public interface ErrorHandler {
        void accept(R r, Throwable e);
    }

    private final R r;
    private final ErrorHandler handler;

    public WrappedR(R r, ErrorHandler handler) {
        this.r = r;
        this.handler = handler;
    }
    
    public WrappedR noThrow() {
        return new WrappedR(r, (r, e) -> {});
    }

    public WrappedR handleError(WrappedR.ErrorHandler handler) {
        return new WrappedR(r, handler);
    }

    public Optional<Object> construct(Object... args) {
        try {
            return Optional.of(r.construct(args));
        } catch (Exception e) {
            handler.accept(r, e);
            return Optional.empty();
        }
    }

    /**
     * Get the value of a field. Can be private or static
     */
    public <T> Optional<T> get(String name, Class<T> type) {
        try {
            return Optional.of(r.get(name, type));
        } catch (RuntimeException e) {
            handler.accept(r, e);
            return Optional.empty();
        }
    }

    /**
     * Set the value of a field. Can be private, final, or static
     */
    public WrappedR set(String name, Object value) {
        try {
            return new WrappedR(r.set(name, value), handler);
        } catch (RuntimeException e) {
            handler.accept(r, e);
            return this;
        }
    }

    /**
     * Invoke a function with a return type
     */
    public <T> Optional<T> call(String name, Class<T> returnType, Object... args) {
        try {
            return Optional.of(r.call(name, returnType, args));
        } catch (RuntimeException e) {
            handler.accept(r, e);
            return Optional.empty();
        }
    }

    /**
     * Invoke a function with no return type
     */
    public WrappedR call(String name, Object... args) {
        try {
            return new WrappedR(r.call(name, args), handler);
        } catch (RuntimeException e) {
            handler.accept(r, e);
            return this;
        }
    }

    /**
     * Get a list of the generic type params of a class
     */
    public Optional<Class<?>[]> generics() {
        try {
            return Optional.of(r.generics());
        } catch (RuntimeException e) {
            handler.accept(r, e);
            return Optional.empty();
        }
    }
}
