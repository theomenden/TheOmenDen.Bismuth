package com.theomenden.bismuth.mixin.coloring.blocks;

import com.theomenden.bismuth.colors.resources.LinearColorMappingResource;
import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import com.theomenden.bismuth.utils.ColorConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RedStoneWireBlock.class)
public abstract class RedstoneWireMixin extends Block {

    public RedstoneWireMixin(Properties properties) {
        super(properties);
    }


    @Inject(
            method = "animateTick",
            at =@At(
                    value = "FIELD",
                    target = "Lnet/minecraft/core/Direction$Plane;HORIZONTAL:Lnet/minecraft/core/Direction$Plane;",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void onAnimateTick(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci, int i) {
        if(BismuthColormaticResolution.hasCustomRedstoneColors()) {
            final LinearColorMappingResource redstoneColor = ObjectUtils.firstNonNull(
              BismuthColormaticResolution.REDSTONE_COLORS,
              BismuthColormaticResolution.COLORMATIC_REDSTONE_COLORS
            );

            double x = pos.getX() + 0.5 + (random.nextFloat() - 0.5) * 0.2;
            double y = ((float)pos.getY() + 0.0625F);
            double z = pos.getZ() + 0.5 + (random.nextFloat() - 0.5) * 0.2;

            int color = redstoneColor.getColorAtIndex(i);

            var colorValues = ColorConverter.createColorVector(color);

            level.addParticle(new DustParticleOptions(colorValues, 1.0f), x, y, z, 0.0, 0.0, 0.0);
            ci.cancel();
        }
    }
}
