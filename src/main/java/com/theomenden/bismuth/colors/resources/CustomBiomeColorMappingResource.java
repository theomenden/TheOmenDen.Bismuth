package com.theomenden.bismuth.colors.resources;

import com.theomenden.bismuth.client.Bismuth;
import com.theomenden.bismuth.colors.mapping.BiomeColorMapping;
import com.theomenden.bismuth.colors.mapping.BiomeColorMappings;
import com.theomenden.bismuth.models.exceptions.InvalidColorMappingException;
import com.theomenden.bismuth.models.records.PropertyImage;
import com.theomenden.bismuth.utils.GsonUtils;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.regex.Pattern;

public class CustomBiomeColorMappingResource implements SimpleSynchronousResourceReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bismuth.class);
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[a-z0-9_/.-]+");
    private final ResourceLocation identifier;
    private final ResourceLocation optifineIdentifier;
    private final ResourceLocation colormaticIdentifier;
    private final ResourceLocation otherOptifineIdentifier;

    public CustomBiomeColorMappingResource() {
        this.identifier = new ResourceLocation(Bismuth.MODID, "colormap/custom");
        this.colormaticIdentifier = new ResourceLocation(Bismuth.COLORMATIC_ID, "colormap/custom");
        this.optifineIdentifier = new ResourceLocation("minecraft", "optifine/colormap/custom");
        this.otherOptifineIdentifier = new ResourceLocation("minecraft", "optifine/colormap/blocks");
    }

    @Override
    public ResourceLocation getFabricId() {
        return identifier;
    }

    private static void addColorMappings(ResourceManager manager, ResourceLocation directory, boolean isInJson) {
        String extension = isInJson? ".json" : ".properties";
        Collection<ResourceLocation> files = manager.listResources(directory.getPath(),
                                                      id -> id.getNamespace().equals(directory.getNamespace())
                                                              && (id.getPath().endsWith(extension) || id.getPath().endsWith(".png")))
                                              .keySet()
                                              .stream()
                                              .map(id -> {
                                                  String path = id.getPath();
                                                  if(path.endsWith(".png")) {
                                                      String standardizedPath = path.substring(0, path.length() - 4) + extension;
                                                      return new ResourceLocation(id.getNamespace(), standardizedPath);
                                                  }
                                                  return id;
                                              })
                                              .distinct()
                                              .toList();

        files.forEach(id -> {
            if (!IDENTIFIER_PATTERN
                    .matcher(id.getPath())
                    .matches()) {
                LOGGER.error("ColorMapping definition file '{}' does not name a valid resource location. Please have the resource pack author fix this.", id);
            }
            try {
                PropertyImage pi = GsonUtils.loadColorMapping(manager, id, true);
                BiomeColorMapping colorMapping = new BiomeColorMapping(pi.properties(), pi.image());
                BiomeColorMappings.addBiomeColorMapping(colorMapping);
            } catch (InvalidColorMappingException e) {
                LOGGER.error("Error parsing {}: {}", id, e.getMessage());
            }
        });
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        BiomeColorMappings.resetColorMappings();
        addColorMappings(resourceManager, otherOptifineIdentifier, false);
        addColorMappings(resourceManager, optifineIdentifier, false);
        addColorMappings(resourceManager, colormaticIdentifier, true);
        addColorMappings(resourceManager, identifier, true);
    }
}