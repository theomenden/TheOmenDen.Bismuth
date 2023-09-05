package com.theomenden.bismuth.colors.mapping;

import com.theomenden.bismuth.client.Bismuth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public final class LightMappings {
    private static final Map<ResourceLocation, LightMapping> lightMappings = new HashMap<>();

    public static LightMapping getLightMapping(Level level) {
        return lightMappings.get(Bismuth.getDimensionId(level));
    }

    public static void addLightMapping(ResourceLocation id, LightMapping mapping) {
        lightMappings.put(id, mapping);
    }

    public static void clear() {
        lightMappings.clear();
    }
}
