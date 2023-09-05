package com.theomenden.bismuth.colors.resources;

import com.theomenden.bismuth.client.Bismuth;
import com.theomenden.bismuth.colors.properties.GlobalColorProperties;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class GlobalColorResource implements SimpleSynchronousResourceReloadListener {
    private final ResourceLocation identifier;
    private final ResourceLocation optifineId;
    private final ResourceLocation colormaticId;
    private GlobalColorProperties globalColorProperties = GlobalColorProperties.DEFAULT;

    public GlobalColorResource(ResourceLocation identifier) {
        this.identifier = new ResourceLocation(identifier.getNamespace(), identifier.getPath() + ".json");
        this.optifineId = new ResourceLocation("minecraft", "optifine/" + identifier.getPath() + ".properties");
        this.colormaticId = new ResourceLocation(Bismuth.COLORMATIC_ID, identifier.getPath());
    }

    public GlobalColorProperties getProperties() {
        return globalColorProperties;
    }

    @Override
    public ResourceLocation getFabricId() {
        return identifier;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        GlobalColorProperties properties = GlobalColorProperties.load(resourceManager, identifier, false);

        if (properties == null) {
            properties = GlobalColorProperties.load(resourceManager, colormaticId, false);
        }

        if (properties == null) {
            properties = GlobalColorProperties.load(resourceManager, optifineId, true);
        }

        this.globalColorProperties = properties;
    }
}