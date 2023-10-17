package com.theomenden.bismuth.colors.mapping;

import com.google.common.collect.Sets;
import com.theomenden.bismuth.colors.interfaces.BismuthResolver;
import com.theomenden.bismuth.colors.properties.ColorMappingProperties;
import com.theomenden.bismuth.defaults.DefaultBismuthResolverProviders;
import com.theomenden.bismuth.models.ColorMappingStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public final class BiomeColorMappings {
    private static final ColorMappingStorage<Block> colorMappingsByBlock = new ColorMappingStorage<>(DefaultBismuthResolverProviders.BLOCK_PROVIDER);
    private static final ColorMappingStorage<BlockState> colorMappingsByBlockState = new ColorMappingStorage<>(DefaultBismuthResolverProviders.BLOCK_STATE_PROVIDER);
    private static final ColorMappingStorage<ResourceLocation> skycolorMappings = new ColorMappingStorage<>(DefaultBismuthResolverProviders.SKY_PROVIDER);
    private static final ColorMappingStorage<ResourceLocation> skyFogColorMappings = new ColorMappingStorage<>(DefaultBismuthResolverProviders.SKY_FOG_PROVIDER);
    private static final ColorMappingStorage<Fluid> fluidFogColorMappings = new ColorMappingStorage<>(DefaultBismuthResolverProviders.FLUID_FOG_PROVIDER);

    private static final Set<String> skyKeys = Sets.newHashSet("bismuth:sky", "vanadium:sky", "colormatic:sky");
    private static final Set<String> skyFogKeys = Sets.newHashSet("bismuth:sky_fog", "vanadium:sky_fog", "colormatic:sky_fog");
    private static final Set<String> fluidFogKeys = Sets.newHashSet("bismuth:fluid_fog", "vanadium:fluid_fog", "colormatic:fluid_fog");


    private BiomeColorMappings(){
    }

    public static BismuthResolver getTotalSky(ResourceLocation dimensionId) {
        return skycolorMappings.getBismuthResolver(dimensionId);
    }

    public static BismuthResolver getTotalSkyFog(ResourceLocation dimensionId) {
        return skyFogColorMappings.getBismuthResolver(dimensionId);
    }

    public static BiomeColorMapping getFluidFog(RegistryAccess manager, Fluid fluid, Biome biome) {
        return fluidFogColorMappings.getColorMapping(manager, fluid, biome);
    }

    public static void addBiomeColorMapping(BiomeColorMapping biomeColorMap) {
        ColorMappingProperties props = biomeColorMap.getProperties();
        Set<ResourceLocation> biomes = props.getApplicableBiomes();
        colorMappingsByBlockState.addColorMapping(biomeColorMap, props.getApplicableBlockStates(), biomes);
        colorMappingsByBlock.addColorMapping(biomeColorMap, props.getApplicableBlocks(), biomes);

        props
                .getApplicableSpecialIds()
                .forEach((key1, value) -> {
                    String key = key1.toString();
                    if (skyKeys.contains(key)) {
                        skycolorMappings.addColorMapping(biomeColorMap, value, biomes);
                    } else if (skyFogKeys.contains(key)) {
                        skyFogColorMappings.addColorMapping(biomeColorMap, value, biomes);
                    } else if (fluidFogKeys.contains(key)) {
                        Collection<Fluid> fluids = value
                                .stream()
                                .map(BuiltInRegistries.FLUID::get)
                                .collect(Collectors.toList());
                        fluidFogColorMappings.addColorMapping(biomeColorMap, fluids, biomes);
                    }
                });
    }

    public static void resetColorMappings() {
        colorMappingsByBlockState.clearMappings();
        colorMappingsByBlock.clearMappings();
        fluidFogColorMappings.clearMappings();
        skycolorMappings.clearMappings();
        skyFogColorMappings.clearMappings();
    }

    public static boolean isCustomColored(BlockState state) {
        return colorMappingsByBlock.contains(state.getBlock())
                || colorMappingsByBlockState.contains(state);
    }

    public static boolean isItemCustomColored(BlockState state) {
        return colorMappingsByBlock.getFallbackColorMapping(state.getBlock()) != null
                || colorMappingsByBlockState.getFallbackColorMapping(state) != null;
    }

    public static boolean isFluidFogCustomColored(Fluid fluid) {
        return fluidFogColorMappings.contains(fluid);
    }

    public static int getBiomeColorMapping(BlockState state, BlockAndTintGetter world, BlockPos pos) {
        if(world != null && pos != null) {
            var resolver = colorMappingsByBlockState.getExtendedResolver(state);
            if(resolver == null) {
                resolver = colorMappingsByBlock.getExtendedResolver(state.getBlock());
            }
            if(resolver == null) {
                throw new IllegalArgumentException(String.valueOf(state));
            }

            return resolver.resolveExtendedColor(world, pos);
        } else {
            BiomeColorMapping colormap = colorMappingsByBlockState.getFallbackColorMapping(state);
            if(colormap == null) {
                colormap = colorMappingsByBlock.getFallbackColorMapping(state.getBlock());
            }
            if(colormap != null) {
                return colormap.getDefaultColor();
            } else {
                return 0xffffff;
            }
        }
    }
}
