package com.theomenden.bismuth.colors.mapping;

import com.mojang.blaze3d.platform.NativeImage;
import com.theomenden.bismuth.client.Bismuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LightMapping {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bismuth.MODID);

    private final NativeImage lightMap;

    public LightMapping(NativeImage lightMap) {
        this.lightMap = lightMap;
    }
    public int getBlockColorForLightLevel(int lightLevel, float flicker, float nightVision) {
        int width = lightMap.getWidth();
        int positionX = (int)(flicker * width) % width;

        if(positionX < 0) {
            positionX = -positionX;
        }

        return getPixelAtPositionWithGivenLightLevel(positionX, lightLevel + 16, nightVision);
    }

    public int getSkyLighting(int level, float ambience, float nightVision) {
        if(ambience < 0) {
            int posX = lightMap.getWidth() - 1;
            return getPixelAtPositionWithGivenLightLevel(posX, level, nightVision);
        }

        float scaledAmbience = ambience * (lightMap.getWidth() - 2);
        float scaledAmbienceModulated = scaledAmbience % 1.0f;

        int posX = (int)scaledAmbience;
        int light = getPixelAtPositionWithGivenLightLevel(posX, level, nightVision);
        boolean shouldBlendSkyLighting = Bismuth.configuration.shouldBlendSkyLight;

        if(shouldBlendSkyLighting
                && posX < lightMap.getWidth() - 2) {
            int rightLighting = getPixelAtPositionWithGivenLightLevel(posX + 1, level, nightVision);
            light = mergeColorsBasedOnNightVisionFactors(rightLighting, light, scaledAmbienceModulated);
        }
        return light;
    }

    private int getPixelAtPositionWithGivenLightLevel(int x, int y, float nightVision) {
        if(nightVision <=0.0f) {
            return lightMap.getPixelRGBA(x, y);
        }
        if(nightVision >=1.0f) {
            if (lightMap.getHeight() != 64) {
                return getRationalizedValue(x, y);
            }  else {
                return lightMap.getPixelRGBA(x, y +32);
            }
        }
        int normalColor = lightMap.getPixelRGBA(x, y);
        int nightVisionColor = (lightMap.getHeight() !=64)
                ? getRationalizedValue(x, y)
                : lightMap.getPixelRGBA(x, y +32);
        return mergeColorsBasedOnNightVisionFactors(normalColor, nightVisionColor, nightVision);
    }

    private int getRationalizedValue(int x, int y) {
        int color = lightMap.getPixelRGBA(x, y);
        int red = (color >>16) &0xff;
        int green = (color >>8) &0xff;
        int blue = color & 0xff;
        int scale = Math.max(red, Math.max(green, blue));
        int rationalizedValue;
        if (scale == 0) {
            rationalizedValue = 0x00ffffff;
        } else {
            int inverseScale = 255 / scale;
            rationalizedValue = 0xff000000 | (inverseScale * red) << 16 | (inverseScale * green) << 8 | (inverseScale * blue);
        }
        return rationalizedValue;
    }

    private int mergeColorsBasedOnNightVisionFactors(int color1, int color2, float nightVision) {
        float oneMinusAweight = 1 - nightVision;
        int cha, chb;
        int resolvedColor = 0xff000000;
        cha = ((color1 >> 16) & 0xff);
        chb = ((color2 >> 16) & 0xff);
        resolvedColor |= (int)(cha * nightVision + chb * oneMinusAweight) << 16;
        cha = ((color1 >> 8) & 0xff);
        chb = ((color2>> 8) & 0xff);
        resolvedColor |= (int)(cha * nightVision + chb * oneMinusAweight) << 8;
        cha = color1 & 0xff;
        chb = color2 & 0xff;
        resolvedColor |= (int)(cha * nightVision + chb * oneMinusAweight);
        return resolvedColor;
    }

}
