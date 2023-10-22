package com.theomenden.bismuth.utils;

import com.theomenden.bismuth.models.records.ColumnBounds;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.Map;

@Getter
@NoArgsConstructor
public final class SimpleBiomeRegistryUtils {
    @Getter
    private static final Object2IntMap<ResourceKey<Biome>> biomes;
    @Getter
    private static final Object2ObjectLinkedOpenHashMap<ResourceLocation, ColumnBounds> legacyColumns;
    @Getter
    private static final Object2ObjectLinkedOpenHashMap<ResourceLocation, ColumnBounds> stableColumns;
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

    static {
        var legacyMapResult = Map.<ResourceLocation, ColumnBounds>ofEntries(
                Map.entry(Biomes.OCEAN.location(), new ColumnBounds(0, 1)),
                Map.entry(Biomes.PLAINS.location(), new ColumnBounds(1, 1)),
                Map.entry(Biomes.DESERT.location(), new ColumnBounds(2, 1)),
                Map.entry(Biomes.WINDSWEPT_HILLS.location(), new ColumnBounds(3, 1)),
                Map.entry(Biomes.FOREST.location(), new ColumnBounds(4, 1)),
                Map.entry(Biomes.TAIGA.location(), new ColumnBounds(5, 1)),
                Map.entry(Biomes.SWAMP.location(), new ColumnBounds(6, 1)),
                Map.entry(Biomes.RIVER.location(), new ColumnBounds(7, 1)),
                Map.entry(Biomes.NETHER_WASTES.location(), new ColumnBounds(8, 1)),
                Map.entry(Biomes.THE_END.location(), new ColumnBounds(9, 1)),
                Map.entry(Biomes.FROZEN_OCEAN.location(), new ColumnBounds(10, 1)),
                Map.entry(Biomes.FROZEN_RIVER.location(), new ColumnBounds(11, 1)),
                Map.entry(Biomes.SNOWY_PLAINS.location(), new ColumnBounds(12, 1)),
                Map.entry(Biomes.MUSHROOM_FIELDS.location(), new ColumnBounds(14, 1)),
                Map.entry(Biomes.BEACH.location(), new ColumnBounds(16, 1)),
                Map.entry(Biomes.JUNGLE.location(), new ColumnBounds(21, 1)),
                Map.entry(Biomes.SPARSE_JUNGLE.location(), new ColumnBounds(23, 1)),
                Map.entry(Biomes.DEEP_OCEAN.location(), new ColumnBounds(24, 1)),
                Map.entry(Biomes.STONY_SHORE.location(), new ColumnBounds(25, 1)),
                Map.entry(Biomes.SNOWY_BEACH.location(), new ColumnBounds(26, 1)),
                Map.entry(Biomes.BIRCH_FOREST.location(), new ColumnBounds(27, 1)),
                Map.entry(Biomes.DARK_FOREST.location(), new ColumnBounds(29, 1)),
                Map.entry(Biomes.SNOWY_TAIGA.location(), new ColumnBounds(30, 1)),
                Map.entry(Biomes.OLD_GROWTH_PINE_TAIGA.location(), new ColumnBounds(32, 1)),
                Map.entry(Biomes.WINDSWEPT_FOREST.location(), new ColumnBounds(34, 1)),
                Map.entry(Biomes.SAVANNA.location(), new ColumnBounds(35, 1)),
                Map.entry(Biomes.SAVANNA_PLATEAU.location(), new ColumnBounds(36, 1)),
                Map.entry(Biomes.BADLANDS.location(), new ColumnBounds(37, 1)),
                Map.entry(Biomes.WOODED_BADLANDS.location(), new ColumnBounds(38, 1)),
                Map.entry(Biomes.SMALL_END_ISLANDS.location(), new ColumnBounds(40, 1)),
                Map.entry(Biomes.END_MIDLANDS.location(), new ColumnBounds(41, 1)),
                Map.entry(Biomes.END_HIGHLANDS.location(), new ColumnBounds(42, 1)),
                Map.entry(Biomes.END_BARRENS.location(), new ColumnBounds(43, 1)),
                Map.entry(Biomes.WARM_OCEAN.location(), new ColumnBounds(44, 1)),
                Map.entry(Biomes.LUKEWARM_OCEAN.location(), new ColumnBounds(45, 1)),
                Map.entry(Biomes.COLD_OCEAN.location(), new ColumnBounds(46, 1)),
                Map.entry(Biomes.DEEP_LUKEWARM_OCEAN.location(), new ColumnBounds(48, 1)),
                Map.entry(Biomes.DEEP_COLD_OCEAN.location(), new ColumnBounds(49, 1)),
                Map.entry(Biomes.DEEP_FROZEN_OCEAN.location(), new ColumnBounds(50, 1)),
                // formerly "mutated" variants of biomes, normal biomeSource ID + 128, except for
                // the post-1.7 biomeSource additions.
                Map.entry(Biomes.THE_VOID.location(), new ColumnBounds(127, 1)),
                Map.entry(Biomes.SUNFLOWER_PLAINS.location(), new ColumnBounds(129, 1)),
                Map.entry(Biomes.WINDSWEPT_GRAVELLY_HILLS.location(), new ColumnBounds(131, 1)),
                Map.entry(Biomes.FLOWER_FOREST.location(), new ColumnBounds(132, 1)),
                Map.entry(Biomes.ICE_SPIKES.location(), new ColumnBounds(140, 1)),
                Map.entry(Biomes.OLD_GROWTH_BIRCH_FOREST.location(), new ColumnBounds(155, 1)),
                Map.entry(Biomes.OLD_GROWTH_SPRUCE_TAIGA.location(), new ColumnBounds(160, 1)),
                Map.entry(Biomes.WINDSWEPT_SAVANNA.location(), new ColumnBounds(163, 1)),
                Map.entry(Biomes.ERODED_BADLANDS.location(), new ColumnBounds(165, 1)),
                Map.entry(Biomes.BAMBOO_JUNGLE.location(), new ColumnBounds(168, 1)),
                // 1.16 nether biomes
                Map.entry(Biomes.SOUL_SAND_VALLEY.location(), new ColumnBounds(170, 1)),
                Map.entry(Biomes.CRIMSON_FOREST.location(), new ColumnBounds(171, 1)),
                Map.entry(Biomes.WARPED_FOREST.location(), new ColumnBounds(172, 1)),
                Map.entry(Biomes.BASALT_DELTAS.location(), new ColumnBounds(173, 1)),
                // 1.17 cave biomes
                Map.entry(Biomes.DRIPSTONE_CAVES.location(), new ColumnBounds(174, 1)),
                Map.entry(Biomes.LUSH_CAVES.location(), new ColumnBounds(175, 1)),
                // 1.18 highland biomes
                // meadow -> plains
                Map.entry(Biomes.MEADOW.location(), new ColumnBounds(1, 1)),
                // grove -> snowy taiga
                Map.entry(Biomes.GROVE.location(), new ColumnBounds(30, 1)),
                // snowy slopes -> snowy plains
                Map.entry(Biomes.SNOWY_SLOPES.location(), new ColumnBounds(12, 1)),
                Map.entry(Biomes.FROZEN_PEAKS.location(), new ColumnBounds(12, 1)),
                // non-snow peaks -> windswept hills
                Map.entry(Biomes.JAGGED_PEAKS.location(), new ColumnBounds(3, 1)),
                Map.entry(Biomes.STONY_PEAKS.location(), new ColumnBounds(3, 1)),
                // 1.19 wild biomes
                // mangrove swamp -> swamp
                Map.entry(Biomes.MANGROVE_SWAMP.location(), new ColumnBounds(6, 1)),
                // deep dark -> lush caves
                Map.entry(Biomes.DEEP_DARK.location(), new ColumnBounds(175, 1))
        );

        legacyColumns = new Object2ObjectLinkedOpenHashMap<>(legacyMapResult);
    }

