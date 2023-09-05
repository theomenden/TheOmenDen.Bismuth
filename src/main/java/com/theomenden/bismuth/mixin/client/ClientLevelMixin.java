package com.theomenden.bismuth.mixin.client;

import com.theomenden.bismuth.client.Bismuth;
import com.theomenden.bismuth.colors.mapping.BiomeColorMappings;
import com.theomenden.bismuth.models.records.Coordinates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level {
    private ClientLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @ModifyArg(
            method = "getSkyColor",
            at = @At(
                    value="INVOKE",
                    target="Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;"
            ),
            index = 1
    )
    private CubicSampler.Vec3Fetcher proxySkyColor(CubicSampler.Vec3Fetcher fetcher){
        var dimensionId = Bismuth.getDimensionId(this);
        var resolver = BiomeColorMappings.getTotalSky(dimensionId);
        var biomeAccess = this.getBiomeManager();
        var manager = this.registryAccess();

        return (x,y,z) -> {
          var biomeRegistry = manager.registryOrThrow(Registries.BIOME);
          var biome = Bismuth.getRegistryValue(biomeRegistry, biomeAccess.getNoiseBiomeAtQuart(x,y,z));
          var blockCoordinates = new Coordinates( QuartPos.toBlock(x), QuartPos.toBlock(y), QuartPos.toBlock(z));
          return Vec3
                  .fromRGB24(resolver.getColorAtCoordinatesForBiome(manager, biome,blockCoordinates));
        };
    }
}
