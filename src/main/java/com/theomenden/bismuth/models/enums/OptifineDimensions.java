package com.theomenden.bismuth.models.enums;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum OptifineDimensions implements StringRepresentable {
    OVERWORLD("world0"),
    NETHER("world-1"),
    END("world1");

    private final String name;

    OptifineDimensions(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
