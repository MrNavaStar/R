package me.mrnavastar.r;

import java.lang.reflect.*;
import java.util.Arrays;

public class R {

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

    public static R of(Object instance) {
        return new R(instance);
    }

    public static R of(Class<?> clazz) {
        return new R(clazz);
    }

    // Search super classes for field
    private Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
        if (clazz == null) throw new NoSuchFieldException();

        Field field;
        try {
            field = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            field = findField(clazz.getSuperclass(), name);
        }
        field.setAccessible(true);
        return field;
    }

    // Search super classes for methods
    private Method findMethod(Class<?> clazz, String name, Class<?>[] argTypes) throws NoSuchMethodException {
        if (clazz == null) throw new NoSuchMethodException();

        Method method;
        try {
            method = clazz.getDeclaredMethod(name, argTypes);
        } catch (NoSuchMethodException e) {
            method = findMethod(clazz.getSuperclass(), name, argTypes);
        }
        method.setAccessible(true);
        return method;
    }

    public <T> T get(String name, Class<T> type) {
        try {
            return type.cast(findField(clazz, name).get(instance));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(String name, Object value) {
        try {
            findField(clazz, name).set(instance, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T call(String name, Class<T> returnType, Object... args) {
        try {
            Class<?>[] classes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
            Object returnVal = findMethod(clazz, name, classes).invoke(instance, args);
            if (returnVal == null || returnType == null) return null;
            return returnType.cast(returnVal);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public R call(String name, Object... args) {
        call(name, null, args);
        return this;
    }

    public Class<?>[] generics() {
        Type generic = clazz.getGenericSuperclass();
        if (generic instanceof ParameterizedType) {
            return Arrays.stream(((ParameterizedType) generic).getActualTypeArguments()).map(t -> {
                try {
                    return Class.forName(t.getTypeName());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).toArray(Class[]::new);
        }
        return null;
    }
}