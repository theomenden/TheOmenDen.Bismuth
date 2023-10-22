package com.theomenden.bismuth.models.enums;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ColumnLayout implements StringRepresentable {
    DEFAULT("default"),
    OPTIFINE("optifine"),
    LEGACY("legacy"),
    STABLE("stable");

    private final String name;

    ColumnLayout(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
