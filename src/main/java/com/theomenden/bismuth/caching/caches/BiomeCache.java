package com.theomenden.bismuth.caching.caches;

import com.theomenden.bismuth.caching.strategies.BiomeSlice;
import com.theomenden.bismuth.caching.strategies.SliceCacheStrategy;

public final class BiomeCache extends SliceCacheStrategy<BiomeSlice> {
    public BiomeCache(int count) {
        super(count);
    }

    @Override
    public BiomeSlice createSlice(int size, int salt) {
        return new BiomeSlice(size, salt);
    }
}
