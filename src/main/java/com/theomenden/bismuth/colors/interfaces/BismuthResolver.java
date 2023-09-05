package com.theomenden.bismuth.colors.interfaces;

import com.theomenden.bismuth.models.records.Coordinates;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;

@FunctionalInterface
public interface BismuthResolver {
    int getColorAtCoordinatesForBiome(RegistryAccess manager, Biome biome, Coordinates coordinates);
}
