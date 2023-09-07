package com.theomenden.bismuth.caching.caches;

import com.theomenden.bismuth.caching.strategies.ColorSlice;
import com.theomenden.bismuth.caching.strategies.SliceCacheStrategy;

public class ColorCache extends SliceCacheStrategy<ColorSlice> {

    public ColorCache(int count) {
        super(count);
    }

    @Override
    public ColorSlice createSlice(int sliceSize, int salt) {
        return new ColorSlice(sliceSize, salt);
    }
}
