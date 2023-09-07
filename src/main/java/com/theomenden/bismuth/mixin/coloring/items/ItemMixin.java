package com.theomenden.bismuth.mixin.coloring.items;

import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin{
    @Inject(
            method = "getBarColor",
            at = @At("RETURN"),
            cancellable = true
    )
    private void onGetItemBar(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if(BismuthColormaticResolution.hasCustomDurabilityColors()) {
            float damage = stack.getDamageValue();
            float maxDamage = stack.getMaxDamage();
            float durability = Math.max(0.0f, (maxDamage - damage) / maxDamage);
            var durabilityColors = ObjectUtils.firstNonNull(
                    BismuthColormaticResolution.DURABILITY_COLORS,
                    BismuthColormaticResolution.COLORMATIC_DURABILITY_COLORS
            );
            int color = durabilityColors.getFractionalColorMapping(durability);

            cir.setReturnValue(color);
        }
    }
}
