package com.theomenden.bismuth.mixin.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.theomenden.bismuth.mixin.coloring.xp.ExperienceOrbAccessor;
import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import com.theomenden.bismuth.utils.MathUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbRenderer.class)
public abstract class ExperienceOrbRendererMixin extends EntityRenderer<ExperienceOrb> {

    protected ExperienceOrbRendererMixin() {
        super(null);
    }

    @Unique
    private static boolean isCustom;

    @Unique
    private static int customRed;

    @Unique
    private static int customGreen;

    @Unique
    private static int customBlue;

    @Inject(
            method = "render(Lnet/minecraft/world/entity/ExperienceOrb;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD")
    )
    private void onRendering(ExperienceOrb entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if(BismuthColormaticResolution.hasCustomXpOrbColors()) {
            isCustom = true;
            var colorProperties = ObjectUtils.firstNonNull(
              BismuthColormaticResolution.COLOR_PROPERTIES,
              BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES
            );

            var xpOrbColors = ObjectUtils.firstNonNull(
                    BismuthColormaticResolution.EXPERIENCE_ORB_COLORS,
                    BismuthColormaticResolution.COLORMATIC_EXPERIENCE_ORB_COLORS
            );

            float ticksPerCycle = colorProperties.getProperties().getXpOrbTime() * MathUtils.INV_50F;
            float frac = (1 - Mth.cos(((ExperienceOrbAccessor)(Object)entity).getAge() + partialTicks)* MathUtils.PI2 / ticksPerCycle) / 2;
            int color = xpOrbColors.getFractionalColorMapping(frac);

            customRed = (color >> 16) & 0xff;
            customGreen = (color >> 8) & 0xff;
            customBlue  = color & 0xff;
        } else {
            isCustom = false;
        }
    }

    @Redirect(
            method = "vertex",
            at = @At(
                    value = "INVOKE",
                    target="Lcom/mojang/blaze3d/vertex/VertexConsumer;color(IIII)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            )
    )
    private static VertexConsumer proxyVertexColorCalculation(VertexConsumer instance, int r, int g, int b, int a) {
        if(isCustom) {
            r = customRed;
            g = customGreen;
            b = customBlue;
        }
        return instance.color(r, g, b,a);
    }
}
