package com.theomenden.bismuth.utils;

import lombok.NoArgsConstructor;

import java.util.random.RandomGenerator;

@NoArgsConstructor
public final class MathUtils {
    public static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    public static final float PI2 = (float) (Math.PI * 2);
    public static final int ALPHA = 255 << 24;
    public static final float INV_255 = 1.0f/255.0f;
    public static final float INV_16F = 1.0f/16.0f;
    public static final float INV_50F = 1.0f/50.0f;
    public static final double INV_16 = 1.0/16.0D;
}
