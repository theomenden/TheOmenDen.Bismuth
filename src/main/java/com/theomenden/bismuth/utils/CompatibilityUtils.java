package com.theomenden.bismuth.utils;

import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;

public final class CompatibilityUtils {
    public static final boolean IS_SODIUM_LOADED = FabricLoader
            .getInstance()
            .isModLoaded("sodium");
    
    public static final boolean IS_FABRIC_FLUID_API_LOADED = FabricLoader
            .getInstance()
            .isModLoaded("fabric-rendering-fluids-v1");
}
