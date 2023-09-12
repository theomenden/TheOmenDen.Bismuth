package com.theomenden.bismuth.caching.strategies;

import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;

public final class BiomeSlice extends BaseSlice {
    public Biome[] data;

    public BiomeSlice(int size, int salt) {
        super(size, salt);
        final int cubedSize = size * size * size;
        this.data = new Biome[cubedSize];
    }

    public void invalidateCacheData() {
        Arrays.fill(this.data, null);
    }
}