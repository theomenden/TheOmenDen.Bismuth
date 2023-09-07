package com.theomenden.bismuth.blending;

import lombok.Getter;
import lombok.Setter;

public class BlendingBuffer {
    @Getter
    private final float[] color;
    @Getter
    private final float[] blend;
    @Getter
    private final float[] sum;
    @Getter
    private final int blendingRadius;
    @Getter
    private final int sliceSizeLog2;
    @Getter
    private final int blockSizeLog2;
    @Getter
    private final int sliceSize;
    @Getter
    private final int blockSize;
    @Getter
    private final int blendingSize;
    @Getter
    private final int blendingBufferSize;
    @Getter
    private final int scaledBlendingDiameter;
    @Getter
    @Setter
    private int colorBitsExclusive;
    @Getter
    @Setter
    private int colorBitsInclusive;

    public BlendingBuffer(int blendingRadius) {
        this.blendingRadius = blendingRadius;
        this.sliceSizeLog2 = BlendingConfig.getSliceSizeLog2(blendingRadius);
        this.blockSizeLog2 = BlendingConfig.getBlockSizeLog2(blendingRadius);

        this.sliceSize = 1 << sliceSizeLog2;
        this.blockSize = 1 << blockSizeLog2;

        this.blendingSize = BlendingConfig.getBlendingSize(blendingRadius);
        this.blendingBufferSize = BlendingConfig.getBlendingBufferSize(blendingRadius);

        this.scaledBlendingDiameter = (2 * blendingRadius) >> blockSizeLog2;

        this.color = new float[3 * (int)(Math.pow(blendingBufferSize, 3))];
        this.blend = new float[3 * (int)(Math.pow(blendingBufferSize, 2))];
        this.sum = new float[3 * (int)(Math.pow(blendingBufferSize, 2))];

        colorBitsExclusive = 0xFFFFFFFF;
        colorBitsInclusive = 0;
    }

    public void setBlendAtIndex(int index, float value) {
        blend[index] = value;
    }

    public void setColorAtIndex(int index, float value) {
        color[index] = value;
    }

    public void setSumAtIndex(int index, float value) {
        sum[index] += value;
    }
}
