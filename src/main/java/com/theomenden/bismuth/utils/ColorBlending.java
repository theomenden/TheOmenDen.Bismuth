package com.theomenden.bismuth.utils;

import com.ibm.icu.impl.Utility;
import com.theomenden.bismuth.blending.BlendingBuffer;
import com.theomenden.bismuth.blending.BlendingChunk;
import com.theomenden.bismuth.blending.BlendingConfig;
import com.theomenden.bismuth.caching.caches.ColorBlendingCache;
import com.theomenden.bismuth.caching.caches.ColorCache;
import com.theomenden.bismuth.caching.strategies.ColorSlice;
import com.theomenden.bismuth.client.Bismuth;
import com.theomenden.bismuth.models.NonBlockingThreadLocal;
import com.theomenden.bismuth.models.debug.DebugEvent;
import com.theomenden.bismuth.models.enums.InternalEventType;
import com.theomenden.bismuth.models.records.BiomeColorTypes;
import com.theomenden.bismuth.models.records.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.random.RandomGenerator;

public class ColorBlending {
    public static final int SAMPLE_SEED_X = 1664525;
    public static final int SAMPLE_SEED_Y = 214013;
    public static final int SAMPLE_SEED_Z = 16807;

    public static final ThreadLocal<BlendingBuffer> threadLocalBlendBuffer = new ThreadLocal<>();

    public static BlendingBuffer acquireBlendBuffer(int blendRadius) {
        BlendingBuffer result;
        BlendingBuffer buffer = threadLocalBlendBuffer.get();

        if (buffer != null && buffer.getBlendingRadius() == blendRadius) {
            result = buffer;
        } else {
            result = new BlendingBuffer(blendRadius);
        }

        result.setColorBitsExclusive(0xFFFFFFFF);
        result.setColorBitsInclusive(0);

        return result;
    }

    public static void releaseBlendBuffer(BlendingBuffer buffer) {
        threadLocalBlendBuffer.set(buffer);
    }

    public static int getSliceMin(int blendRadius, int blockSizeLog2, int sliceSizeLog2, int sliceIndex) {
        final int sliceSize = 1 << sliceSizeLog2;
        final int scaledSliceSize = sliceSize >> blockSizeLog2;

        final int scaledBlendDiameter = (2 * blendRadius) >> blockSizeLog2;
        final int scaledLowerBlendRadius = scaledBlendDiameter - (scaledBlendDiameter >> 1);

        int result = 0;

        if (sliceIndex == -1) {
            result = scaledSliceSize - scaledLowerBlendRadius;
        }

        return result;
    }

    private static int resolveScaledResults(int blockSizeLog2, int sliceSize) {
        return sliceSize >> blockSizeLog2;
    }

    public static int getBlendMin(int blendRadius, int blockSizeLog2, int sliceSizeLog2, int sliceIndex) {
        final int sliceSize =  1 << sliceSizeLog2;
        final int scaledSliceSize = sliceSize >> blockSizeLog2;

        final int scaledBlendDiameter = (2 * blendRadius) >> blockSizeLog2;
        final int scaledLowerBlendRadius = scaledBlendDiameter - (scaledBlendDiameter >> 1);

        int result = 0;

        if (sliceIndex >= 0) {
            result += scaledLowerBlendRadius;

            if (sliceIndex == 1) {
                result += scaledSliceSize;
            }
        }

        return result;
    }

    public static int getSliceMax(int blendRadius, int blockSizeLog2, int sliceSizeLog2, int sliceIndex) {
        final int sliceSize = 1 << sliceSizeLog2;
        final int scaledSliceSize = sliceSize >> blockSizeLog2;

        final int scaledBlendDiameter = (2 * blendRadius) >> blockSizeLog2;
        final int scaledUpperBlendRadius = scaledBlendDiameter  >> 1;

        int result = scaledSliceSize;

        if (sliceIndex == 1) {
            result = scaledUpperBlendRadius;
        }

        return result;
    }

