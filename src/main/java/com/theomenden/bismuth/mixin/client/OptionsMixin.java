package com.theomenden.bismuth.mixin.client;

import com.theomenden.bismuth.client.Bismuth;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Shadow @Final
    private OptionInstance<Integer> biomeBlendRadius;

    @Inject(method = "processOptions", at = @At("HEAD"))
    private void preProccessingOptions(Options.FieldAccess accessor, CallbackInfo ci) {
        accessor.process("bismuthBlendingRadius", Bismuth.configuration.blendingRadius);
    }
}
