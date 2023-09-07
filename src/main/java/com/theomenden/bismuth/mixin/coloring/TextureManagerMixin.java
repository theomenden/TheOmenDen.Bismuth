package com.theomenden.bismuth.mixin.coloring;

import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {

    @Dynamic("Post reload method in lambda")
    @Inject(
            method = "method_18167",
            at = @At("HEAD")
    )
    private void onReloading(ResourceManager resourceManager, Executor executor, CompletableFuture completableFuture, Void v, CallbackInfo ci) {
        BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES.onResourceManagerReload(resourceManager);
        BismuthColormaticResolution.COLOR_PROPERTIES.onResourceManagerReload(resourceManager);
    }
}
