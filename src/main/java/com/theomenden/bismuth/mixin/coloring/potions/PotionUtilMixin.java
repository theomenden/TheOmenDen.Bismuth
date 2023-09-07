package com.theomenden.bismuth.mixin.coloring.potions;

import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PotionUtils.class)
public abstract class PotionUtilMixin {
    @ModifyConstant(
            method = "getColor(Ljava/util/Collection;)I",
            constant = @Constant(intValue = 0x385dc6)
    )
    private static int onModifyWaterColor(int waterColor) {
        var colorProperties = ObjectUtils.firstNonNull(
                BismuthColormaticResolution.COLOR_PROPERTIES,
                BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES
        );
        int color = colorProperties.getProperties().getPotion(null);
        return color != 0
                ? color
                : waterColor;
    }
}
