package com.theomenden.bismuth.client;

import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import com.theomenden.bismuth.utils.DebugUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bismuth implements ClientModInitializer {

    private static final Logger logger = LogManager.getLogger(Bismuth.class);
    public static BismuthConfig configuration;
    public static final String MODID = "bismuth";
    public static final String COLORMATIC_ID = "colormatic";
    public static final ResourceLocation OVERWORLD = ResourceLocation.tryBuild("minecraft","overworld");

    public static OptionInstance<Integer> bismuthBlendingRadius = new OptionInstance<>(
            "text.autoconfig.bismuth.option.blendingRadius",
            OptionInstance.noTooltip(),
            (component, integer) -> {
                int diameter = integer * 2 + 1;
                return Options.genericValueLabel(component, Component.translatable("text.autoconfig.bismuth.option.blendingRadius." + diameter));
            },
            new OptionInstance.IntRange(0, 14),
            14,
            (integer) -> Minecraft.getInstance().levelRenderer.allChanged());

    public static int
    getBiomeBlendRadius()
    {
        return bismuthBlendingRadius.get();
    }


    public static ResourceLocation getDimensionId(Level level) {
        DimensionType type = level.dimensionType();

        ResourceLocation id = level.registryAccess()
                .registryOrThrow(Registries.DIMENSION_TYPE)
                .getKey(type);

        if(id == null) {
            id = OVERWORLD;
        }

        return id;
    }

    public static ResourceKey<Biome> getBiomeResourceKey(RegistryAccess registryAccess, Biome biome) {
        return registryAccess.registryOrThrow(Registries.BIOME)
                .getResourceKey(biome)
                .orElse(Biomes.PLAINS);
    }

    public static ResourceLocation getBiomeIdentifier(RegistryAccess manager, Biome biome) {
        var resourceLocation = manager
                .registryOrThrow(Registries.BIOME)
                .getKey(biome);

        if(resourceLocation == null) {
            resourceLocation = Biomes.PLAINS.location();
        }

        return resourceLocation;
    }

    public static <T> T getRegistryValue(Registry<T> registry, Holder<T> entry) {
        var keyHolder = entry.unwrapKey();

        if(keyHolder.isPresent()) {
            return registry.get(keyHolder.get());
        }

        return entry.value();
    }
    @Override
    public void onInitializeClient() {
        AutoConfig.register(BismuthConfig.class, GsonConfigSerializer::new);
        configuration = AutoConfig
                .getConfigHolder(BismuthConfig.class)
                .getConfig();

        ResourceManagerHelper client = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
        BismuthColormaticResolution.registerResources(client);
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
                DebugUtils.registerCommands(dispatcher)));
        logger.info("Bismuth crystalized, refracting colors");
    }
}
