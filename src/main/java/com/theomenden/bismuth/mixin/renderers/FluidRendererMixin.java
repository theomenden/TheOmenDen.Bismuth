package com.theomenden.bismuth.mixin.renderers;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.theomenden.bismuth.colors.mapping.BiomeColorMappings;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LiquidBlockRenderer.class)
public abstract class FluidRendererMixin {

    @ModifyVariable(
            method = "tesselate(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V",
            at = @At("STORE"),
            ordinal = 1
    )
    private int onAfterWaterColoring(int original, BlockAndTintGetter world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
        var canonicalBlockState = fluidState.createLegacyBlock();
        if(BiomeColorMappings.isCustomColored(canonicalBlockState)){
            return BiomeColorMappings.getBiomeColorMapping(canonicalBlockState, world, pos);
        }
      return original;
    }
}
