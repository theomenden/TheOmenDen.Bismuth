package com.theomenden.bismuth.utils;

import com.theomenden.bismuth.client.Bismuth;
import com.theomenden.bismuth.colors.resources.BiomeColorMappingResource;
import com.theomenden.bismuth.colors.resources.LinearColorMappingResource;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;

import static com.theomenden.bismuth.client.Bismuth.MODID;

public class BismuthColormaticResolution {
    private BismuthColormaticResolution(){}
    public static final BiomeColorMappingResource WATER_COLORS = new BiomeColorMappingResource(new ResourceLocation(MODID, "colormap/water"));
    public static final BiomeColorMappingResource UNDERWATER_COLORS = new BiomeColorMappingResource(new ResourceLocation(MODID, "colormap/underwater"));
    public static final BiomeColorMappingResource UNDERLAVA_COLORS = new BiomeColorMappingResource(new ResourceLocation(MODID, "colormap/underlava"));
    public static final BiomeColorMappingResource SKY_COLORS = new BiomeColorMappingResource(new ResourceLocation(MODID, "colormap/sky0"));
    public static final BiomeColorMappingResource FOG_COLORS = new BiomeColorMappingResource(new ResourceLocation(MODID, "colormap/fog0"));
    public static final BiomeColorMappingResource BIRCH_COLORS = new BiomeColorMappingResource(new ResourceLocation(MODID, "colormap/birch"));
    public static final BiomeColorMappingResource SPRUCE_COLORS = new BiomeColorMappingResource(new ResourceLocation(MODID, "colormap/pine"));
    public static final LinearColorMappingResource PUMPKIN_STEM_COLORS = new LinearColorMappingResource(new ResourceLocation(MODID, "colormap/pumpkinstem.png"));
    public static final LinearColorMappingResource MELON_STEM_COLORS = new LinearColorMappingResource(new ResourceLocation(MODID, "colormap/melonstem.png"));
    public static final LinearColorMappingResource REDSTONE_COLORS = new LinearColorMappingResource(new ResourceLocation(MODID, "colormap/redstone.png"));
    public static final LinearColorMappingResource MYCELIUM_PARTICLE_COLORS = new LinearColorMappingResource(new ResourceLocation(MODID, "colormap/myceliumparticle.png"));
    public static final LinearColorMappingResource LAVA_DROP_COLORS = new LinearColorMappingResource(new ResourceLocation(MODID, "colormap/lavadrop.png"));
    public static final LinearColorMappingResource DURABILITY_COLORS = new LinearColorMappingResource(new ResourceLocation(MODID, "colormap/durability.png"));
    public static final LinearColorMappingResource EXPERIENCE_ORB_COLORS = new LinearColorMappingResource(new ResourceLocation(MODID, "colormap/xporb.png"));
    public static final CustomBiomeColorMappingResource CUSTOM_BLOCK_COLORS = new CustomBiomeColorMappingResource();
    public static final GlobalLightMappingResource LIGHTMAP_PROPERTIES = new GlobalLightMappingResource(new ResourceLocation(MODID, "lightmap.json"));
    public static final LightMappingResource LIGHTMAPS = new LightMappingResource(new ResourceLocation(MODID, "lightmap"));
    public static final GlobalColorResource COLOR_PROPERTIES = new GlobalColorResource(new ResourceLocation(MODID, "color"));
    public static final BiomeColorMappingResource COLORMATIC_WATER_COLORS = new BiomeColorMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/water"));
    public static final BiomeColorMappingResource COLORMATIC_UNDERWATER_COLORS = new BiomeColorMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/underwater"));
    public static final BiomeColorMappingResource COLORMATIC_UNDERLAVA_COLORS = new BiomeColorMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/underlava"));
    public static final BiomeColorMappingResource COLORMATIC_SKY_COLORS = new BiomeColorMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/sky0"));
    public static final BiomeColorMappingResource COLORMATIC_FOG_COLORS = new BiomeColorMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/fog0"));
    public static final BiomeColorMappingResource COLORMATIC_BIRCH_COLORS = new BiomeColorMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/birch"));
    public static final BiomeColorMappingResource COLORMATIC_SPRUCE_COLORS = new BiomeColorMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/pine"));
    public static final LinearColorMappingResource COLORMATIC_PUMPKIN_STEM_COLORS = new LinearColorMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/pumpkinstem.png"));
    public static final LinearColorMappingResource COLORMATIC_MELON_STEM_COLORS = new LinearColorMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/melonstem.png"));
    public static final LinearColorMappingResource COLORMATIC_REDSTONE_COLORS = new LinearColorMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/redstone.png"));
    public static final LinearColorMappingResource COLORMATIC_MYCELIUM_PARTICLE_COLORS = new LinearColorMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/myceliumparticle.png"));
    public static final LinearColorMappingResource COLORMATIC_LAVA_DROP_COLORS = new LinearColorMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/lavadrop.png"));
    public static final LinearColorMappingResource COLORMATIC_DURABILITY_COLORS = new LinearColorMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/durability.png"));
    public static final LinearColorMappingResource COLORMATIC_EXPERIENCE_ORB_COLORS = new LinearColorMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/xporb.png"));
    public static final CustomBiomeColorMappingResource COLORMATIC_CUSTOM_BLOCK_COLORS = new CustomBiomeColorMappingResource();
    public static final GlobalLightMappingResource COLORMATIC_LIGHTMAP_PROPERTIES = new GlobalLightMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "lightmap.json"));
    public static final LightMappingResource COLORMATIC_LIGHTMAPS = new LightMappingResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "lightmap"));
    public static final GlobalColorResource COLORMATIC_COLOR_PROPERTIES = new GlobalColorResource(new ResourceLocation(Bismuth.COLORMATIC_ID, "color"));

    public static void registerResources(ResourceManagerHelper client) {
        client.registerReloadListener(WATER_COLORS);
        client.registerReloadListener(UNDERWATER_COLORS);
        client.registerReloadListener(UNDERLAVA_COLORS);
        client.registerReloadListener(SKY_COLORS);
        client.registerReloadListener(FOG_COLORS);
        client.registerReloadListener(BIRCH_COLORS);
        client.registerReloadListener(SPRUCE_COLORS);
        client.registerReloadListener(REDSTONE_COLORS);
        client.registerReloadListener(PUMPKIN_STEM_COLORS);
        client.registerReloadListener(MELON_STEM_COLORS);
        client.registerReloadListener(MYCELIUM_PARTICLE_COLORS);
        client.registerReloadListener(LAVA_DROP_COLORS);
        client.registerReloadListener(DURABILITY_COLORS);
        client.registerReloadListener(EXPERIENCE_ORB_COLORS);
        client.registerReloadListener(LIGHTMAP_PROPERTIES);
        client.registerReloadListener(LIGHTMAPS);

        client.registerReloadListener(COLORMATIC_WATER_COLORS);
        client.registerReloadListener(COLORMATIC_UNDERWATER_COLORS);
        client.registerReloadListener(COLORMATIC_UNDERLAVA_COLORS);
        client.registerReloadListener(COLORMATIC_SKY_COLORS);
        client.registerReloadListener(COLORMATIC_FOG_COLORS);
        client.registerReloadListener(COLORMATIC_BIRCH_COLORS);
        client.registerReloadListener(COLORMATIC_SPRUCE_COLORS);
        client.registerReloadListener(COLORMATIC_REDSTONE_COLORS);
        client.registerReloadListener(COLORMATIC_PUMPKIN_STEM_COLORS);
        client.registerReloadListener(COLORMATIC_MELON_STEM_COLORS);
        client.registerReloadListener(COLORMATIC_MYCELIUM_PARTICLE_COLORS);
        client.registerReloadListener(COLORMATIC_LAVA_DROP_COLORS);
        client.registerReloadListener(COLORMATIC_DURABILITY_COLORS);
        client.registerReloadListener(COLORMATIC_EXPERIENCE_ORB_COLORS);
        client.registerReloadListener(COLORMATIC_LIGHTMAP_PROPERTIES);
        client.registerReloadListener(COLORMATIC_LIGHTMAPS);
    }

    public static boolean hasCustomWaterColors() {
        return WATER_COLORS.hasCustomColorMapping() || COLORMATIC_WATER_COLORS.hasCustomColorMapping();
    }

    public static boolean hasCustomPumpkinStemColors() {
        return PUMPKIN_STEM_COLORS.hasCustomColorMapping() || COLORMATIC_PUMPKIN_STEM_COLORS.hasCustomColorMapping();
    }

    public static boolean hasCustomMelonStemColors() {
        return MELON_STEM_COLORS.hasCustomColorMapping() || COLORMATIC_MELON_STEM_COLORS.hasCustomColorMapping();
    }

    public static boolean hasCustomRedstoneColors() {
        return REDSTONE_COLORS.hasCustomColorMapping() || COLORMATIC_REDSTONE_COLORS.hasCustomColorMapping();
    }

    public static boolean hasCustomXpOrbColors() {
        return EXPERIENCE_ORB_COLORS.hasCustomColorMapping() || COLORMATIC_EXPERIENCE_ORB_COLORS.hasCustomColorMapping();
    }

    public static boolean hasCustomLavaDropParticleColors() {
        return LAVA_DROP_COLORS.hasCustomColorMapping() || COLORMATIC_LAVA_DROP_COLORS.hasCustomColorMapping();
    }

    public static boolean hasCustomMyceliumParticleColors() {
        return MYCELIUM_PARTICLE_COLORS.hasCustomColorMapping() || COLORMATIC_MYCELIUM_PARTICLE_COLORS.hasCustomColorMapping();
    }

    public static boolean hasCustomUnderLavaColors() {
        return UNDERLAVA_COLORS.hasCustomColorMapping() || COLORMATIC_UNDERLAVA_COLORS.hasCustomColorMapping();
    }

    public static boolean hasCustomUnderWaterColors(){
        return UNDERWATER_COLORS.hasCustomColorMapping() || COLORMATIC_UNDERWATER_COLORS.hasCustomColorMapping();
    }

    public static boolean hasCustomSkyColors() {
        return SKY_COLORS.hasCustomColorMapping() || COLORMATIC_SKY_COLORS.hasCustomColorMapping();
    }

    public static boolean hasCustomFogColors() {
        return FOG_COLORS.hasCustomColorMapping() || COLORMATIC_FOG_COLORS.hasCustomColorMapping();
    }

    public static boolean hasCustomBirchColors() {
        return BIRCH_COLORS.hasCustomColorMapping() || COLORMATIC_BIRCH_COLORS.hasCustomColorMapping();
    }

    public static boolean hasCustomSpruceColors() {
        return SPRUCE_COLORS.hasCustomColorMapping() || COLORMATIC_SPRUCE_COLORS.hasCustomColorMapping();
    }

    public static boolean hasCustomDurabilityColors() {
        return DURABILITY_COLORS.hasCustomColorMapping() || COLORMATIC_DURABILITY_COLORS.hasCustomColorMapping();
    }
}
