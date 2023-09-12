package com.theomenden.bismuth.caching.strategies;

import java.util.Arrays;

public final class ColorSlice extends BaseSlice {
    public int[] data;

    public ColorSlice(int size, int salt) {
        super(size, salt);
        final int cubedSize = size * size * size;
        this.data = new int[cubedSize];
    }

    public void invalidateCacheData() {
        Arrays.fill(this.data, 0);
    }
}