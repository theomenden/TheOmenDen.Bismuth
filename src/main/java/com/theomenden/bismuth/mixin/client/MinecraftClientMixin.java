package com.theomenden.bismuth.mixin.client;

import com.theomenden.bismuth.colors.BismuthExtendedColorResolver;
import com.theomenden.bismuth.defaults.DefaultColumns;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Inject(
            method="setLevel",
            at=@At("HEAD")
    )
    private void propagateDynamicRegistry(@Nullable ClientLevel level, CallbackInfo ci) {
        var manager = level == null ? null : level.registryAccess();
        BismuthExtendedColorResolver.setRegistryManager(manager);
        DefaultColumns.reloadDefaultColumnBoundaries(manager);
    }
}
