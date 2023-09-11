package com.theomenden.bismuth.mixin.sodium;

import com.theomenden.bismuth.models.records.Coordinates;
import com.theomenden.bismuth.utils.ColorCachingUtils;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import me.jellysquid.mods.sodium.client.world.biome.BlockColorCache;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.BiomeManager;
import org.apache.commons.lang3.Range;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockColorCache.class)
public abstract class BlockColorCacheMixin {
    @Unique private Coordinates bismuth$BaseCoordinates;

    @Unique private Reference2ReferenceOpenHashMap<ColorResolver, int[]> bismuth$colors;

    @Final
    @Shadow private WorldSlice slice;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    public void onTailConstructor(WorldSlice slice, int radius, CallbackInfo ci) {
        SectionPos origin = slice.getOrigin();
        this.bismuth$BaseCoordinates = new Coordinates(
                origin.minBlockX(),
                origin.minBlockY(),
                origin.minBlockZ()
        );
        this.bismuth$colors = new Reference2ReferenceOpenHashMap<>();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public int getColor(ColorResolver resolver, int posX, int posY, int posZ) {
        int[] colors = this.bismuth$colors.computeIfAbsent(resolver, k -> new int[4096]);

        int blockX = Range.between(0,15).fit(posX - this.bismuth$BaseCoordinates.x());
        int blockY = Range.between(0,15).fit(posY - this.bismuth$BaseCoordinates.y());
        int blockZ = Range.between(0,15).fit(posZ - this.bismuth$BaseCoordinates.z());

        int index = ColorCachingUtils.getArrayIndex(16, blockX, blockY, blockZ);

        int color = colors[index];

        if(color == 0) {
            BiomeManager biomeManager = slice.getBiomeAccess();

            color = colors[index];
        }

        return color;
    }
}
