package com.theomenden.bismuth.utils;

import java.util.stream.IntStream;

public final class LCGUtils {
    private LCGUtils() {}

    final static int mask = (1 << 31) - 1;

    private static final int lower = 214_013;
    private static final int upper = 2_531_011;

    public static int generateLCG(int input, int seed) {
        var proccessedSeed = (input ^ seed) + seed ;
        var lcg = lower * proccessedSeed + upper;
        return lcg >> 8;
    }
}
