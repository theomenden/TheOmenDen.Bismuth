package com.theomenden.bismuth.models.enums;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum InternalEventType implements StringRepresentable {
    COLOR("Color Generation"),
    SUBEVENT("Sub Event");

    InternalEventType(String name) {
        this.name = name;
    }

    private final String name;

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
