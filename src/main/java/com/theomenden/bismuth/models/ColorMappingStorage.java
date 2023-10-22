package com.theomenden.bismuth.models;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.theomenden.bismuth.client.Bismuth;
import com.theomenden.bismuth.colors.BismuthExtendedColorResolver;
import com.theomenden.bismuth.colors.interfaces.BismuthResolver;
import com.theomenden.bismuth.colors.interfaces.BismuthResolverProvider;
import com.theomenden.bismuth.colors.mapping.BiomeColorMapping;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public class ColorMappingStorage<T> {
    private final Table<T, ResourceLocation, BiomeColorMapping> colorMappings;
    private final Object2ObjectLinkedOpenHashMap<T, BiomeColorMapping> fallbackColorMappings;
    private final Object2ObjectOpenHashMap<T, BismuthExtendedColorResolver> resolvers;
    private final Object2ObjectLinkedOpenHashMap<T, BismuthResolver>  defaultResolvers;
    private final BismuthResolverProvider<T> defaultResolverProvider;

    public ColorMappingStorage(BismuthResolverProvider<T> defaultResolverProvider) {
        this.colorMappings = HashBasedTable.create();
        this.fallbackColorMappings = new Object2ObjectLinkedOpenHashMap<>();
        this.resolvers = new Object2ObjectOpenHashMap<>();
        this.defaultResolvers = new Object2ObjectLinkedOpenHashMap<>();
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
    public BismuthResolver getBismuthResolver(T key) {
        var extendedResolver = getExtendedResolver(key);

        return extendedResolver != null
                ? extendedResolver.getWrappedResolver()
                : defaultResolvers.computeIfAbsent(key, defaultResolverProvider::create);
    }

    public boolean contains(T key) {
        return !colorMappings.row(key).isEmpty() || fallbackColorMappings.containsKey(key);
    }

    public void addColorMapping(BiomeColorMapping colorMapping, Collection<? extends T> keys, Set<? extends ResourceLocation> biomes) {
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
