package com.theomenden.bismuth.models;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.theomenden.bismuth.client.Bismuth;
import com.theomenden.bismuth.colors.BismuthExtendedColorResolver;
import com.theomenden.bismuth.colors.interfaces.BismuthResolver;
import com.theomenden.bismuth.colors.interfaces.BismuthResolverProvider;
import com.theomenden.bismuth.colors.mapping.BiomeColorMapping;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.intellij.lang.annotations.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ColorMappingStorage<T> {
    private final Table<T, ResourceLocation, BiomeColorMapping> colorMappings;
    private final Map<T, BiomeColorMapping> fallbackColorMappings;
    private final Map<T, BismuthExtendedColorResolver> resolvers;
    private final Map<T, BismuthResolver> defaultResolvers;
    private final BismuthResolverProvider<T> defaultResolverProvider;

    public ColorMappingStorage(BismuthResolverProvider<T> defaultResolverProvider) {
        this.colorMappings = HashBasedTable.create();
        this.fallbackColorMappings = Maps.newHashMap();
        this.resolvers = Maps.newHashMap();
        this.defaultResolvers = Maps.newHashMap();
        this.defaultResolverProvider = defaultResolverProvider;
    }

    @Nullable
    public BiomeColorMapping getColorMapping(RegistryAccess manager, T key, Biome biome) {
        var resultingMapping = this.colorMappings.get(key, Bismuth.getBiomeIdentifier(manager, biome));

        return resultingMapping == null
                ? this.fallbackColorMappings.get(key)
                : resultingMapping;
    }

    @Nullable
    public BiomeColorMapping getFallbackColorMapping(T key) {
        return this.fallbackColorMappings.get(key);
    }

    @Nullable
    public BismuthExtendedColorResolver getExtendedResolver(T key) {
        return this.resolvers.get(key);
    }

    @Nullable
    public BismuthResolver getVanadiumResolver(T key) {
        var extendedResolver = getExtendedResolver(key);

        return extendedResolver != null
                ? extendedResolver.getWrappedResolver()
                : defaultResolvers.computeIfAbsent(key, defaultResolverProvider::create);
    }

    public boolean contains(T key) {
        return !colorMappings.row(key).isEmpty() || fallbackColorMappings.containsKey(key);
    }

    public void addColorMapping(BiomeColorMapping colorMapping, Collection<? extends T> keys, Set<? extends Identifier> biomes) {
        if(biomes.isEmpty()){
            keys
                    .forEach(key -> {
                        fallbackColorMappings.put(key, colorMapping);
                        resolvers.put(key, new BismuthExtendedColorResolver(this, key, defaultResolvers.computeIfAbsent(key, defaultResolverProvider::create)));
                    });
            return;
        }

        keys.forEach(key -> {
            biomes.forEach(b -> colorMappings.put(key, b, colorMapping));
            resolvers.put(key, new BismuthExtendedColorResolver(this, key, defaultResolvers.computeIfAbsent(key, defaultResolverProvider::create)));
        });

    }

    public void clearMappings() {
        colorMappings.clear();
        fallbackColorMappings.clear();
        resolvers.clear();
        defaultResolvers.clear();
    }
}