    @Nullable
    public static Biome getDefaultBiome(Level world) {
        Biome result = null;

        var biomeHolder = world
                .registryAccess()
                .registryOrThrow(Registries.BIOME)
                .getHolderOrThrow(Biomes.PLAINS);

        if(biomeHolder.isBound()) {
            result = biomeHolder.value();
        }
        return result;
    }

    public static Biome getBiomeAtPositionOrDefault(Level world, BlockPos blockPosition) {
       Biome result;

       var biomeHolder = world.getBiome(blockPosition);

       if(biomeHolder.isBound()) {
           result = biomeHolder.value();
       } else {
           result = getDefaultBiome(world);
       }
       return result;
    }

    public static Biome getBiomeAtPositionOrThrow(Level world, BlockPos blockPos) {
        Biome result = getBiomeAtPositionOrDefault(world, blockPos);

        if (result == null) {
            throw new IllegalStateException("Biome could not be retrieved for block position.");
        }

        return result;
    }

    public static int getColorAtPosition(Level world, BlockPos blockPos, float posX, float posZ, ColorResolver colorResolver) {
        Biome biome = getBiomeAtPositionOrThrow(world, blockPos);

        return colorResolver.getColor(biome, posX, posZ);
    }

    public static int getRandomSamplePosition(int min, int blockSizeLog2, int seed) {
        int blockMask = MathUtils.createLowerBitMask(blockSizeLog2);

        int random = LCGUtils
                .generateRandomNoise(seed)
                .findFirst()
                .orElse(RandomGenerator
                        .getDefault()
                        .nextInt());
        int offset = random & blockMask;

        return  min + offset;
    }

    public static void gatherColorsForSlice(
            Level world,
            ColorResolver colorResolver,
            ColorSlice colorSlice,
            BlendingBuffer blendBuffer,
            int sliceIDX,
            int sliceIDY,
            int sliceIDZ,
            int sliceX,
            int sliceY,
            int sliceZ) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        final int blendRadius = blendBuffer.getBlendingRadius();

        final int sliceSizeLog2 = blendBuffer.getSliceSizeLog2();
        final int blockSizeLog2 = blendBuffer.getBlockSizeLog2();

        final int sliceSize = blendBuffer.getSliceSize();
        final int blendSize = blendBuffer.getBlendingSize();

        final Coordinates sliceMinimums = new Coordinates(
        getSliceMin(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDX),
        getSliceMin(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDY),
        getSliceMin(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDZ)
        );

        final Coordinates sliceMaximums = new Coordinates(
        getSliceMax(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDX),
        getSliceMax(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDY),
        getSliceMax(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDZ)
        );

        final Coordinates blendingMinimums = new Coordinates(
        getBlendMin(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDX),
        getBlendMin(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDY),
        getBlendMin(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDZ)
        );

        final Coordinates dimensionCoordinates = new Coordinates(
                sliceMaximums.x() - sliceMinimums.x(),
                sliceMaximums.y() - sliceMinimums.y(),
                sliceMaximums.z() - sliceMinimums.z()
        );

        int worldMinX = (sliceX << sliceSizeLog2) + (sliceMinimums.x() << blockSizeLog2);
        int worldMinY = (sliceY << sliceSizeLog2) + (sliceMinimums.y() << blockSizeLog2);
        int worldMinZ = (sliceZ << sliceSizeLog2) + (sliceMinimums.z() << blockSizeLog2);

        if ((blendBuffer.getScaledBlendingDiameter() & 1) != 0 && blockSizeLog2 > 0) {
            worldMinX += (1 << (blockSizeLog2 - 1));
            worldMinY += (1 << (blockSizeLog2 - 1));
            worldMinZ += (1 << (blockSizeLog2 - 1));
        }

        int sliceIndexZ = ColorCachingUtils.getArrayIndex(sliceSize, sliceMinimums);
        int blendIndexZ = 3 * ColorCachingUtils.getArrayIndex(blendSize, blendingMinimums);

