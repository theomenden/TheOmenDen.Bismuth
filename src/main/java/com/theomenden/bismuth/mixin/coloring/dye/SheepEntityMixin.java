package com.theomenden.bismuth.mixin.coloring.dye;

import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(Sheep.class)
public abstract class SheepEntityMixin extends Animal {
    private SheepEntityMixin() {
        super(null, null);
    }

    @Redirect(
            method = "getColorArray",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;",
                    remap = false
            )
    )
    private static Object proxyRgb(Map<DyeColor, float[]> instance, Object o) {
        var colorProperties = ObjectUtils.firstNonNull(
                BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                BismuthColormaticResolution.COLOR_PROPERTIES
        );

        float[] rgb = colorProperties.getProperties()
                                     .getWoolRgb((DyeColor) o);

        return rgb != null
                ? rgb
                : instance.get(o);
    }
}
