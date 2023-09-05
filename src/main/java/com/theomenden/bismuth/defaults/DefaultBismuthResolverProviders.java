package com.theomenden.bismuth.defaults;

import com.theomenden.bismuth.client.Bismuth;
import com.theomenden.bismuth.colors.interfaces.BismuthResolver;
import com.theomenden.bismuth.colors.interfaces.BismuthResolverProvider;
import com.theomenden.bismuth.mixin.coloring.blocks.BlockColorsAccessor;
import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.ObjectUtils;

public final class DefaultBismuthResolverProviders {
    public static final BismuthResolverProvider<BlockState> BLOCK_STATE_PROVIDER = DefaultBismuthResolverProviders::byBlockState;
    public static final BismuthResolverProvider<Block> BLOCK_PROVIDER = DefaultBismuthResolverProviders::byBlock;
    public static final BismuthResolverProvider<ResourceLocation> SKY_PROVIDER = DefaultBismuthResolverProviders::bySky;
    public static final BismuthResolverProvider<ResourceLocation> SKY_FOG_PROVIDER = DefaultBismuthResolverProviders::byFog;
    public static final BismuthResolverProvider<Fluid> FLUID_FOG_PROVIDER = key -> (manager, biome, coordinates) -> -1;

    private DefaultBismuthResolverProviders(){
    }

    private static BismuthResolver byBlock(Block key) {
        return byBlockState(key.defaultBlockState());
    }

    private static BismuthResolver byBlockState(BlockState key) {
        return (manager, biome, coordinates) -> {
          var colorProvider = ((BlockColorsAccessor)Minecraft
                  .getInstance()
                  .getBlockColors())
                  .getBlockColors()
                  .byId(BuiltInRegistries.BLOCK.getId(key.getBlock()));

          if(colorProvider != null) {
              var world = Minecraft.getInstance().level;
              return colorProvider.getColor(key, world, new BlockPos(coordinates.x(), coordinates.y(), coordinates.z()), 0);
          } else {
              return -1;
          }
        };
    }

    private static BismuthResolver bySky(ResourceLocation key) {
        return(manager, biome, coordinates) ->{
        int color;
        if(BismuthColormaticResolution.hasCustomFogColors()
                && key.equals(Bismuth.OVERWORLD)) {
            var skyColors = ObjectUtils.firstNonNull(
                    BismuthColormaticResolution.SKY_COLORS,
                    BismuthColormaticResolution.COLORMATIC_SKY_COLORS
            );
            color = 0xff000000 | skyColors.getColorMapping().getColorAtCoordinatesForBiome(manager, biome, coordinates);
        } else {
            var colorProperties = ObjectUtils.firstNonNull(
                    BismuthColormaticResolution.COLOR_PROPERTIES,
                    BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES);

            color = colorProperties.getProperties().getDimensionSky(key);

            if(color == 0) {
                color = biome.getSkyColor();
            }
        }
        return color;
    };
    }
    private static BismuthResolver byFog(ResourceLocation key) {
     return (manager, biome, coordinates) -> {
       int color;
       if(BismuthColormaticResolution.hasCustomFogColors()
       && key.equals(Bismuth.OVERWORLD)) {
           var fogColors = ObjectUtils.firstNonNull(
             BismuthColormaticResolution.FOG_COLORS,
             BismuthColormaticResolution.COLORMATIC_FOG_COLORS
           );
           color = 0xff000000 | fogColors.getColorMapping().getColorAtCoordinatesForBiome(manager, biome, coordinates);
       } else {
           var colorProperties = ObjectUtils.firstNonNull(
                   BismuthColormaticResolution.COLOR_PROPERTIES,
                   BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES);

           color = colorProperties.getProperties().getDimensionFog(key);

           if(color == 0) {
               color = biome.getFogColor();
           }
       }
       return color;
     };
    }
}
