package com.theomenden.bismuth.models.records;

import net.minecraft.client.renderer.BiomeColors;

public record BiomeColorTypes(int grass, int water, int foliage) {
    public static final BiomeColorTypes INSTANCE = new BiomeColorTypes(0,1,2);
    public static final int FIRST = INSTANCE.grass();
    public static final int LAST = INSTANCE.foliage();
}