    static {
        var stableColumnResults = Map.<ResourceLocation, ColumnBounds>ofEntries(
                Map.entry(Biomes.THE_VOID.location(), new ColumnBounds(0, 1)),
                Map.entry(Biomes.PLAINS.location(), new ColumnBounds(1, 1)),
                Map.entry(Biomes.SUNFLOWER_PLAINS.location(), new ColumnBounds(2, 1)),
                Map.entry(Biomes.SNOWY_PLAINS.location(), new ColumnBounds(3, 1)),
                Map.entry(Biomes.ICE_SPIKES.location(), new ColumnBounds(4, 1)),
                Map.entry(Biomes.DESERT.location(), new ColumnBounds(5, 1)),
                Map.entry(Biomes.SWAMP.location(), new ColumnBounds(6, 1)),
                Map.entry(Biomes.FOREST.location(), new ColumnBounds(7, 1)),
                Map.entry(Biomes.FLOWER_FOREST.location(), new ColumnBounds(8, 1)),
                Map.entry(Biomes.BIRCH_FOREST.location(), new ColumnBounds(9, 1)),
                Map.entry(Biomes.DARK_FOREST.location(), new ColumnBounds(10, 1)),
                Map.entry(Biomes.OLD_GROWTH_BIRCH_FOREST.location(), new ColumnBounds(11, 1)),
                Map.entry(Biomes.OLD_GROWTH_PINE_TAIGA.location(), new ColumnBounds(12, 1)),
                Map.entry(Biomes.OLD_GROWTH_SPRUCE_TAIGA.location(), new ColumnBounds(13, 1)),
                Map.entry(Biomes.TAIGA.location(), new ColumnBounds(14, 1)),
                Map.entry(Biomes.SNOWY_TAIGA.location(), new ColumnBounds(15, 1)),
                Map.entry(Biomes.SAVANNA.location(), new ColumnBounds(16, 1)),
                Map.entry(Biomes.SAVANNA_PLATEAU.location(), new ColumnBounds(17, 1)),
                Map.entry(Biomes.WINDSWEPT_HILLS.location(), new ColumnBounds(18, 1)),
                Map.entry(Biomes.WINDSWEPT_GRAVELLY_HILLS.location(), new ColumnBounds(19, 1)),
                Map.entry(Biomes.WINDSWEPT_FOREST.location(), new ColumnBounds(20, 1)),
                Map.entry(Biomes.WINDSWEPT_SAVANNA.location(), new ColumnBounds(21, 1)),
                Map.entry(Biomes.JUNGLE.location(), new ColumnBounds(22, 1)),
                Map.entry(Biomes.SPARSE_JUNGLE.location(), new ColumnBounds(23, 1)),
                Map.entry(Biomes.BAMBOO_JUNGLE.location(), new ColumnBounds(24, 1)),
                Map.entry(Biomes.BADLANDS.location(), new ColumnBounds(25, 1)),
                Map.entry(Biomes.ERODED_BADLANDS.location(), new ColumnBounds(26, 1)),
                Map.entry(Biomes.WOODED_BADLANDS.location(), new ColumnBounds(27, 1)),
                Map.entry(Biomes.MEADOW.location(), new ColumnBounds(28, 1)),
                Map.entry(Biomes.GROVE.location(), new ColumnBounds(29, 1)),
                Map.entry(Biomes.SNOWY_SLOPES.location(), new ColumnBounds(30, 1)),
                Map.entry(Biomes.FROZEN_PEAKS.location(), new ColumnBounds(31, 1)),
                Map.entry(Biomes.JAGGED_PEAKS.location(), new ColumnBounds(32, 1)),
                Map.entry(Biomes.STONY_PEAKS.location(), new ColumnBounds(33, 1)),
                Map.entry(Biomes.RIVER.location(), new ColumnBounds(34, 1)),
                Map.entry(Biomes.FROZEN_RIVER.location(), new ColumnBounds(35, 1)),
                Map.entry(Biomes.BEACH.location(), new ColumnBounds(36, 1)),
                Map.entry(Biomes.SNOWY_BEACH.location(), new ColumnBounds(37, 1)),
                Map.entry(Biomes.STONY_SHORE.location(), new ColumnBounds(38, 1)),
                Map.entry(Biomes.WARM_OCEAN.location(), new ColumnBounds(39, 1)),
                Map.entry(Biomes.LUKEWARM_OCEAN.location(), new ColumnBounds(40, 1)),
                Map.entry(Biomes.DEEP_LUKEWARM_OCEAN.location(), new ColumnBounds(41, 1)),
                Map.entry(Biomes.OCEAN.location(), new ColumnBounds(42, 1)),
                Map.entry(Biomes.DEEP_OCEAN.location(), new ColumnBounds(43, 1)),
                Map.entry(Biomes.COLD_OCEAN.location(), new ColumnBounds(44, 1)),
                Map.entry(Biomes.DEEP_COLD_OCEAN.location(), new ColumnBounds(45, 1)),
                Map.entry(Biomes.FROZEN_OCEAN.location(), new ColumnBounds(46, 1)),
                Map.entry(Biomes.DEEP_FROZEN_OCEAN.location(), new ColumnBounds(47, 1)),
                Map.entry(Biomes.MUSHROOM_FIELDS.location(), new ColumnBounds(48, 1)),
                Map.entry(Biomes.DRIPSTONE_CAVES.location(), new ColumnBounds(49, 1)),
                Map.entry(Biomes.LUSH_CAVES.location(), new ColumnBounds(50, 1)),
                Map.entry(Biomes.NETHER_WASTES.location(), new ColumnBounds(51, 1)),
                Map.entry(Biomes.WARPED_FOREST.location(), new ColumnBounds(52, 1)),
                Map.entry(Biomes.CRIMSON_FOREST.location(), new ColumnBounds(53, 1)),
                Map.entry(Biomes.SOUL_SAND_VALLEY.location(), new ColumnBounds(54, 1)),
                Map.entry(Biomes.BASALT_DELTAS.location(), new ColumnBounds(55, 1)),
                Map.entry(Biomes.THE_END.location(), new ColumnBounds(56, 1)),
                Map.entry(Biomes.END_HIGHLANDS.location(), new ColumnBounds(57, 1)),
                Map.entry(Biomes.END_MIDLANDS.location(), new ColumnBounds(58, 1)),
                Map.entry(Biomes.SMALL_END_ISLANDS.location(), new ColumnBounds(59, 1)),
                Map.entry(Biomes.END_BARRENS.location(), new ColumnBounds(60, 1)),
                // 1.19
                Map.entry(Biomes.MANGROVE_SWAMP.location(), new ColumnBounds(61, 1)),
                Map.entry(Biomes.DEEP_DARK.location(), new ColumnBounds(62, 1))
        );

        stableColumns = new Object2ObjectLinkedOpenHashMap<>(stableColumnResults);
    }
}