package com.theomenden.bismuth.mixin.sodium;

import com.theomenden.bismuth.colors.mapping.BiomeColorMappings;
import me.jellysquid.mods.sodium.client.world.biome.ItemColorsExtended;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;

@Mixin(value = ItemColors.class, priority = 2000)
@Implements(@Interface(iface = ItemColorsExtended.class, prefix = "i$", remap = Interface.Remap.NONE))
public abstract class SodiumItemColorsMixin implements ItemColorsExtended {
    @Intrinsic(displace = true)
    public ItemColor i$getColorProvider(ItemStack stack) {
        if(stack.getItem() instanceof  BlockItem blockItem
         && BiomeColorMappings.isItemCustomColored(blockItem.getBlock().defaultBlockState())) {
            return BISMUTH_PROVIDER;
        }
        return this.getColorProvider(stack);
    }

    @Unique
    private static final ItemColor BISMUTH_PROVIDER =
            (stack, tintIndex) -> BiomeColorMappings.getBiomeColorMapping(
                    ((BlockItem)stack.getItem()).getBlock().defaultBlockState(),
                    null,
                    null);
}
