package com.theomenden.bismuth.mixin.coloring.blocks;

import com.theomenden.bismuth.colors.mapping.BiomeColorMapping;
import com.theomenden.bismuth.colors.mapping.BiomeColorMappings;
import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockColors.class)
public abstract class BlockColorsMixin {
    @Inject(
            method = "method_1687",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void onCalculatingBirchColor(BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, int i, CallbackInfoReturnable<Integer> cir) {
        if(BismuthColormaticResolution.hasCustomBirchColors()) {
            var birchColors = ObjectUtils.firstNonNull(BismuthColormaticResolution.BIRCH_COLORS, BismuthColormaticResolution.COLORMATIC_BIRCH_COLORS);
            int color = BiomeColorMapping
                    .getBiomeCurrentColorOrDefault(blockAndTintGetter, blockPos, birchColors.getColorMapping());
            cir.setReturnValue(color);
        }
    }

    @Inject(method = "method_1695", at = @At("HEAD"), cancellable = true)
    private static void onCalculatingSpruceColor(BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, int i, CallbackInfoReturnable<Integer> cir){
        if(BismuthColormaticResolution.hasCustomSpruceColors()) {
            var spruceColors = ObjectUtils.firstNonNull(BismuthColormaticResolution.SPRUCE_COLORS, BismuthColormaticResolution.COLORMATIC_SPRUCE_COLORS);
            int color = BiomeColorMapping.getBiomeCurrentColorOrDefault(
                    blockAndTintGetter, blockPos, spruceColors.getColorMapping());
            cir.setReturnValue(color);
        }
    }

    @Dynamic("attached stem foliage lambda method")
    @Inject(method = "method_1698", at = @At("HEAD"), cancellable = true)
    private static void onAttachedStemColor(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> info) {
        Block block = state.getBlock();
        if(block == Blocks.ATTACHED_PUMPKIN_STEM && BismuthColormaticResolution.hasCustomPumpkinStemColors()) {
            var pumpkinStemColors = ObjectUtils.firstNonNull(BismuthColormaticResolution.PUMPKIN_STEM_COLORS, BismuthColormaticResolution.COLORMATIC_PUMPKIN_STEM_COLORS);
            info.setReturnValue(pumpkinStemColors.getColorAtIndex(Integer.MAX_VALUE));
        } else if(block == Blocks.ATTACHED_MELON_STEM && BismuthColormaticResolution.hasCustomMelonStemColors()) {
            var melonStemColors = ObjectUtils.firstNonNull(BismuthColormaticResolution.MELON_STEM_COLORS, BismuthColormaticResolution.COLORMATIC_MELON_STEM_COLORS);
            info.setReturnValue(melonStemColors.getColorAtIndex(Integer.MAX_VALUE));
        }
    }

    @Dynamic("stem foliage lambda method")
    @Inject(method = "method_1696", at = @At("HEAD"), cancellable = true)
    private static void onStemColor(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> info) {
        Block block = state.getBlock();
        if(block == Blocks.PUMPKIN_STEM && BismuthColormaticResolution.hasCustomPumpkinStemColors()) {
            var pumpkinStemColors = ObjectUtils.firstNonNull(BismuthColormaticResolution.PUMPKIN_STEM_COLORS, BismuthColormaticResolution.COLORMATIC_PUMPKIN_STEM_COLORS);
            int age = state.getValue(StemBlock.AGE);
            info.setReturnValue(pumpkinStemColors.getColorAtIndex(age));
        } else if(block == Blocks.MELON_STEM && BismuthColormaticResolution.hasCustomMelonStemColors()) {
            int age = state.getValue(StemBlock.AGE);
            var melonStemColors = ObjectUtils.firstNonNull(BismuthColormaticResolution.MELON_STEM_COLORS, BismuthColormaticResolution.COLORMATIC_MELON_STEM_COLORS);
            info.setReturnValue(melonStemColors.getColorAtIndex(age));
        }
    }

    @Dynamic("Lily pad lambda method")
    @Inject(method = "method_1684", at = @At("HEAD"), cancellable = true)
    private static void onLilyPadColor(CallbackInfoReturnable<Integer> cir) {
        var colorProperties = ObjectUtils.firstNonNull(BismuthColormaticResolution.COLOR_PROPERTIES, BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES);
        int color = colorProperties.getProperties().getLilyPad();
        if(color != 0) {
            cir.setReturnValue(color);
        }
    }

    @Inject(method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;I)I", at = @At("HEAD"), cancellable = true)
    private void onColorMultiplier(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> cir) {
        if(world != null && pos != null && BiomeColorMappings.isCustomColored(state)) {
            int color = BiomeColorMappings.getBiomeColorMapping(state, world, pos);
            cir.setReturnValue(color);
        }
    }
}
