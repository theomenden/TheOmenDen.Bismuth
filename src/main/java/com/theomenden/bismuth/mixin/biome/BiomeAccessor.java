package com.theomenden.bismuth.mixin.biome;

import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "Lnet/minecraft/world/biome/Biome;weather:Lnet/minecraft/world/biome/Biome$Weather;")
public interface BiomeAccessor {
    @Accessor
    float getTemperature();

    @Accessor
    float getDownfall();
}
