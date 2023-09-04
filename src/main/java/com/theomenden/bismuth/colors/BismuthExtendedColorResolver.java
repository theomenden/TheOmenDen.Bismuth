package com.theomenden.bismuth.colors;

import com.theomenden.bismuth.colors.interfaces.BismuthResolver;
import com.theomenden.bismuth.colors.mapping.BiomeColorMapping;
import com.theomenden.bismuth.models.ColorMappingStorage;
import com.theomenden.bismuth.models.NonBlockingThreadLocal;
import com.theomenden.bismuth.models.YCoordinate;
import com.theomenden.bismuth.models.records.Coordinates;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

public final class BismuthExtendedColorResolver implements ColorResolver {
    @Nullable
    private static RegistryAccess registryManager;
    private final ThreadLocal<YCoordinate> positionY;
    @Getter
    private final BismuthResolver wrappedResolver;

    public <T> BismuthExtendedColorResolver(ColorMappingStorage<T> storage, T key, BismuthResolver fallback) {
        this.positionY = NonBlockingThreadLocal.withInitial(YCoordinate::new);
        this.wrappedResolver = createResolver(storage, key, fallback);
    }

    public BismuthExtendedColorResolver(BismuthResolver wrappedResolver) {
        this.positionY = NonBlockingThreadLocal.withInitial(YCoordinate::new);
        this.wrappedResolver = wrappedResolver;
    }

    public int resolveExtendedColor(BlockAndTintGetter world, BlockPos position) {
        this.positionY.get().Y = position.getY();
        return world.getBlockTint(position, this);
    }

    @Override
    public int getColor(Biome biome, double x, double z) {
        var coordinates = new Coordinates((int)x, this.positionY.get().Y, (int)z);
        return 0xfffefefe & wrappedResolver.getColorAtCoordinatesForBiome(registryManager, biome, coordinates);
    }

    public static void setRegistryManager(@Nullable RegistryAccess manager) {
        registryManager = manager;
    }

    private static <T> BismuthResolver createResolver(ColorMappingStorage<T> storage, T key, BismuthResolver fallback) {

        var data = NonBlockingThreadLocal.withInitial(StoredData::new);

        return (manager, biome, coordinates) -> {
            var storedData = data.get();
            if(storedData.lastBiome != biome) {
                storedData.lastColormapping = storage.getColorMapping(manager, key, biome);
                storedData.lastBiome = biome;
            }

            var colormapping = storedData.lastColormapping;
            return colormapping != null
                    ? colormapping.getColorAtCoordinatesForBiome(manager, biome, coordinates)
                    : fallback.getColorAtCoordinatesForBiome(manager, biome, coordinates);
        };
    }

    private static final class StoredData {
        @Nullable Biome lastBiome;
        @Nullable
        BiomeColorMapping lastColormapping;
    }
}
