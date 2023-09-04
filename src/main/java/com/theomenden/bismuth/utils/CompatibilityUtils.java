package com.theomenden.bismuth.utils;

import net.fabricmc.loader.api.FabricLoader;

public final class CompatibilityUtils {
    public static final boolean IS_SODIUM_LOADED = FabricLoader
            .getInstance()
            .isModLoaded("sodium");
}
