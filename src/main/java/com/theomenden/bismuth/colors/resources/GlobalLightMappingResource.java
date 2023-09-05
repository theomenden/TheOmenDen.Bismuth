package com.theomenden.bismuth.colors.resources;

import com.theomenden.bismuth.colors.properties.LightMappingProperties;
import lombok.Getter;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class GlobalLightMappingResource  implements SimpleSynchronousResourceReloadListener {
    private final ResourceLocation identifier;
    @Getter
    private LightMappingProperties properties;

    public GlobalLightMappingResource(ResourceLocation identifier) {
        this.identifier = identifier;
    }

    @Override
    public ResourceLocation getFabricId() {
        return this.identifier;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        properties = LightMappingProperties.loadPropertiesForIdentifier(resourceManager, this.identifier);
    }
}