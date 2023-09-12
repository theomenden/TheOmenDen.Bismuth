package com.theomenden.bismuth.mixin.client;

import com.theomenden.bismuth.blending.BlendingChunk;
import com.theomenden.bismuth.caching.caches.BlendingCache;
import com.theomenden.bismuth.caching.caches.ColorCache;
import com.theomenden.bismuth.caching.caches.LocalCache;
import com.theomenden.bismuth.client.Bismuth;
import com.theomenden.bismuth.colors.BismuthExtendedColorResolver;
import com.theomenden.bismuth.colors.mapping.BiomeColorMappings;
import com.theomenden.bismuth.models.NonBlockingThreadLocal;
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
    @Unique
    public final BlendingCache bismuth$blendingColorCache = new BlendingCache(1024);
    @Unique
    public final ColorCache bismuth$chunkColorCache= new ColorCache(1024);
    @Unique
    private final ThreadLocal<LocalCache> bismuth$threadLocalCache = NonBlockingThreadLocal.withInitial(LocalCache::new);

    @Final
    @Shadow
    private final Object2ObjectArrayMap<ColorResolver, BlockTintCache> tintCaches = new Object2ObjectArrayMap<>();

    protected ClientLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(method = "clearTintCaches", at = @At("HEAD"))
    public void onClearColorCaches(CallbackInfo ci) {
        this.tintCaches.entrySet().removeIf(entry -> entry.getKey() instanceof BismuthExtendedColorResolver);

        bismuth$blendingColorCache.invalidateAllChunks();
        int blendingRadius = Bismuth.configuration.blendingRadius;
        bismuth$chunkColorCache.invalidateAllCachesInRadius(blendingRadius);
    }

    @Inject(
            method = "onChunkLoaded",
            at = @At("HEAD")
    )
    public void onChunkLoaded(ChunkPos chunkPos, CallbackInfo ci) {
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;

        bismuth$blendingColorCache.invalidateChunk(chunkX, chunkZ);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        final Coordinates blockPositionCoordinates = new Coordinates(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        final Coordinates chunkCoordinates = new Coordinates(blockPositionCoordinates.x() >> 4, blockPositionCoordinates.y() >> 4, blockPositionCoordinates.z() >> 4);
        final Coordinates blockCoordinates = new Coordinates(blockPositionCoordinates.x() & 15, blockPositionCoordinates.y() & 15, blockPositionCoordinates.z() & 15);

        LocalCache localCache = bismuth$threadLocalCache.get();

        BlendingChunk chunk = null;
        int colorType;

        if(localCache.latestColorResolver == colorResolver) {
            colorType = localCache.lastColorType;

            long key = ColorCachingUtils.getChunkKey(chunkCoordinates, colorType);

            if(localCache.lastBlendedChunk.key == key){
                chunk = localCache.lastBlendedChunk;
            }
        } else {
            if(colorResolver == BiomeColors.GRASS_COLOR_RESOLVER) {
                colorType = BiomeColorTypes.INSTANCE.grass();
            } else if (colorResolver == BiomeColors.WATER_COLOR_RESOLVER) {
                colorType = BiomeColorTypes.INSTANCE.water();
            } else if (colorResolver == BiomeColors.FOLIAGE_COLOR_RESOLVER) {
                colorType = BiomeColorTypes.INSTANCE.foliage();
            } else {
                colorType = CompatibilityUtils.getColorType(colorResolver);

                if(colorType >= localCache.blendedChunksCount) {
                    localCache.reallocateBlendedChunkyArray(colorType);
                }
            }

            long key = ColorCachingUtils.getChunkKey(chunkCoordinates, colorType);
            BlendingChunk cachedChunk = localCache.blendedChunks[colorType];

            if(cachedChunk.key == key) {
                chunk = cachedChunk;
            }
        }

        DebugUtils.countThreadLocalChunks(chunk);

        if(chunk == null) {
            chunk = bismuth$blendingColorCache.getOrInitializeChunk(chunkCoordinates, colorType);

            localCache.putChunkInBlendedCache(bismuth$blendingColorCache, chunk, colorType, colorResolver);
        }

        int index = ColorCachingUtils.getArrayIndex(16, blockCoordinates);

        int color = chunk.data[index];

        if(color == 0) {
            ColorBlending.generateColors(
                    this,
                    colorResolver,
                    colorType,
                    bismuth$chunkColorCache,
                    chunk,
                    blockPositionCoordinates
            );

            color = chunk.data[index];
        }

        return color;
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
