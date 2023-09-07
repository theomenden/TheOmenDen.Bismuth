package com.theomenden.bismuth.mixin.coloring.particles;

import com.theomenden.bismuth.models.enums.ColoredParticle;
import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import com.theomenden.bismuth.utils.MathUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PortalParticle.class)
public abstract class PortalParticleMixin extends TextureSheetParticle {

    protected PortalParticleMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Inject(method ="<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo ci) {
        var colorProperties = ObjectUtils.firstNonNull(
                BismuthColormaticResolution.COLOR_PROPERTIES,
                BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES
        );
        int color = colorProperties
                .getProperties()
                .getParticle(ColoredParticle.PORTAL);

        if(color != 0) {
            float multi = this.bCol;
            this.rCol = multi * (((color >> 16) & 0xff) * MathUtils.INV_255);
            this.gCol = multi * (((color >> 8) & 0xff) * MathUtils.INV_255);
            this.bCol = multi * ((color  & 0xff) * MathUtils.INV_255);
        }
    }
}
