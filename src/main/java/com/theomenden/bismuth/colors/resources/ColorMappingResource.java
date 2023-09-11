package com.theomenden.bismuth.colors.resources;

import com.theomenden.bismuth.client.Bismuth;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.resources.LegacyStuffWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ColorMappingResource implements SimpleResourceReloadListener<int[]> {
    private static final Logger logger = LoggerFactory.getLogger(Bismuth.MODID);
    private final ResourceLocation resourceLocation;
    private final ResourceLocation optifineLocation;
    private final ResourceLocation colormaticLocation;
    protected int[] colorMapping = null;

    public ColorMappingResource(ResourceLocation location) {
        this.resourceLocation = location;
        this.optifineLocation = new ResourceLocation("minecraft", "optifine/" + location.getPath());
        this.colormaticLocation = new ResourceLocation(Bismuth.COLORMATIC_ID, resourceLocation.getPath());
    }

    @SuppressWarnings("deprecation")
    @Override
    public CompletableFuture<int[]> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            int[] resultingNamespaceLoad;

            try {
                resultingNamespaceLoad = attemptToLoadFromColormaticNamespace(manager);
                if(resultingNamespaceLoad == null) {
                    resultingNamespaceLoad = LegacyStuffWrapper.getPixels(manager, this.resourceLocation);
                }
            }
            catch(IOException e) {
                resultingNamespaceLoad =  attemptToLoadFromOptifineDirectory(manager);
            }
            return resultingNamespaceLoad;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(int[] data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> colorMapping = data, executor);
    }

    @Override
    public ResourceLocation getFabricId() {
        return resourceLocation;
    }

    @SuppressWarnings("deprecation")
    private int @Nullable [] attemptToLoadFromColormaticNamespace(ResourceManager manager) {
        try{
            return LegacyStuffWrapper.getPixels(manager, colormaticLocation);
        }
        catch (IOException e) {
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    private int @Nullable [] attemptToLoadFromOptifineDirectory(ResourceManager manager) {
        try {
            return LegacyStuffWrapper.getPixels(manager, optifineLocation);
        } catch (IOException e) {
            return null;
        }
    }

    public boolean hasCustomColorMapping() {
        return colorMapping != null;
    }
}
