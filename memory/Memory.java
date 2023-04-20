package dev.mv.cstblib.memory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Memory {
    private static byte[] data = new byte[0];
    private static int[] sizes = new int[0];
    private static final HashMap<Long, Integer> addrMapper = new HashMap<>();
    private static final HashMap<Integer, Long> sizeAddrMapper = new HashMap<>();
    private static final Random addrGen = new Random();

    static byte[] get(long addr) {
        try {
            int size = get_size(addr);
            byte[] ret = new byte[size];
            int idx = get_mem_addr_size_addr(addrMapper.get(addr));
            for (int i = 0; i < size; i++) {
                ret[i] = data[idx + i];
            }
            return ret;
        } catch (IndexOutOfBoundsException e) {
            throw new SIGSEGV();
        }
    }

    static void set(long addr, byte[] value) {
        if(get_size(addr) < value.length) {
            throw new SIGSEGV();
        }

        int idx = get_mem_addr_size_addr(addrMapper.get(addr));
        for (int i = 0; i < value.length; i++) {
            data[idx + i] = value[i];
        }
    }

    public static <T> Pointer<T> calloc(int amt, int size) {
        if (data.length < size * amt + data.length) {
            data = Arrays.copyOf(data, data.length + size * amt);
            sizes = Arrays.copyOf(sizes, sizes.length + size * amt);
        }

        long firstAddr = -1;
        for (int c = 0; c < amt; c++) {
            int count = 0;
            for (int i = 0; i < sizes.length; i++) {
                if(sizes[i] == 0) {
                    count++;
                    if(count >= size) {
                        int index = i - count + 1;
                        sizes[index] = size;
                        long addr = addrGen.nextLong();
                        while (addrMapper.containsKey(addr)) {
                            addr = addrGen.nextLong();
                        }
                        addrMapper.put(addr, index);
                        sizeAddrMapper.put(index, addr);
                        for (int j = 0; j < size; j++) {
                            int dataOff = j + get_mem_addr_size_addr(index);
                            data[dataOff] = Byte.MIN_VALUE;
                        }
                        if (firstAddr == -1) firstAddr = addr;
                    }
                }
            }
        }

        return new Pointer<>(firstAddr);
    }

    public static <T> Pointer<T> malloc(int amt, int size) {
        if (data.length < size * amt + data.length) {
            data = Arrays.copyOf(data, data.length + size * amt);
            sizes = Arrays.copyOf(sizes, sizes.length + size * amt);
        }

        long firstAddr = -1;
        for (int c = 0; c < amt; c++) {
            int count = 0;
            for (int i = 0; i < sizes.length; i++) {
                if(sizes[i] == 0) {
                    count++;
                    if(count >= size) {
                        int index = i - count + 1;
                        sizes[index] = size;
                        long addr = addrGen.nextLong();
                        while (addrMapper.containsKey(addr)) {
                            addr = addrGen.nextLong();
                        }
                        addrMapper.put(addr, index);
                        sizeAddrMapper.put(index, addr);
                        if (firstAddr == -1) firstAddr = addr;
                    }
                }
            }
        }

        return new Pointer<>(firstAddr);
    }

    public static <T> void free(Pointer<T> ptr) {
        if(!addrMapper.containsKey(ptr.address())) throw new SIGSEGV();
        int index = addrMapper.get(ptr.address());
        int size = sizes[index];
        for (int i = 0; i < size; i++) {
            sizes[index + i] = 0;
        }
        addrMapper.remove(ptr.address());
        sizeAddrMapper.remove(index);
    }

    public static void memcpy(long addr, long to) {
        if(!addrMapper.containsKey(addr)) throw new SIGSEGV();
        if(!addrMapper.containsKey(to)) throw new SIGSEGV();

        int dataOff = get_mem_addr_size_addr(addrMapper.get(addr));
        int toOff = get_mem_addr_size_addr(addrMapper.get(to));
        for (int i = 0; i < get_size(addr); i++) {
            data[toOff + i] = data[dataOff + i];
        }
    }

    static <T> Pointer<T> incr_ptr(Pointer<T> ptr, int amt) {
        if(!sizeAddrMapper.containsKey(addrMapper.get(ptr.address()) + amt)) {
            long addr = addrGen.nextLong();
            while (addrMapper.containsKey(addr)) {
                addr = addrGen.nextLong();
            }
            sizeAddrMapper.put(addrMapper.get(ptr.address()) + amt, addr);
            addrMapper.put(addr, addrMapper.get(ptr.address()) + amt);
        }
        return new Pointer<>(sizeAddrMapper.get(addrMapper.get(ptr.address()) + amt));
    }

    static int get_size(long addr) {
        try {
            return sizes[addrMapper.get(addr)];
        } catch (Exception e) {
            throw new SIGSEGV();
        }
    }

    private static int get_mem_addr_size_addr(int sizeAddr) {
        int dataIdx = 0;
        for (int i = 0; i < sizeAddr; i++) {
            dataIdx += sizes[i];
        }
        return dataIdx;
    }
}
