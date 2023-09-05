package com.theomenden.bismuth.utils;

import java.util.random.RandomGenerator;

public final class MathUtils {
    public static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    public static final float PI2 = (float) (Math.PI * 2);
    public static final int ALPHA = 255 << 24;
    public static final float INV_255 = 1.0f/255.0f;
    public static final float INV_16F = 1.0f/16.0f;
    public static final float INV_12_92 = 1.0f/12.92f;
    public static final float INV_1_055 = 1.0f/1.055f;
    public static final float INV_50F = 1.0f/50.0f;
    public static final double INV_2_4 = 1.0/2.4;
    public static final double INV_16 = 1.0/16.0D;
    public static final int INV_16_INT = Math.floorDiv(1, 16);

    private MathUtils(){}

    public static int createLowerBitMask(int bitCount) {
        return (1 << bitCount) - 1;
    }

    public static int getLowerBits(int value, int bitCount) {
        return value & createLowerBitMask(bitCount);
    }
}
