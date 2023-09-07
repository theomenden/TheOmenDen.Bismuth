package com.theomenden.bismuth.mixin.client;

import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Style.class)
public abstract class ChatStyleMixin {

    @Inject(
            method = "getColor",
            at = @At("RETURN"),
            cancellable = true
    )
    private void switchToCustomTextColor(CallbackInfoReturnable<TextColor> cir) {
        if(cir.getReturnValue() != null) {
            String name = cir.getReturnValue().toString();

            if(name != null) {
                var colorProperties = ObjectUtils.firstNonNull(
                        BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                        BismuthColormaticResolution.COLOR_PROPERTIES
                );

                ChatFormatting formatting = ChatFormatting.getByName(name);

                if(formatting != null)  {
                    TextColor color = colorProperties.getProperties()
                                                     .getText(formatting);

                    if(color != null) {
                        cir.setReturnValue(color);
                    }
                }
            }
        }
    }
}
