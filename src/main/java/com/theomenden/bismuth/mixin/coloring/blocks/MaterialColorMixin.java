package com.theomenden.bismuth.mixin.coloring.blocks;

import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import net.minecraft.world.level.material.MaterialColor;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MaterialColor.class)
public abstract class MaterialColorMixin {
    @Inject(
            method="calculateRGBColor",
            at=@At(
                    value="FIELD",
                    target="Lnet/minecraft/world/level/material/MaterialColor$Brightness;modifier:I",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void onCalculatingColor(MaterialColor.Brightness brightness, CallbackInfoReturnable<Integer> cir){
        var colorProperties = ObjectUtils.firstNonNull(
                BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                BismuthColormaticResolution.COLOR_PROPERTIES
        );

        int color = colorProperties.getProperties().getMap((MaterialColor)(Object)this);
        if(color != 0) {
            int scalar = brightness.id;
            int red = ((color >> 16) & 0xff) * scalar / 255;
            int green = ((color >> 8) & 0xff) * scalar / 255;
            int blue = (color & 0xff) * scalar / 255;
            cir.setReturnValue(0xff000000 | (blue << 16) | (green << 8) | red );
        }
    }
}
