package com.theomenden.bismuth.blending;

import lombok.Getter;
import lombok.Setter;

@Getter
public class BlendingBuffer {
    private final float[] color;
    private final float[] blend;
    private final float[] sum;
    private final int blendingRadius;
    private final int sliceSizeLog2;
    private final int blockSizeLog2;
    private final int sliceSize;
    private final int blockSize;
    private final int blendingSize;
    private final int blendingBufferSize;
    private final int scaledBlendingDiameter;
    @Setter
    private int colorBitsExclusive;
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

        final int squaredBufferSize = 3 * blendingBufferSize * blendingBufferSize;
        final int cubedBufferSize = 3 * blendingBufferSize * blendingBufferSize * blendingBufferSize;

        this.color = new float[cubedBufferSize];
        this.blend = new float[squaredBufferSize];
        this.sum = new float[squaredBufferSize];

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
