package com.theomenden.bismuth.mixin.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Redirect(
            method = "renderGuiItemDecorations(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target="Lnet/minecraft/client/gui/GuiComponent;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/item/ItemStack;isBarVisible()Z"
                    )
            )
    )
    private void renderQuadProxy(ItemStack stack) {
        if(BismuthColormaticResolution.hasCustomDurabilityColors()) {
            var durabilityColors = ObjectUtils.firstNonNull(
                    BismuthColormaticResolution.DURABILITY_COLORS,
                    BismuthColormaticResolution.COLORMATIC_DURABILITY_COLORS
            );
            float damage = instance.getDamageValue();
            float maxDamage = instance.getMaxDamage();
            float durability = Math.max(0.0f, (maxDamage - damage) / maxDamage);
            int color = durabilityColors.getFractionalColorMapping(durability);
            int r = (color >> 16) & 0xff;
            int g = (color >>  8) & 0xff;
            int b = color  & 0xff;
        }
        return 0;
    }
    }
}
