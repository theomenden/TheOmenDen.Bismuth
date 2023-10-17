package com.theomenden.bismuth.utils;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

@Getter
@NoArgsConstructor
public final class SimpleBiomeRegistryUtils {
    @Getter
    private static final Object2IntMap<ResourceKey<Biome>> biomes;

    static {
        biomes = new Object2IntArrayMap<>();
        biomes.put(Biomes.THE_VOID, 0);
        biomes.put(Biomes.PLAINS, 1);
        biomes.put(Biomes.SUNFLOWER_PLAINS, 2);
        biomes.put(Biomes.SNOWY_PLAINS, 3);
        biomes.put(Biomes.ICE_SPIKES, 4);
        biomes.put(Biomes.DESERT, 5);
        biomes.put(Biomes.SWAMP, 6);
        biomes.put(Biomes.MANGROVE_SWAMP, 7);
        biomes.put(Biomes.FOREST, 8);
        biomes.put(Biomes.FLOWER_FOREST, 9);
        biomes.put(Biomes.BIRCH_FOREST, 10);
        biomes.put(Biomes.DARK_FOREST, 11);
        biomes.put(Biomes.OLD_GROWTH_BIRCH_FOREST, 12);
        biomes.put(Biomes.OLD_GROWTH_PINE_TAIGA, 13);
        biomes.put(Biomes.OLD_GROWTH_SPRUCE_TAIGA, 14);
        biomes.put(Biomes.TAIGA, 15);
        biomes.put(Biomes.SNOWY_TAIGA, 16);
        biomes.put(Biomes.SAVANNA, 17);
        biomes.put(Biomes.SAVANNA_PLATEAU, 18);
        biomes.put(Biomes.WINDSWEPT_HILLS, 19);
        biomes.put(Biomes.WINDSWEPT_GRAVELLY_HILLS, 20);
        biomes.put(Biomes.WINDSWEPT_FOREST, 21);
        biomes.put(Biomes.WINDSWEPT_SAVANNA, 22);
        biomes.put(Biomes.JUNGLE, 23);
        biomes.put(Biomes.SPARSE_JUNGLE, 24);
        biomes.put(Biomes.BAMBOO_JUNGLE, 25);
        biomes.put(Biomes.BADLANDS, 26);
        biomes.put(Biomes.ERODED_BADLANDS, 27);
        biomes.put(Biomes.WOODED_BADLANDS, 28);
        biomes.put(Biomes.MEADOW, 29);
        biomes.put(Biomes.GROVE, 30);
        biomes.put(Biomes.SNOWY_SLOPES, 31);
        biomes.put(Biomes.FROZEN_PEAKS, 32);
        biomes.put(Biomes.JAGGED_PEAKS, 33);
        biomes.put(Biomes.STONY_PEAKS, 34);
        biomes.put(Biomes.RIVER, 35);
        biomes.put(Biomes.FROZEN_RIVER, 36);
        biomes.put(Biomes.BEACH, 37);
        biomes.put(Biomes.SNOWY_BEACH, 38);
        biomes.put(Biomes.STONY_SHORE, 39);
        biomes.put(Biomes.WARM_OCEAN, 40);
        biomes.put(Biomes.LUKEWARM_OCEAN, 41);
        biomes.put(Biomes.DEEP_LUKEWARM_OCEAN, 42);
        biomes.put(Biomes.OCEAN, 43);
        biomes.put(Biomes.DEEP_OCEAN, 44);
        biomes.put(Biomes.COLD_OCEAN, 45);
        biomes.put(Biomes.DEEP_COLD_OCEAN, 46);
        biomes.put(Biomes.FROZEN_OCEAN, 47);
        biomes.put(Biomes.DEEP_FROZEN_OCEAN, 48);
        biomes.put(Biomes.MUSHROOM_FIELDS, 49);
        biomes.put(Biomes.DRIPSTONE_CAVES, 50);
        biomes.put(Biomes.LUSH_CAVES, 51);
        biomes.put(Biomes.DEEP_DARK, 52);
        biomes.put(Biomes.NETHER_WASTES, 53);
        biomes.put(Biomes.WARPED_FOREST, 54);
        biomes.put(Biomes.CRIMSON_FOREST, 55);
        biomes.put(Biomes.SOUL_SAND_VALLEY, 56);
        biomes.put(Biomes.BASALT_DELTAS, 57);
        biomes.put(Biomes.THE_END, 58);
        biomes.put(Biomes.END_HIGHLANDS, 59);
        biomes.put(Biomes.END_MIDLANDS, 60);
        biomes.put(Biomes.SMALL_END_ISLANDS, 61);
        biomes.put(Biomes.END_BARRENS, 62);
    }
}