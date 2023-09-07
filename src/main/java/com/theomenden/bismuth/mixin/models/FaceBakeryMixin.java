package com.theomenden.bismuth.mixin.models;

import com.theomenden.bismuth.colors.decorators.ModelIdContext;
import net.minecraft.client.renderer.block.model.FaceBakery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FaceBakery.class)
public abstract class FaceBakeryMixin {
    @ModifyArg(
            method="bakeQuad",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/model/BakedQuad;<init>([IILnet/minecraft/core/Direction;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Z)V"
            ),
            index = 1
    )
    private int addTintToCustomColoredModel(int tintIndex) {
        return ModelIdContext.shouldTintCurrentModel
                ? 0
                : tintIndex;
    }
}
