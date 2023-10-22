package com.theomenden.bismuth.models;

import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import net.minecraft.resources.ResourceLocation;

public class GridEntry {
    public ObjectImmutableList<ResourceLocation> biomes = ObjectImmutableList.of();
    public int column = -1;
    public int width = 1;
}
