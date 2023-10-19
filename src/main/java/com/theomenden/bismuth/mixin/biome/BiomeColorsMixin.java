package com.theomenden.bismuth.mixin.biome;

import com.theomenden.bismuth.colors.mapping.BiomeColorMapping;
import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeColors.class)
public abstract class BiomeColorsMixin {
    @Inject(method = "getAverageWaterColor", at =@At("HEAD"), cancellable = true)
    private static void onColoringWater(BlockAndTintGetter level, BlockPos blockPos, CallbackInfoReturnable<Integer> cir) {
        if(BismuthColormaticResolution.hasCustomWaterColors()) {
            var colorMap = ObjectUtils.firstNonNull(
                    BismuthColormaticResolution.WATER_COLORS.getColorMapping(),
                    BismuthColormaticResolution.COLORMATIC_WATER_COLORS.getColorMapping()
            );

            cir.setReturnValue(BiomeColorMapping.getBiomeCurrentColorOrDefault(level, blockPos, colorMap));
        }
    }
}
