package com.theomenden.bismuth.adapters;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.world.level.material.MaterialColor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MaterialColorAdapter extends TypeAdapter<MaterialColor> {
    private static final Map<String, MaterialColor> MATERIAL_COLORS = new HashMap<>(
            Map.<String, MaterialColor>ofEntries(
                    Map.entry("air", MaterialColor.NONE),
                    Map.entry("grass", MaterialColor.GRASS),
                    Map.entry("sand", MaterialColor.SAND),
                    Map.entry("cloth", MaterialColor.WOOL),
                    Map.entry("tnt", MaterialColor.FIRE),
                    Map.entry("ice", MaterialColor.ICE),
                    Map.entry("iron", MaterialColor.METAL),
                    Map.entry("foliage", MaterialColor.PLANT),
                    Map.entry("snow", MaterialColor.SNOW),
                    Map.entry("white", MaterialColor.SNOW),
                    Map.entry("clay", MaterialColor.CLAY),
                    Map.entry("dirt", MaterialColor.DIRT),
                    Map.entry("stone", MaterialColor.STONE),
                    Map.entry("water", MaterialColor.WATER),
                    Map.entry("wood", MaterialColor.WOOD),
                    Map.entry("quartz", MaterialColor.QUARTZ),
                    Map.entry("adobe", MaterialColor.COLOR_ORANGE),
                    Map.entry("orange", MaterialColor.COLOR_ORANGE),
                    Map.entry("magenta", MaterialColor.COLOR_MAGENTA),
                    Map.entry("light_blue", MaterialColor.COLOR_LIGHT_BLUE),
                    Map.entry("yellow", MaterialColor.COLOR_YELLOW),
                    Map.entry("lime", MaterialColor.COLOR_LIGHT_GREEN),
                    Map.entry("pink", MaterialColor.COLOR_PINK),
                    Map.entry("gray", MaterialColor.COLOR_GRAY),
                    Map.entry("light_gray", MaterialColor.COLOR_LIGHT_GRAY),
                    Map.entry("cyan", MaterialColor.COLOR_CYAN),
                    Map.entry("purple", MaterialColor.COLOR_PURPLE),
                    Map.entry("blue", MaterialColor.COLOR_BLUE),
                    Map.entry("brown", MaterialColor.COLOR_BROWN),
                    Map.entry("green", MaterialColor.COLOR_GREEN),
                    Map.entry("red", MaterialColor.COLOR_RED),
                    Map.entry("black", MaterialColor.COLOR_BLACK),
                    Map.entry("gold", MaterialColor.GOLD),
                    Map.entry("diamond", MaterialColor.DIAMOND),
                    Map.entry("lapis", MaterialColor.LAPIS),
                    Map.entry("emerald", MaterialColor.EMERALD),
                    Map.entry("podzol", MaterialColor.PODZOL),
                    Map.entry("netherrack", MaterialColor.NETHER),
                    Map.entry("white_terracotta", MaterialColor.TERRACOTTA_WHITE),
                    Map.entry("orange_terracotta", MaterialColor.TERRACOTTA_ORANGE),
                    Map.entry("magenta_terracotta", MaterialColor.TERRACOTTA_MAGENTA),
                    Map.entry("light_blue_terracotta", MaterialColor.TERRACOTTA_LIGHT_BLUE),
                    Map.entry("yellow_terracotta", MaterialColor.TERRACOTTA_YELLOW),
                    Map.entry("lime_terracotta", MaterialColor.TERRACOTTA_LIGHT_GREEN),
                    Map.entry("pink_terracotta", MaterialColor.TERRACOTTA_PINK),
                    Map.entry("gray_terracotta", MaterialColor.TERRACOTTA_GRAY),
                    Map.entry("light_gray_terracotta", MaterialColor.TERRACOTTA_LIGHT_GRAY),
                    Map.entry("cyan_terracotta", MaterialColor.TERRACOTTA_CYAN),
                    Map.entry("purple_terracotta", MaterialColor.TERRACOTTA_PURPLE),
                    Map.entry("blue_terracotta", MaterialColor.TERRACOTTA_BLUE),
                    Map.entry("brown_terracotta", MaterialColor.TERRACOTTA_BROWN),
                    Map.entry("green_terracotta", MaterialColor.TERRACOTTA_GREEN),
                    Map.entry("red_terracotta", MaterialColor.TERRACOTTA_RED),
                    Map.entry("black_terracotta", MaterialColor.TERRACOTTA_BLACK)
            ));

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
