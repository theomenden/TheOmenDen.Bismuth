package com.theomenden.bismuth.mixin.coloring.particles;

import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import com.theomenden.bismuth.utils.MathUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DripParticle.class)
public abstract class DrippingLavaParticleMixin extends TextureSheetParticle{

    @Unique
    private int age;

    protected DrippingLavaParticleMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void onConstructor(ClientLevel level, double x, double y, double z, Fluid type, CallbackInfo ci) {
        age = 0;
        if(BismuthColormaticResolution.hasCustomLavaDropParticleColors()) {
            var lavaDropColors = ObjectUtils.firstNonNull(
                    BismuthColormaticResolution.LAVA_DROP_COLORS,
                    BismuthColormaticResolution.COLORMATIC_LAVA_DROP_COLORS
            );
            int color = lavaDropColors.getColorAtIndex(0);

            float red = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float green = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float blue = (color & 0xff) * MathUtils.INV_255;
            this.setColor(red, green, blue);
        }
    }

    @Inject(
            method="preMoveUpdate",
            at = @At(
                    value = "RETURN",
                    shift = At.Shift.BY,
                    by = -2
            )
    )
    private void onUpdateAge(CallbackInfo ci) {
        if(BismuthColormaticResolution.hasCustomLavaDropParticleColors()) {
            var lavaDropColors = ObjectUtils.firstNonNull(
                    BismuthColormaticResolution.LAVA_DROP_COLORS,
                    BismuthColormaticResolution.COLORMATIC_LAVA_DROP_COLORS
            );
            int color = lavaDropColors.getColorAtIndex(++age);
            float red = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float green = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float blue = (color & 0xff) * MathUtils.INV_255;
            this.setColor(red, green, blue);
        }
    }

}
