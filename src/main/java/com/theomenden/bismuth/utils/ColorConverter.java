package com.theomenden.bismuth.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joml.Vector3f;

@Getter
@NoArgsConstructor
public final class ColorConverter {
    @Getter
    private static final int WHITE_RGB = 0xFFFFFF;
    @Getter
    private static final int WHITE_ARGB = 0xFFFFFFFF;
    @Getter
    private static final float[] WHITE_HSL = {0f, 0f, 1f};
    @Getter
    private static final String WHITE_HEX = "#FFFFFF";

    public static String rgbToHex(int rgb) {
        return String.format("#%06X", rgb & 0xFFFFFF);
    }

    public static int rgbToArgb(int rgb, int alpha) {
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }

    public static Vector3f createColorVector(int rgb) {
        float red = ((rgb >> 16) & 0xff) * MathUtils.INV_255;
        float green = ((rgb >> 8) & 0xff) * MathUtils.INV_255;
        float blue = (rgb & 0xff) * MathUtils.INV_255;

        return new Vector3f(red, green,blue);
    }

    public static float[] createColorFloatArray(int srgb) {
        return new float[] {
                (srgb >> 16 & 0xff) * MathUtils.INV_255,
                (srgb >> 8 & 0xff) * MathUtils.INV_255,
                (srgb & 0xff) * MathUtils.INV_255
        };
    }
}
