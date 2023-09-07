package com.theomenden.bismuth.mixin.coloring.particles;

import com.theomenden.bismuth.colors.mapping.BiomeColorMapping;
import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import com.theomenden.bismuth.utils.MathUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashParticle.class)
public abstract class WaterSplashParticleMixin extends TextureSheetParticle {

    @Unique
    private static final BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();
    protected WaterSplashParticleMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo ci) {
        if(!BismuthColormaticResolution.hasCustomWaterColors()) {
            return;
        }

        BiomeColorMapping colorMapping = ObjectUtils.firstNonNull(BismuthColormaticResolution.WATER_COLORS.getColorMapping(), BismuthColormaticResolution.COLORMATIC_WATER_COLORS.getColorMapping());

        position.set(this.x, this.y, this.z);

        int color = BiomeColorMapping.getBiomeCurrentColorOrDefault(this.level, position, colorMapping);

        this.rCol = ((color >> 16) & 0xff) * MathUtils.INV_255;
        this.gCol = ((color >> 8) & 0xff) * MathUtils.INV_255 ;
        this.bCol = (color & 0xff) * MathUtils.INV_255;
    }

}
