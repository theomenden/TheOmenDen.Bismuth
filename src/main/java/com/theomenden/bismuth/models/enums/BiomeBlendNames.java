package com.theomenden.bismuth.models.enums;

import lombok.Getter;
import net.minecraft.util.StringRepresentable;

public enum BiomeBlendNames implements StringRepresentable {
    BURLY("text.autoconfig.bismuth.option.blendingRadius.17", 8),
    EXTENSIVE("text.autoconfig.bismuth.option.blendingRadius.19", 9),
    ELEPHANTINE("text.autoconfig.bismuth.option.blendingRadius.21", 10),
    ENORMOUS("text.autoconfig.bismuth.option.blendingRadius.23", 11),
    MAMMOTH("text.autoconfig.bismuth.option.blendingRadius.25",12),
    IMMENSE("text.autoconfig.bismuth.option.blendingRadius.27",13),
    BROBDINGNAGIAN("text.autoconfig.bismuth.option.blendingRadius.29",14);

    private final String name;
    @Getter
    private final int value;
    private BiomeBlendNames(String name, int value){ this.name = name; this.value = value;}

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
