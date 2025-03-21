package me.mrnavastar.r;

public class QuietR {

    private final R r;

    public QuietR(R r) {
        this.r = r;
    }

    /**
     * Get the value of a field. Can be private or static
     */
    public <T> T get(String name, Class<T> type) {
        return r.get(name, type);
    }

    /**
     * Set the value of a field. Can be private, final, or static
     */
    public QuietR set(String name, Object value) {
        return new QuietR(r.set(name, value));
    }

    /**
     * Invoke a function with a return type
     */
    public <T> T call(String name, Class<T> returnType, Object... args) {
        return r.call(name, returnType, args);
    }

    /**
     * Invoke a function with no return type
     */
    public QuietR call(String name, Object... args) {
        return new QuietR(r.call(name, args));
    }

    /**
     * Get a list of the generic type params of a class
     */
    public Class<?>[] generics() {
        return r.generics();
    }
}
