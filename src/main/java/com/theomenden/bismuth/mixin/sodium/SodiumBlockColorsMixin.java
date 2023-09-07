package com.theomenden.bismuth.mixin.sodium;

import com.theomenden.bismuth.colors.mapping.BiomeColorMappings;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorSampler;
import me.jellysquid.mods.sodium.client.world.biome.BlockColorsExtended;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;

@Mixin(value = BlockColors.class, priority = 2000)
@Implements(
        @Interface(
                iface = BlockColorsExtended.class,
                prefix ="i$",
                remap = Interface.Remap.NONE)
)
public abstract class SodiumBlockColorsMixin implements BlockColorsExtended {

    @Unique
    private static final ColorSampler<BlockState> BISMUTH_PROVIDER =
            (state, blockAndTintGetter, pos, tintIndex) ->
                    BiomeColorMappings.getBiomeColorMapping(state, blockAndTintGetter, pos);
    @Intrinsic(displace = true)
    public ColorSampler<BlockState> i$getColorProvider(BlockState state) {
        if(BiomeColorMappings.isCustomColored(state)) {
            return BISMUTH_PROVIDER;
        }
        return this.getColorProvider(state);
    }

}
