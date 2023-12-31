package com.theomenden.bismuth.colors.mapping;

import com.mojang.blaze3d.platform.NativeImage;
import com.theomenden.bismuth.client.Bismuth;
import com.theomenden.bismuth.colors.BismuthExtendedColorResolver;
import com.theomenden.bismuth.colors.interfaces.BismuthResolver;
import com.theomenden.bismuth.colors.properties.ColorMappingProperties;
import com.theomenden.bismuth.models.records.BismuthColor;
import com.theomenden.bismuth.models.records.ColumnBounds;
import com.theomenden.bismuth.models.records.Coordinates;
import com.theomenden.bismuth.utils.ColorConverter;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.apache.commons.lang3.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class BiomeColorMapping implements BismuthResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bismuth.class);
    @Getter
    private final ColorMappingProperties properties;
    private final NativeImage imageColorMapping;
    @Getter
    private transient int defaultColor;
    private transient final BismuthExtendedColorResolver resolver;

    public BiomeColorMapping(ColorMappingProperties properties, NativeImage colorMapping) {
        this.properties = properties;
        this.imageColorMapping = colorMapping;

        BismuthColor colorAsHex = properties.getColor();

        if(colorAsHex != null) {
            defaultColor = ColorConverter.rgbToArgb(colorAsHex.rgb(),1);
        } else {
            defaultColor = computeDefaultColor(properties);
        }

        this.resolver = new BismuthExtendedColorResolver(this);
    }

    @Override
    public int getColorAtCoordinatesForBiome(RegistryAccess manager, Biome biome, Coordinates coordinates) {
        switch (properties.getFormat()) {
            case VANILLA -> {
                float temp = biome.climateSettings.temperature();
                temp = Range
                        .between(0.0f, 1.0f)
                        .fit(temp);
                float rain = Range
                        .between(0.5f, 1.0f)
                        .fit(biome.climateSettings.downfall());
                return getColor(temp, rain);
            }
            case GRID -> {
                ColumnBounds columnBounds = properties.getColumn(
                        Bismuth.getBiomeResourceKey(manager, biome), manager.registry(Registries.BIOME).get());
                @SuppressWarnings({"removal", "deprecation"})
                double fraction = Biome.BIOME_INFO_NOISE
                        .getValue(coordinates.x() * 0.0225, coordinates.z() * 0.0225, false);
                fraction = (fraction + 1.0) * 0.5;
                int x = columnBounds.Column() + (int) (fraction * columnBounds.Count());
                int y = coordinates.y() - properties.getYOffset();
                int variance = properties.getYVariance();
                RandomGenerator gridRandom = RandomGeneratorFactory
                        .getDefault()
                        .create(coordinates.x() * 31L + coordinates.z());
                y += gridRandom.nextInt(variance * 2 + 1) - variance;
                x %= imageColorMapping.getWidth();
                y = Range
                        .between(0, imageColorMapping.getHeight() - 1)
                        .fit(y);
                return imageColorMapping.getPixelRGBA(x, y);
            }
            case FIXED -> {
                return getDefaultColor();
            }
        }
        throw new AssertionError();
    }

    public static int getBiomeCurrentColorOrDefault(BlockAndTintGetter world, BlockPos pos, BiomeColorMapping colormap) {
        if(worldOrPositionIsNull(world, pos)) {
            return colormap.getDefaultColor();
        }

        if(colormap.defaultColor == -1) {
            colormap.defaultColor = colormap.computeDefaultColor(world, colormap.properties);
        }

        return colormap.resolver.resolveExtendedColor(world, pos);
    }

    private static boolean worldOrPositionIsNull(BlockAndTintGetter world, BlockPos pos) {
        return world == null || pos == null;
    }

    private int getColor(float temperature, float rain) {
        rain *= temperature;
        int x = (int)((1.0f - temperature) * 255.0f);
        int y = (int)((1.0f - rain) * 255.0f);

        if(x >= imageColorMapping.getWidth() || y >= imageColorMapping.getHeight()) {
            return 0xffff00ff;
        }

        return imageColorMapping.getPixelRGBA(x, y);
    }

    private int computeDefaultColor(ColorMappingProperties properties) {
        switch (properties.getFormat()) {
            case VANILLA -> {
                return this.imageColorMapping.getPixelRGBA(128, 128);
            }
            case GRID -> {
                try {
                    int x =  properties.getColumn(Biomes.PLAINS, Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.BIOME))
                                       .Column();
                    int y = Range
                            .between(0, imageColorMapping.getHeight() - 1)
                            .fit(63 - properties.getYOffset());

                    return imageColorMapping.getPixelRGBA(x, y);
                } catch (IllegalArgumentException | NullPointerException e) {
                    return 0xffffffff;
                }
            }
            case FIXED -> {
                return 0xffffffff;
            }
        }
        throw new AssertionError();
    }

    private int computeDefaultColor(BlockAndTintGetter world, ColorMappingProperties properties) {
        switch (properties.getFormat()) {
            case VANILLA -> {
                return imageColorMapping.getPixelRGBA(128, 128);
            }
            case GRID -> {
                try {
                    int x = properties
                            .getColumn(Biomes.PLAINS, Minecraft.getInstance().level
                                    .registryAccess()
                                    .registryOrThrow(Registries.BIOME))
                            .Column();
                    int y = Range
                            .between(0, imageColorMapping.getHeight() - 1)
                            .fit(63 - properties.getYOffset());
                    return imageColorMapping.getPixelRGBA(x, y);
                } catch (IllegalArgumentException e) {
                    return 0xffffffff;
                }
            }
            case FIXED -> {
                return 0xffffffff;
            }
        }
        throw new AssertionError();
    }
}