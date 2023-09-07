package com.theomenden.bismuth.mixin.coloring.potions;

import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import net.minecraft.world.effect.MobEffect;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffect.class)
public abstract class MobEffectMixin {
    @Inject(
            method = "getColor",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onGetColor(CallbackInfoReturnable<Integer> cir) {
        MobEffect self = (MobEffect) (Object)this;

        var statusEffectColors = ObjectUtils.firstNonNull(
                BismuthColormaticResolution.COLOR_PROPERTIES,
                BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES
        );

        int color = statusEffectColors.getProperties().getPotion(self);

        if(color != 0) {
            cir.setReturnValue(color);
        }


    }
}
