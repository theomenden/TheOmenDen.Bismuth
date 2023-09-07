package com.theomenden.bismuth.mixin.client;

import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import net.minecraft.client.gui.Gui;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @ModifyConstant(
            method = "renderExperienceBar",
            constant = @Constant(intValue = 8453920)
    )
    private int getExperienceTextColor(int original) {
        var xpTextProperties = ObjectUtils.firstNonNull(
                BismuthColormaticResolution.COLOR_PROPERTIES,
                BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES
        );

        int color = xpTextProperties.getProperties().getXpText();
        return color != 0
                ? color
                : original;
    }
}
