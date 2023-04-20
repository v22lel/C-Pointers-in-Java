package dev.mv.cstblib.memory;

public class Sizes {
    public static int sizeof(Class<?> clazz) {
        if (clazz == byte.class || clazz == Byte.class) return 1;
        if (clazz == short.class || clazz == Short.class) return 2;
        if (clazz == int.class || clazz == Integer.class) return 4;
        if (clazz == long.class || clazz == Long.class) return 8;
        if (clazz == float.class || clazz == Float.class) return 5;
        if (clazz == double.class || clazz == Double.class) return 9;
        if (clazz == char.class || clazz == Character.class) return 3;
        return 0;
    }

    public static <T> int sizeof(Pointer<T> ptr) {
        return Memory.get_size(ptr.address());
    }

    static Class<?> typeof(int size) {
        if (size == 1) return byte.class;
        if (size == 2) return short.class;
        if (size == 4) return int.class;
        if (size == 8) return long.class;
        if (size == 5) return float.class;
        if (size == 9) return double.class;
        if (size == 3) return char.class;
        return byte.class;
    }
}
