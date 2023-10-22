package com.theomenden.bismuth.adapters;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.world.level.material.MaterialColor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class MaterialColorAdapter extends TypeAdapter<MaterialColor> {
    private static final Reference2ObjectOpenHashMap<String,MaterialColor> MATERIAL_COLORS;

    static{
        var materialColors = new HashMap<>(
                Map.<String, MaterialColor>ofEntries(
                        entry("air", MaterialColor.NONE),
                        entry("grass", MaterialColor.GRASS),
                        entry("sand", MaterialColor.SAND),
                        entry("cloth", MaterialColor.WOOL),
                        entry("tnt", MaterialColor.FIRE),
                        entry("ice", MaterialColor.ICE),
                        entry("iron", MaterialColor.METAL),
                        entry("foliage", MaterialColor.PLANT),
                        entry("snow", MaterialColor.SNOW),
                        entry("white", MaterialColor.SNOW),
                        entry("clay", MaterialColor.CLAY),
                        entry("dirt", MaterialColor.DIRT),
                        entry("stone", MaterialColor.STONE),
                        entry("water", MaterialColor.WATER),
                        entry("wood", MaterialColor.WOOD),
                        entry("quartz", MaterialColor.QUARTZ),
                        entry("adobe", MaterialColor.COLOR_ORANGE),
                        entry("orange", MaterialColor.COLOR_ORANGE),
                        entry("magenta", MaterialColor.COLOR_MAGENTA),
                        entry("light_blue", MaterialColor.COLOR_LIGHT_BLUE),
                        entry("yellow", MaterialColor.COLOR_YELLOW),
                        entry("lime", MaterialColor.COLOR_LIGHT_GREEN),
                        entry("pink", MaterialColor.COLOR_PINK),
                        entry("gray", MaterialColor.COLOR_GRAY),
                        entry("light_gray", MaterialColor.COLOR_LIGHT_GRAY),
                        entry("cyan", MaterialColor.COLOR_CYAN),
                        entry("purple", MaterialColor.COLOR_PURPLE),
                        entry("blue", MaterialColor.COLOR_BLUE),
                        entry("brown", MaterialColor.COLOR_BROWN),
                        entry("green", MaterialColor.COLOR_GREEN),
                        entry("red", MaterialColor.COLOR_RED),
                        entry("black", MaterialColor.COLOR_BLACK),
                        entry("gold", MaterialColor.GOLD),
                        entry("diamond", MaterialColor.DIAMOND),
                        entry("lapis", MaterialColor.LAPIS),
                        entry("emerald", MaterialColor.EMERALD),
                        entry("podzol", MaterialColor.PODZOL),
                        entry("netherrack", MaterialColor.NETHER),
                        entry("white_terracotta", MaterialColor.TERRACOTTA_WHITE),
                        entry("orange_terracotta", MaterialColor.TERRACOTTA_ORANGE),
                        entry("magenta_terracotta", MaterialColor.TERRACOTTA_MAGENTA),
                        entry("light_blue_terracotta", MaterialColor.TERRACOTTA_LIGHT_BLUE),
                        entry("yellow_terracotta", MaterialColor.TERRACOTTA_YELLOW),
                        entry("lime_terracotta", MaterialColor.TERRACOTTA_LIGHT_GREEN),
                        entry("pink_terracotta", MaterialColor.TERRACOTTA_PINK),
                        entry("gray_terracotta", MaterialColor.TERRACOTTA_GRAY),
                        entry("light_gray_terracotta", MaterialColor.TERRACOTTA_LIGHT_GRAY),
                        entry("cyan_terracotta", MaterialColor.TERRACOTTA_CYAN),
                        entry("purple_terracotta", MaterialColor.TERRACOTTA_PURPLE),
                        entry("blue_terracotta", MaterialColor.TERRACOTTA_BLUE),
                        entry("brown_terracotta", MaterialColor.TERRACOTTA_BROWN),
                        entry("green_terracotta", MaterialColor.TERRACOTTA_GREEN),
                        entry("red_terracotta", MaterialColor.TERRACOTTA_RED),
                        entry("black_terracotta", MaterialColor.TERRACOTTA_BLACK)
                ));

        MATERIAL_COLORS = new Reference2ObjectOpenHashMap<>(materialColors);
    }

    @Override
    public void write(JsonWriter jsonWriter, MaterialColor MaterialColor) throws IOException {
        throw new UnsupportedOperationException("writing is not supported");
    }

    @Override
    public MaterialColor read(JsonReader jsonReader) throws IOException {
        if(jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            throw new JsonSyntaxException("Required value cannot be null");
        }
        String readValue = jsonReader.nextString();
        return MATERIAL_COLORS.get(readValue);
    }
}
