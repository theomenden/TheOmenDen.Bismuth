package com.theomenden.bismuth.models.records;

import com.mojang.blaze3d.platform.NativeImage;
import com.theomenden.bismuth.models.ColorMappingProperties;

public record PropertyImage(ColorMappingProperties properties, NativeImage image) {
}
