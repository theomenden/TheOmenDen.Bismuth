package com.theomenden.bismuth.utils;

import com.theomenden.bismuth.models.records.BiomeColorTypes;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.ColorResolver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class CompatibilityUtils {
    public static final boolean IS_SODIUM_LOADED = FabricLoader
            .getInstance()
            .isModLoaded("sodium");
    
    public static final boolean IS_FABRIC_FLUID_API_LOADED = FabricLoader
            .getInstance()
            .isModLoaded("fabric-rendering-fluids-v1");

    public static final ReentrantLock lock = new ReentrantLock();
    public static final ConcurrentHashMap<ColorResolver, Integer> knownColorResolvers = new ConcurrentHashMap<>();
    public static int nextColorResolverID = BiomeColorTypes.LAST + 1;

    public static int getNextColorResolverID() {
        return nextColorResolverID++;
    }

    public static int addColorResolver(ColorResolver resolver) {
        lock.lock();
        int result;

        if(!knownColorResolvers.containsKey(resolver)) {
            result = getNextColorResolverID();
            knownColorResolvers.put(resolver, result);
        } else {
            result = knownColorResolvers.get(resolver);
        }

        lock.unlock();
        return result;
    }


    public static int getColorType(ColorResolver resolver) {
        Integer result = knownColorResolvers.get(resolver);

        if(result == null) {
            result = addColorResolver(resolver);
        }

        return  result;
    }

}
