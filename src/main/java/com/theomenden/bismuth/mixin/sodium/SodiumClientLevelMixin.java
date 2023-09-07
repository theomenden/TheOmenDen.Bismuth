package com.theomenden.bismuth.mixin.sodium;

import me.jellysquid.mods.sodium.client.world.BiomeSeedProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientLevel.class)
public abstract class SodiumClientLevelMixin extends Level implements BiomeSeedProvider {
    private SodiumClientLevelMixin() {
        super(null, null, null, null, null, false, false, 0L, 0);
    }

    @Override
    public long getBiomeSeed() {
        return ((SodiumBiomeManagerAccessor)this.getBiomeManager()).getBiomeZoomSeed();
    }
}
