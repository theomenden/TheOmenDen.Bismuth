package com.theomenden.bismuth.colors.properties;

import com.google.gson.JsonSyntaxException;
import com.theomenden.bismuth.client.Bismuth;
import com.theomenden.bismuth.defaults.DefaultColumns;
import com.theomenden.bismuth.models.ApplicableBlockStates;
import com.theomenden.bismuth.models.GridEntry;
import com.theomenden.bismuth.models.enums.ColumnLayout;
import com.theomenden.bismuth.models.enums.Format;
import com.theomenden.bismuth.models.records.BismuthColor;
import com.theomenden.bismuth.models.records.ColumnBounds;
import com.theomenden.bismuth.utils.BiomeTracingUtils;
import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import com.theomenden.bismuth.utils.GsonUtils;
import lombok.Getter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ColorMappingProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bismuth.MODID);
    private final ResourceLocation id;
    private transient final boolean isUsingOptfine;
    @Getter
    private final Format format;
    private final Collection<ApplicableBlockStates> blocks;
    @Getter
    private final ResourceLocation source;
    @Getter
    private final BismuthColor color;
    private final ColumnLayout layout;
    @Getter
    private final int yVariance;
    @Getter
    private final int yOffset;
    private final Map<ResourceLocation, ColumnBounds> columnsByBiome;

    private static final ColumnBounds DEFAULT_BOUNDS = new ColumnBounds(0, 1);

    private ColorMappingProperties(ResourceLocation id, Settings settings) {
        this.id = id;
        this.isUsingOptfine = this.id
                .getPath()
                .endsWith(".properties");
        this.format = settings.format;
        this.blocks = settings.blocks;
        ResourceLocation source = ResourceLocation.tryParse(settings.source);

        if (source == null) {
            LOGGER.error("{}: Invalid source location '{}', using file name as fallback", id, settings.source);
            source = new ResourceLocation(makeSourceFromFileName(id));
        }

        this.source = source;
        this.color = settings.color;
        this.layout = Objects.requireNonNullElse(settings.layout, this.isUsingOptfine ? ColumnLayout.OPTIFINE : ColumnLayout.DEFAULT);
        this.yVariance = settings.yVariance;
        this.yOffset = settings.yOffset;

        if (settings.grid != null) {
            this.columnsByBiome = new HashMap<>();
            int nextColumn = 0;
            List<GridEntry> grid = settings.grid;
            for (GridEntry entry : grid) {
                if (entry.column >= 0) {
                    nextColumn = entry.column;
                }
                ColumnBounds bounds = new ColumnBounds(nextColumn, entry.width);
                nextColumn += entry.width;

                for(var biomeResourceLocation : entry.biomes) {
                    var updatedResource = BiomeTracingUtils.updateBiomeName(biomeResourceLocation, this.id);
                    if(updatedResource != null) {
                        columnsByBiome.put(updatedResource, bounds);
                    }
                }
            }
        } else if (settings.biomes != null) {
            this.columnsByBiome = new HashMap<>();

            for(Map.Entry<ResourceLocation, Integer> entry: settings.biomes.entrySet()) {
                var updated = BiomeTracingUtils.updateBiomeName(entry.getKey(), this.id);
                if(updated != null) {
                    columnsByBiome.put(updated, new ColumnBounds(entry.getValue(), 1));
                }
            }
        } else {
            this.columnsByBiome = null;
        }
    }

    public ColumnBounds getColumn(ResourceKey<Biome> biomeKey, Registry<Biome> biomeRegistry) {
        if(format == Format.GRID) {
            if(biomeKey != null) {
                ResourceLocation id = biomeKey.location();
                if(columnsByBiome != null) {
                    ColumnBounds cb = columnsByBiome.get(id);

                    if(cb == null) {
                        throw new IllegalArgumentException(id.toString());
                    }
                    return cb;
                } else {
                    return switch(layout) {
                        case DEFAULT ->  DefaultColumns.getDefaultBoundaries(biomeKey);
                        case OPTIFINE -> DefaultColumns.getOptifineBoundaries(biomeKey, biomeRegistry);
                        case LEGACY -> DefaultColumns.getLegacyBoundaries(biomeKey, biomeRegistry, this.isUsingOptfine);
                        case STABLE -> DefaultColumns.getStableBoundaries(biomeKey);
                    };
                }
            } else{
                return DEFAULT_BOUNDS;
            }
        } else {
            throw new IllegalStateException(format.toString());
        }
    }

    public Set<ResourceLocation> getApplicableBiomes() {
        Set<ResourceLocation> result = new HashSet<>();
        if (columnsByBiome != null) {
            result = new HashSet<>(columnsByBiome.keySet());
        }
        return result;
    }

    public Set<Block> getApplicableBlocks() {
        Set<Block> result = new HashSet<>();
        for(ApplicableBlockStates applicableBlockState: blocks) {
            if(applicableBlockState.specialKey == null && applicableBlockState.states.isEmpty()) {
                result.add(applicableBlockState.block);
            }
        }
        return result;
    }

    public Set<BlockState> getApplicableBlockStates() {
        Set<BlockState> result = new HashSet<>();
        for(ApplicableBlockStates blockStates: blocks) {
            if(blockStates.specialKey == null) {
                result.addAll(blockStates.states);
            }
        }
        return result;
    }

    public Map<ResourceLocation, Collection<ResourceLocation>> getApplicableSpecialIds() {
        return blocks
                .stream()
                .filter(a -> a.specialKey != null)
                .collect(Collectors.toMap(a -> a.specialKey, a -> a.specialLocations, (a1, b) -> b));
    }

    @Override
    public String toString() {
        return String.format("ColormapProperties { format=%s, blocks=%s, source=%s, color=%x, yVariance=%x, yOffset=%x }",
                format,
                blocks,
                source,
                color == null ? 0 : color.rgb(),
                yVariance,
                yOffset);
    }

    public static ColorMappingProperties load(ResourceManager manager, ResourceLocation id, boolean custom) {
        try (InputStream in = manager
                .getResourceOrThrow(id)
                .open();
             Reader r = GsonUtils
                     .getJsonReader(in, id, k -> k, "blocks"::equals)) {
            return loadFromJson(r, id, custom);
        } catch (IOException e) {
            return loadFromJson(new StringReader("{}"), id, custom);
        }
    }

    private static ColorMappingProperties loadFromJson(Reader json, ResourceLocation id, boolean custom) {
        Settings settings;


        try {
            settings = GsonUtils.PROPERTY_GSON.fromJson(json, Settings.class);
            if (settings == null) {
                settings = new Settings();
            }
        } catch (Exception e) {
            // any one of a number of exceptions could have been thrown during deserialization
            LOGGER.error("Error loading {}: {}", id, e.getMessage());
            settings = new Settings();
        }

        var colorProperties = ObjectUtils.firstNonNull(
                BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                BismuthColormaticResolution.COLOR_PROPERTIES
        );
        if (settings.format == null) {
            settings.format = colorProperties
                    .getProperties()
                    .getDefaultFormat();
        }
        if (settings.layout == null) {
            settings.layout = colorProperties
                    .getProperties()
                    .getDefaultColumnLayout();
        }
        if (custom) {
            if (settings.blocks == null) {
                String blockId = id.getPath();
                blockId = blockId.substring(blockId.lastIndexOf('/') + 1, blockId.lastIndexOf('.'));
                settings.blocks = new ArrayList<>();
                try {
                    settings.blocks.add(GsonUtils.PROPERTY_GSON.fromJson(blockId, ApplicableBlockStates.class));
                } catch (JsonSyntaxException e) {
                    LOGGER.error("Error parsing {}: {}", id, e.getMessage());
                }
            }
        } else {
            // disable `blocks`, `grid`, and `biomes` for non-custom colormaps, warn if they are present
            if (settings.biomes != null || settings.grid != null || settings.blocks != null) {
                LOGGER.warn("{}: found `biomes`, `grid`, or `blocks` properties in a provided colormap; these will be ignored", id);
            }
            settings.biomes = null;
            settings.grid = null;
            settings.blocks = Collections.emptyList();
        }
        if (settings.source == null) {
            settings.source = makeSourceFromFileName(id);
        }
        settings.source = GsonUtils.resolveRelativeResourceLocation(settings.source, id);
        return new ColorMappingProperties(id, settings);
    }

    private static String makeSourceFromFileName(ResourceLocation id) {
        String path = id.toString();
        path = path.substring(0, path.lastIndexOf('.')) + ".png";
        return path;
    }

    private static class Settings {
        Format format = null;
        Collection<ApplicableBlockStates> blocks = null;
        String source = null;
        BismuthColor color = null;
        ColumnLayout layout = null;
        int yVariance = 0;
        int yOffset = 0;
        Map<ResourceLocation, Integer> biomes = null;
        List<GridEntry> grid = null;
    }
}
