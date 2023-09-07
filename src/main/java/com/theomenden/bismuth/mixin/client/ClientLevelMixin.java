package com.theomenden.bismuth.mixin.client;

import com.theomenden.bismuth.blending.BlendingChunk;
import com.theomenden.bismuth.caching.caches.BlendingCache;
import com.theomenden.bismuth.caching.caches.ColorCache;
import com.theomenden.bismuth.caching.caches.LocalCache;
import com.theomenden.bismuth.client.Bismuth;
import com.theomenden.bismuth.colors.BismuthExtendedColorResolver;
import com.theomenden.bismuth.colors.mapping.BiomeColorMappings;
import com.theomenden.bismuth.models.records.BiomeColorTypes;
import com.theomenden.bismuth.models.records.Coordinates;
import com.theomenden.bismuth.utils.ColorBlending;
import com.theomenden.bismuth.utils.ColorCachingUtils;
import com.theomenden.bismuth.utils.CompatibilityUtils;
import com.theomenden.bismuth.utils.DebugUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level {
    @Final
    @Shadow
    private final Object2ObjectArrayMap<ColorResolver, BlockTintCache> tintCaches = new Object2ObjectArrayMap<>();

    @Shadow public abstract int calculateBlockTint(BlockPos blockPos, ColorResolver colorResolver);

    protected ClientLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(method = "clearTintCaches", at = @At("HEAD"))
    public void onClearColorCaches(CallbackInfo ci) {
        this.tintCaches.entrySet().removeIf(entry -> entry.getKey() instanceof BismuthExtendedColorResolver);
    }

    @Inject(
            method = "getBlockTint",
            at = @At("HEAD")
    )
    private void onGetBlockTint(BlockPos blockPos, ColorResolver colorResolver, CallbackInfoReturnable<Integer> cir) {
        if(this.tintCaches.get(colorResolver) == null) {
            this.tintCaches.put(colorResolver, new BlockTintCache(pos1 -> this.calculateBlockTint(pos1, colorResolver)));
        }
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
