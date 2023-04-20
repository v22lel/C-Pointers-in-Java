package dev.mv.cstblib.memory;

import java.nio.ByteBuffer;

public class Pointer<T> {
    private long address;
    private boolean containsPtr = false;

    public Pointer(long addr) {
        this.address = addr;
    }

    public long address() {
        return address;
    }

    public T deref() {
        if(containsPtr) {
            byte[] res = Memory.get(address);
            ByteBuffer wrapper = ByteBuffer.wrap(res);
            return (T) new Pointer<>(wrapper.getLong());
        }
        byte[] res = Memory.get(address);
        Class<T> type = (Class<T>) Sizes.typeof(res.length);
        ByteBuffer wrapper = ByteBuffer.wrap(res);
        Object ret = null;
        if (type == byte.class) ret = res[0];
        if (type == short.class) ret = wrapper.getShort();
        if (type == int.class) ret = wrapper.getInt();
        if (type == long.class) ret = wrapper.getLong();
        if (type == float.class) ret = wrapper.getFloat();
        if (type == double.class) ret = wrapper.getDouble();
        if (type == char.class) ret = wrapper.getChar();
        return (T) ret;
    }

    public Pointer<T> at(int index, T val) {
        return incr(index).set(val);
    }

    public Pointer<T> set(T t) {
        if(t instanceof Pointer<?> p) {
            containsPtr = true;
            ByteBuffer putter = ByteBuffer.allocate(Long.BYTES);
            putter.putLong(p.address());
            Memory.set(address(), putter.array());
            return this;
        } else {
            containsPtr = false;
        }
        ByteBuffer putter = ByteBuffer.allocate(Sizes.sizeof(t.getClass()));
        if (t instanceof Byte b) putter.put(b);
        if (t instanceof Short s) putter.putShort(s);
        if (t instanceof Integer i) putter.putInt(i);
        if (t instanceof Long l) putter.putLong(l);
        if (t instanceof Float f) putter.putFloat(f);
        if (t instanceof Double d) putter.putDouble(d);
        if (t instanceof Character c) putter.putChar(c);
        Memory.set(address(), putter.array());
        return this;
    }

    public Pointer<T> incr(int times) {
        return Memory.incr_ptr(this, times);
    }

    public String as_str(int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            builder.append(incr(i).deref());
        }

        return builder.toString();
    }

    public static <U> Pointer<U> ref(U o) {
        Pointer<U> ret = Memory.calloc(1, Sizes.sizeof(o.getClass()));
        ret.set(o);
        return ret;
    }
}
