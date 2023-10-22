package com.theomenden.bismuth.models.enums;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum Format implements StringRepresentable {
    FIXED("fixed"),
    VANILLA("vanilla"),
    GRID("grid");

    private final String name;

    Format(String s) {
        this.name = s;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
