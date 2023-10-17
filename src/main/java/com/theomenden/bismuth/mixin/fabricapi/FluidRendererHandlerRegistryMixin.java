package com.theomenden.bismuth.mixin.fabricapi;

import com.theomenden.bismuth.colors.decorators.BismuthFluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = FluidRenderHandlerRegistryImpl.class, remap = false)
public abstract class FluidRendererHandlerRegistryMixin {
    @ModifyVariable(
            method="register",
            at=@At("HEAD"),
            ordinal = 0)
    private FluidRenderHandler onFluidRenderingRegistration(FluidRenderHandler delegate) {
        return new BismuthFluidRenderHandler(delegate);
    }
}
