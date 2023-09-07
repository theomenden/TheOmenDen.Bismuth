package com.theomenden.bismuth.mixin.coloring.particles;

import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import com.theomenden.bismuth.utils.MathUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DripParticle.class)
public abstract class LandingLavaParticleMixin {
    @Inject(
            method = "createLavaLandParticle",
            at = @At("RETURN")
    )
    private static void onCreateLandingParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<TextureSheetParticle> cir) {
        if(BismuthColormaticResolution.hasCustomLavaDropParticleColors()) {
            Particle particle = cir.getReturnValue();
            var lavaDropColors = ObjectUtils.firstNonNull(
                    BismuthColormaticResolution.LAVA_DROP_COLORS,
                    BismuthColormaticResolution.COLORMATIC_LAVA_DROP_COLORS
            );
            int color = lavaDropColors
                    .getColorAtIndex(Integer.MAX_VALUE);

            float red = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float green = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float blue = (color & 0xff) * MathUtils.INV_255;
            particle.setColor(red, green, blue);
        }
    }
}
