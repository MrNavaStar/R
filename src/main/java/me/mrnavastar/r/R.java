package me.mrnavastar.r;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class R {

    private static boolean oldJavaCompat = false;
    static {
        try {
            Field.class.getDeclaredField("modifiers");
            oldJavaCompat = true;
        } catch (NoSuchFieldException ignore) {}
    }

    private static final ConcurrentHashMap<String, Field> fieldCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Method> methodCache = new ConcurrentHashMap<>();

    private final Object instance;
    private final Class<?> clazz;

    public R(Object instance) {
        this.instance = instance;
        clazz = instance.getClass();
    }

    public R(Class<?> clazz) {
        instance = null;
        this.clazz = clazz;
    }

    /**
     * Create an instance of {@link R}. Can be used for static or non-static actions
     */
    public static R of(Object instance) {
        return new R(instance);
    }

    /**
     * Create an instance of {@link R} that can only be used for static actions
     */
    public static R of(Class<?> clazz) {
        return new R(clazz);
    }

    /**
     * Create an instance of {@link R} from a class name or a field in this instance
     */
    public R of(String name) {
        try {
            return R.of(Class.forName(name));
        } catch (ClassNotFoundException ignore) {}
        try {
            return R.of(findField(name, clazz).get(instance));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // Search super classes for field
    private Field findField(String name, Class<?> clazz) {
        String cacheKey = clazz.getName() + name;

        Field cached = fieldCache.get(cacheKey);
        if (cached != null) return cached;

        Class<?> current = clazz;
        while (current != null) {
            try {
                Field f = current.getDeclaredField(name);
                f.setAccessible(true);
                fieldCache.put(cacheKey, f);
                return f;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }

        throw new RuntimeException("no field with name: " + name);
    }


    // Search super classes for methods
    private Method findMethod(String name, Class<?> clazz, Class<?>[] argTypes) {
        StringBuilder keyBuilder = new StringBuilder(clazz.getName()).append(name);
        for (Class<?> arg : argTypes) keyBuilder.append(":").append(arg.getName());
        String cacheKey = keyBuilder.toString();

        Method cached = methodCache.get(cacheKey);
        if (cached != null) return cached;

        Class<?> current = clazz;
        while (current != null) {
            try {
                Method m = current.getDeclaredMethod(name, argTypes);
                m.setAccessible(true);
                methodCache.put(cacheKey, m);
                return m;
            } catch (NoSuchMethodException ignored) {
                current = current.getSuperclass();
            }
        }

        throw new RuntimeException("no method with name: " + name + " and args: " + Arrays.toString(argTypes));
    }


    /**
     * Get the value of a field. Can be private or static
     */
    public <T> T get(String name, Class<T> type) {
        try {
            return type.cast(findField(name, clazz).get(instance));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the value of a field. Can be private, final, or static
     */
    public R set(String name, Object value) {
        try {
            Field toSet = findField(name, clazz);
            if (oldJavaCompat) {
                Field modifiersField = findField("modifiers", toSet.getClass());
                modifiersField.setInt(toSet, toSet.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
            }
            toSet.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    //TODO: The way proxies are implemented means you can't call a function that takes a proxy as an arg (but maybe that doesn't matter?)
    private Object callAndReturn(String name, Object... args) {
        try {
            Class<?>[] classes = Arrays.stream(args).map(object -> {
                if (object instanceof Proxy) return object.getClass().getInterfaces()[0];
                return object.getClass();
            }).toArray(Class[]::new);
            return findMethod(name, clazz, classes).invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invoke a function with a return type
     */
    public <T> T call(String name, Class<T> returnType, Object... args) {
        Object returnVal = callAndReturn(name, args);
        if (returnVal == null || returnType == null) return null;
        return returnType.cast(returnVal);
    }

    /**
     * Invoke a function with no return type
     */
    public R call(String name, Object... args) {
        call(name, null, args);
        return this;
    }

    /**
     * Get a list of the generic type params of a class
     */
    public Class<?>[] generics() {
        if (clazz.isEnum()) return new Class[]{};   // Enums cant have generics

        Type generic = clazz.getGenericSuperclass();
        if (generic instanceof ParameterizedType) {
            return Arrays.stream(((ParameterizedType) generic).getActualTypeArguments()).map(t -> {
                try {
                    return Class.forName(t.getTypeName());
                } catch (ClassNotFoundException e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toArray(Class[]::new);
        }
        return new Class[]{};
    }

    /**
     * Instantiates an object that implements the passed in interface
     */
    public Object implement(Class<?> iface) {
        return Proxy.newProxyInstance(R.class.getClassLoader(), new Class[]{iface}, (proxy, method, args) -> callAndReturn(method.getName(), args));
    }

    /**
     * Instantiates an object that implements the passed in interface class name
     */
    public Object implement(String iface) {
        try {
            return implement(Class.forName(iface));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}