        for (int z = 0;
             z < dimensionCoordinates.z();
             ++z) {
            int sliceIndexY = sliceIndexZ;
            int blendIndexY = blendIndexZ;

            for (int y = 0;
                 y < dimensionCoordinates.y();
                 ++y) {
                int sliceIndex = sliceIndexY;
                int blendIndex = blendIndexY;

                for (int x = 0;
                     x < dimensionCoordinates.x();
                     ++x) {
                    int cachedColor = colorSlice.data[sliceIndex];

                    if (cachedColor == 0) {
                        final Coordinates sampleMinimums = new Coordinates(
                        worldMinX + (x << blockSizeLog2),
                        worldMinY + (y << blockSizeLog2),
                        worldMinZ + (z << blockSizeLog2));

                        final Coordinates sampleCoordinates = new Coordinates(
                        getRandomSamplePosition(sampleMinimums.x(), blockSizeLog2, SAMPLE_SEED_X),
                        getRandomSamplePosition(sampleMinimums.y(), blockSizeLog2, SAMPLE_SEED_Y),
                        getRandomSamplePosition(sampleMinimums.z(), blockSizeLog2, SAMPLE_SEED_Z)
                        );
                        blockPos.set(sampleCoordinates.x(), sampleCoordinates.y(), sampleCoordinates.z());

                        cachedColor = getColorAtPosition(world, blockPos, sampleCoordinates.x(), sampleCoordinates.z(), colorResolver);

                        colorSlice.data[sliceIndex] = cachedColor;
                    }

                    ColorConverter.convertSRGBToOkLabsInPlace(cachedColor, blendBuffer.getColor(), blendIndex);

                    blendBuffer.setColorBitsExclusive(blendBuffer.getColorBitsExclusive() & cachedColor);
                    blendBuffer.setColorBitsInclusive(blendBuffer.getColorBitsInclusive() | cachedColor);

                    sliceIndex += 1;
                    blendIndex += 3;
                }

                sliceIndexY += sliceSize;
                blendIndexY += 3 * blendSize;
            }

            sliceIndexZ += sliceSize * sliceSize;
            blendIndexZ += 3 * blendSize * blendSize;
        }
    }

    private static void setColorBitsToCenterColor(
            Level world,
            ColorResolver colorResolver,
            BlendingBuffer blendBuffer,
            int sliceX,
            int sliceY,
            int sliceZ) {
        int centerX = (sliceX << blendBuffer.getSliceSizeLog2()) + (1 << (blendBuffer.getSliceSizeLog2() - 1));
        int centerY = (sliceY << blendBuffer.getSliceSizeLog2()) + (1 << (blendBuffer.getSliceSizeLog2() - 1));
        int centerZ = (sliceZ << blendBuffer.getSliceSizeLog2()) + (1 << (blendBuffer.getSliceSizeLog2() - 1));

        BlockPos blockPos = new BlockPos(centerX, centerY, centerZ);

        int color = getColorAtPosition(world, blockPos, centerX, centerZ, colorResolver);

        blendBuffer.setColorBitsInclusive(color);
        blendBuffer.setColorBitsExclusive(color);
    }

    public static boolean neighborChunksAreLoaded(
            Level world,
            int sliceSizeLog2,
            int sliceX,
            int sliceZ) {
        boolean result = true;

        int prevChunkX = Integer.MAX_VALUE;
        int prevChunkZ = Integer.MAX_VALUE;

        for (int sliceOffsetZ = -1;
             sliceOffsetZ <= 1;
             ++sliceOffsetZ) {
            int neighborSliceZ = sliceZ + sliceOffsetZ;
            int neighborChunkZ = neighborSliceZ >> (4 - sliceSizeLog2);

            if (neighborChunkZ != prevChunkZ) {
                for (int sliceOffsetX = -1;
                     sliceOffsetX <= 1;
                     ++sliceOffsetX) {
                    int neighborSliceX = sliceX + sliceOffsetX;
                    int neighborChunkX = neighborSliceX >> (4 - sliceSizeLog2);

                    if (neighborChunkX != prevChunkX) {
                        ChunkAccess chunk = world.getChunk(neighborChunkX, neighborChunkZ, ChunkStatus.BIOMES, false);

                        if (chunk == null) {
                            result = false;
                            break;
                        }
                    }

                    prevChunkX = neighborChunkX;
                }
            }

            prevChunkZ = neighborChunkZ;
        }

        return result;
    }

    public static void gatherColorsToBlendBuffer(
            Level world,
            ColorResolver colorResolver,
            int colorType,
            ColorCache colorCache,
            BlendingBuffer blendBuffer,
            int x,
            int y,
            int z) {

        final Coordinates sliceCoordinates = new Coordinates(
        x >> blendBuffer.getSliceSizeLog2(),
        y >> blendBuffer.getSliceSizeLog2(),
        z >> blendBuffer.getSliceSizeLog2()
        );

        boolean neighborsAreLoaded = neighborChunksAreLoaded(world, blendBuffer.getSliceSizeLog2(), sliceCoordinates.x(), sliceCoordinates.z());

        if (neighborsAreLoaded) {
            boolean[] finishedSlices = new boolean[27];

            final int iterationCount = 2;

            for (int iteration = 0;
                 iteration < 2;
                 ++iteration) {
                boolean lastIteration = ((iteration + 1) == iterationCount);
                boolean tryLock = !lastIteration;
                boolean hasMissingSlices = false;
                int sliceIndex = 0;

                for (int sliceOffsetZ = -1;
                     sliceOffsetZ <= 1;
                     ++sliceOffsetZ) {
                    for (int sliceOffsetY = -1;
                         sliceOffsetY <= 1;
                         ++sliceOffsetY) {
                        for (int sliceOffsetX = -1;
                             sliceOffsetX <= 1;
                             ++sliceOffsetX) {
                            if (!finishedSlices[sliceIndex]) {
                                final Coordinates neighborSlice = new Coordinates(
                                        sliceCoordinates.x() + sliceOffsetX,
                                        sliceCoordinates.y() + sliceOffsetY,
                                        sliceCoordinates.z() + sliceOffsetZ);
                                ColorSlice colorSlice = colorCache.getOrInitSliceByCoordinates(blendBuffer.getSliceSize(), neighborSlice.x(), neighborSlice.y(), neighborSlice.z(), colorType, tryLock);

                                if (colorSlice != null) {
                                    gatherColorsForSlice(
                                            world,
                                            colorResolver,
                                            colorSlice,
                                            blendBuffer,
                                            sliceOffsetX,
                                            sliceOffsetY,
                                            sliceOffsetZ,
                                            neighborSlice.x(),
                                            neighborSlice.y(),
                                            neighborSlice.z());

                                    colorCache.releaseSliceFromCache(colorSlice);

                                    finishedSlices[sliceIndex] = true;
                                } else {
                                    hasMissingSlices = true;
                                }
                            }

                            ++sliceIndex;
                        }
                    }
                }

                if (!hasMissingSlices) {
                    break;
                }
            }
        } else {
            setColorBitsToCenterColor(
                    world,
                    colorResolver,
                    blendBuffer,
                    sliceCoordinates.x(),
                    sliceCoordinates.y(),
                    sliceCoordinates.z());
        }
    }

    public static void blendColorsForSlice(BlendingBuffer buffer, BlendingChunk blendChunk, int inputX, int inputY, int inputZ) {
        final int srcSize = BlendingConfig.getBlendingSize(buffer.getBlendingRadius());
        final int dstSize = BlendingConfig.getSliceSize(buffer.getBlendingRadius());

        final int blendBufferDim = BlendingConfig.getBlendingBufferSize(buffer.getBlendingRadius());

        final int filterSupport = BlendingConfig.getFilterSupport(buffer.getBlendingRadius());
        final int fullFilterDim = filterSupport - 1;
        final int scaledDstSize = dstSize >> buffer.getBlockSizeLog2();

        final int blockSize = buffer.getBlockSize();

        final float oneOverBlockSize = (1.0f / blockSize);

        final float filter = (float) (filterSupport - 1) + oneOverBlockSize;
        final float filterScalar = (float) (1.0f / Math.pow(filter, 3));

        final int sliceSizeLog2 = buffer.getSliceSizeLog2();

        final Coordinates sliceCoordinates  = new Coordinates(
        resolveScaledResults(sliceSizeLog2, inputX),
        resolveScaledResults(sliceSizeLog2, inputY),
        resolveScaledResults(sliceSizeLog2, inputZ));

        int baseX = sliceCoordinates.x() << sliceSizeLog2;
        int baseY = sliceCoordinates.y() << sliceSizeLog2;
        int baseZ = sliceCoordinates.z() << sliceSizeLog2;

        final Coordinates chunkCoordinates = new Coordinates(
        MathUtils.getLowerBits(baseX, 4),
        MathUtils.getLowerBits(baseY, 4),
        MathUtils.getLowerBits(baseZ, 4));

        int baseIndex = ColorCachingUtils.getArrayIndex(16, chunkCoordinates);

        Arrays.fill(buffer.getSum(), 0);

        int newBufferIndexZ = 0;
        int newResultIndexZ = baseIndex;

        for (int z = 0;
             z < srcSize;
             ++z) {
            int newXIndex = 0;

            for (int newX = 0;
                 newX < srcSize;
                 ++newX) {
                int newSourceIndexForY = newXIndex + newBufferIndexZ;
                int newDestinationIndexForY = newXIndex;

                float sumR = 0;
                float sumG = 0;
                float sumB = 0;

                for (int newY = 0;
                     newY < fullFilterDim;
                     ++newY) {
                    sumR += buffer.getColor()[newSourceIndexForY];
                    sumG += buffer.getColor()[newSourceIndexForY + 1];
                    sumB += buffer.getColor()[newSourceIndexForY + 2];

                    newSourceIndexForY += 3 * blendBufferDim;
                }

                newSourceIndexForY = newXIndex + newBufferIndexZ;

                int lowerOffset = 0;
                int upperOffset = 3 * fullFilterDim * blendBufferDim;

                int lowerIndex = newSourceIndexForY + lowerOffset;
                int upperIndex = newSourceIndexForY + upperOffset;

                for (int newY = 0;
                     newY < scaledDstSize;
                     ++newY) {
                    float lowerR = buffer.getColor()[lowerIndex] * oneOverBlockSize;
                    float lowerG = buffer.getColor()[lowerIndex + 1] * oneOverBlockSize;
                    float lowerB = buffer.getColor()[lowerIndex + 2] * oneOverBlockSize;

                    float upperR = buffer.getColor()[upperIndex] * oneOverBlockSize;
                    float upperG = buffer.getColor()[upperIndex + 1] * oneOverBlockSize;
                    float upperB = buffer.getColor()[upperIndex + 2] * oneOverBlockSize;

                    for (int i = 0;
                         i < blockSize;
                         ++i) {
                        sumR += upperR;
                        sumG += upperG;
                        sumB += upperB;

                        buffer.setBlendAtIndex(newDestinationIndexForY, sumR);
                        buffer.setBlendAtIndex(newDestinationIndexForY + 1,sumG);
                        buffer.setBlendAtIndex(newDestinationIndexForY + 2,sumB);

                        sumR -= lowerR;
                        sumG -= lowerG;
                        sumB -= lowerB;

                        newDestinationIndexForY += 3 * blendBufferDim;
                    }

                    lowerIndex += 3 * blendBufferDim;
                    upperIndex += 3 * blendBufferDim;
                }

                newXIndex += 3;
            }

            if (z < fullFilterDim) {
                int newIndexY = 0;

                for (int newY = 0;
                     newY < dstSize;
                     ++newY) {
                    int newSrcIndexX = newIndexY;
                    int newDstIndexX = newIndexY + newBufferIndexZ;
                    int newSumIndexX = newIndexY;

                    float sumR = 0;
                    float sumG = 0;
                    float sumB = 0;

                    for (int newX = 0;
                         newX < fullFilterDim;
                         ++newX) {
                        sumR += buffer.getBlend()[newSrcIndexX];
                        sumG += buffer.getBlend()[newSrcIndexX + 1];
                        sumB += buffer.getBlend()[newSrcIndexX + 2];

                        newSrcIndexX += 3;
                    }

                    int lowerOffset = 0;
                    int upperOffset = 3 * fullFilterDim;

                    newSrcIndexX = newIndexY;

                    for (int newX = 0;
                         newX < scaledDstSize;
                         ++newX) {
                        float lowerR = buffer.getBlend()[newSrcIndexX + lowerOffset] * oneOverBlockSize;
                        float lowerG = buffer.getBlend()[newSrcIndexX + lowerOffset + 1] * oneOverBlockSize;
                        float lowerB = buffer.getBlend()[newSrcIndexX + lowerOffset + 2] * oneOverBlockSize;

                        float upperR = buffer.getBlend()[newSrcIndexX + upperOffset] * oneOverBlockSize;
                        float upperG = buffer.getBlend()[newSrcIndexX + upperOffset + 1] * oneOverBlockSize;
                        float upperB = buffer.getBlend()[newSrcIndexX + upperOffset + 2] * oneOverBlockSize;

                        for (int i = 0;
                             i < blockSize;
                             ++i) {
                            sumR += upperR;
                            sumG += upperG;
                            sumB += upperB;

                            buffer.setColorAtIndex(newDstIndexX,sumR);
                            buffer.setColorAtIndex(newDstIndexX + 1,sumG);
                            buffer.setColorAtIndex(newDstIndexX + 2,sumB);

                            buffer.setSumAtIndex(newSumIndexX,sumR);
                            buffer.setSumAtIndex(newSumIndexX + 1,sumG);
                            buffer.setSumAtIndex(newSumIndexX + 2,sumB);

                            sumR -= lowerR;
                            sumG -= lowerG;
                            sumB -= lowerB;

                            newDstIndexX += 3;
                            newSumIndexX += 3;
                        }

                        newSrcIndexX += 3;
                    }

                    newIndexY += 3 * blendBufferDim;
                }
            } else {
                int resultOffsetX = 0;
                int indexX = 0;

                for (int newY = 0;
                     newY < dstSize;
                     ++newY) {
                    int srcIndexZ = indexX;
                    int dstIndexZ = indexX + newBufferIndexZ;
                    int sumIndexZ = indexX;

                    float sumR = 0;
                    float sumG = 0;
                    float sumB = 0;

                    for (int newX = 0;
                         newX < fullFilterDim;
                         ++newX) {
                        sumR += buffer.getBlend()[srcIndexZ];
                        sumG += buffer.getBlend()[srcIndexZ + 1];
                        sumB += buffer.getBlend()[srcIndexZ + 2];

                        srcIndexZ += 3;
                    }

                    int lowerOffset = 0;
                    int upperOffset = 3 * fullFilterDim;

                    srcIndexZ = indexX;

                    int finalIndexZ = newResultIndexZ + resultOffsetX;

                    for (int newX = 0;
                         newX < scaledDstSize;
                         ++newX) {
                        float lowerR = buffer.getBlend()[srcIndexZ + lowerOffset] * oneOverBlockSize;
                        float lowerG = buffer.getBlend()[srcIndexZ + lowerOffset + 1] * oneOverBlockSize;
                        float lowerB = buffer.getBlend()[srcIndexZ + lowerOffset + 2] * oneOverBlockSize;

                        float upperR = buffer.getBlend()[srcIndexZ + upperOffset] * oneOverBlockSize;
                        float upperG = buffer.getBlend()[srcIndexZ + upperOffset + 1] * oneOverBlockSize;
                        float upperB = buffer.getBlend()[srcIndexZ + upperOffset + 2] * oneOverBlockSize;

                        int lowerYOffset = 3 * -(filterSupport - 1) * blendBufferDim * blendBufferDim;

                        for (int i = 0;
                             i < blockSize;
                             ++i) {
                            sumR += upperR;
                            sumG += upperG;
                            sumB += upperB;

                            buffer.setColorAtIndex(dstIndexZ, sumR);
                            buffer.setColorAtIndex(dstIndexZ + 1,  sumG);
                            buffer.setColorAtIndex(dstIndexZ + 2, sumB);

                            float lowerYRV = buffer.getColor()[dstIndexZ + lowerYOffset];
                            float lowerYGV = buffer.getColor()[dstIndexZ + lowerYOffset + 1];
                            float lowerYBV = buffer.getColor()[dstIndexZ + lowerYOffset + 2];

                            float lowerYR = lowerYRV * oneOverBlockSize;
                            float lowerYG = lowerYGV * oneOverBlockSize;
                            float lowerYB = lowerYBV * oneOverBlockSize;

                            float upperYR = sumR * oneOverBlockSize;
                            float upperYG = sumG * oneOverBlockSize;
                            float upperYB = sumB * oneOverBlockSize;

                            float valueR = buffer.getSum()[sumIndexZ];
                            float valueG = buffer.getSum()[sumIndexZ + 1];
                            float valueB = buffer.getSum()[sumIndexZ + 2];

                            for (int j = 0;
                                 j < blockSize;
                                 ++j) {
                                valueR += upperYR;
                                valueG += upperYG;
                                valueB += upperYB;

                                int finalIndexY = finalIndexZ + 256 * j;

                                float filterR = valueR * filterScalar;
                                float filterG = valueG * filterScalar;
                                float filterB = valueB * filterScalar;

                                ColorConverter.convertOKLabsTosRGBAInPlace(filterR, filterG, filterB, blendChunk.data, finalIndexY);

                                valueR -= lowerYR;
                                valueG -= lowerYG;
                                valueB -= lowerYB;
                            }

                            buffer.setSumAtIndex(sumIndexZ,sumR - lowerYRV);
                            buffer.setSumAtIndex(sumIndexZ + 1, sumG - lowerYGV);
                            buffer.setSumAtIndex(sumIndexZ + 2, sumB - lowerYBV);

                            sumR -= lowerR;
                            sumG -= lowerG;
                            sumB -= lowerB;

                            dstIndexZ += 3;
                            sumIndexZ += 3;

                            finalIndexZ += 1;
                        }

                        srcIndexZ += 3;
                    }

                    indexX += 3 * blendBufferDim;

                    resultOffsetX += 16;
                }

                newResultIndexZ += blockSize *  256;
            }

            newBufferIndexZ += 3 * blendBufferDim * blendBufferDim;
        }
    }

    public static void fillBlendChunkRegionWithColor(
            BlendingChunk blendChunk,
            int color,
            int baseIndex,
            int dim) {
        int indexZ = baseIndex;

        for (int z = 0;
             z < dim;
             ++z) {
            int indexY = indexZ;

            for (int y = 0;
                 y < dim;
                 ++y) {
                for (int x = 0;
                     x < dim;
                     ++x) {
                    blendChunk.data[indexY + x] = color;
                }

                indexY += 16;
            }

            indexZ += 256;
        }
    }

    public static void fillBlendChunkSliceWithColor(
            BlendingChunk blendChunk,
            int color,
            int sliceSizeLog2,
            int x,
            int y,
            int z) {
        final int sliceSize = 1 << sliceSizeLog2;

        final Coordinates sliceCoordinates = new Coordinates(x >> sliceSizeLog2,y >> sliceSizeLog2, z >> sliceSizeLog2);
        int baseX = sliceCoordinates.x() << sliceSizeLog2;
        int baseY = sliceCoordinates.y() << sliceSizeLog2;
        int baseZ = sliceCoordinates.z() << sliceSizeLog2;

        final Coordinates inChunkCoordinates = new Coordinates(
        MathUtils.getLowerBits(baseX, 4),
        MathUtils.getLowerBits(baseY, 4),
        MathUtils.getLowerBits(baseZ, 4)
        );

        int baseIndex = ColorCachingUtils.getArrayIndex(16, inChunkCoordinates);

        fillBlendChunkRegionWithColor(
                blendChunk,
                color,
                baseIndex,
                sliceSize);
    }

    public static void gatherColorsDirectly(
            Level world,
            ColorResolver colorResolver,
            BlendingChunk blendChunk,
            Coordinates requestCoordinates) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        final int sliceSizeLog2 = BlendingConfig.getSliceSizeLog2(0);
        final int sliceSize = BlendingConfig.getSliceSize(0);
        final  Coordinates sliceCoordinates = new Coordinates(
                requestCoordinates.x() >> sliceSizeLog2,
                requestCoordinates.y() >> sliceSizeLog2,
                requestCoordinates.z() >> sliceSizeLog2
        );

        boolean neighborsAreLoaded = neighborChunksAreLoaded(world, sliceSizeLog2, sliceCoordinates.x(), sliceCoordinates.z());

        Coordinates baseCoordinates = new Coordinates(
                sliceCoordinates.x() << sliceSizeLog2,
                sliceCoordinates.y() << sliceSizeLog2,
                sliceCoordinates.z() << sliceSizeLog2
        );

        final Coordinates inChunkCoordinates = new Coordinates(
                MathUtils.getLowerBits(baseCoordinates.x(), 4),
                MathUtils.getLowerBits(baseCoordinates.y(), 4),
                MathUtils.getLowerBits(baseCoordinates.z(), 4)
        );

        int baseIndex = ColorCachingUtils.getArrayIndex(16, inChunkCoordinates);

        if (neighborsAreLoaded) {
            int indexZ = baseIndex;

            int indexY = indexZ;
            int worldZ = baseCoordinates.z();
            for (int z = 0; z < sliceSize; ++z) {
                for (int y = 0; y < sliceSize; ++y) {
                    int indexX = indexY;
                    int worldY = baseCoordinates.y() + y;
                    int worldX = baseCoordinates.x();
                    for (int x = 0; x < sliceSize; ++x) {
                        blockPos.set(worldX, worldY, worldZ);
                        int color = getColorAtPosition(world, blockPos, worldX, worldZ, colorResolver);
                        blendChunk.data[indexX] = color;
                        worldX++;
                        indexX++;
                    }
                    indexY += sliceSize;
                }
                indexZ += sliceSize * sliceSize;
                worldZ += 1;
            }
        } else {
            Coordinates centerCoordinates = new Coordinates(
                    (sliceCoordinates.x() << sliceSizeLog2) + (1 << (sliceSizeLog2 - 1)),
                    (sliceCoordinates.y() << sliceSizeLog2) + (1 << (sliceSizeLog2 - 1)),
                    (sliceCoordinates.z() << sliceSizeLog2) + (1 << (sliceSizeLog2 - 1))
            );
            blockPos.set(centerCoordinates.x(), centerCoordinates.y(), centerCoordinates.z());

            int color = getColorAtPosition(world, blockPos, centerCoordinates.x(), centerCoordinates.z(), colorResolver);

            fillBlendChunkRegionWithColor(blendChunk, color, baseIndex, sliceSize);
        }
    }

    public static void generateColors(
            Level world,
            ColorResolver colorResolver,
            int colorType,
            ColorCache colorCache,
            BlendingChunk blendChunk,
            Coordinates coordinates) {

        DebugEvent debugEvent = DebugUtils.putColorEvent(coordinates, colorType);
        final int blendRadius = Bismuth.configuration.blendingRadius;

        if (blendRadius > BlendingConfig.BIOME_MINIMUM_BLENDING_RADIUS &&
                blendRadius <= BlendingConfig.BIOME_MAXIMUM_BLENDING_RADIUS) {
            BlendingBuffer blendBuffer = acquireBlendBuffer(blendRadius);

            gatherColorsToBlendBuffer(
                    world,
                    colorResolver,
                    colorType,
                    colorCache,
                    blendBuffer,
                    coordinates.x(),
                    coordinates.y(),
                    coordinates.z());

            if (blendBuffer.getColorBitsInclusive() != blendBuffer.getColorBitsExclusive()) {
                DebugEvent subEvent = DebugUtils.putSubEvent(InternalEventType.SUBEVENT);
                blendColorsForSlice(blendBuffer, blendChunk, coordinates.x(), coordinates.y(), coordinates.z());
                DebugUtils.endEventProfile(subEvent);
            } else {
                fillBlendChunkSliceWithColor(
                        blendChunk,
                        blendBuffer.getColorBitsInclusive(),
                        blendBuffer.getSliceSizeLog2(),
                        coordinates.x(), coordinates.y(), coordinates.z());
            }

            releaseBlendBuffer(blendBuffer);
        } else {
            gatherColorsDirectly(
                    world,
                    colorResolver,
                    blendChunk,
                    coordinates);
        }

        DebugUtils.endEventProfile(debugEvent);
    }
}
