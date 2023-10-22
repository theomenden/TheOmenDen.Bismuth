package com.theomenden.bismuth.models.enums;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ColoredParticle implements StringRepresentable {
    WATER("water"),
    LAVA("lava"),
    PORTAL("portal");

    private final String name;

    private ColoredParticle(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
