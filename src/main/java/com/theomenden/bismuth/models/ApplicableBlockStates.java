package com.theomenden.bismuth.models;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class ApplicableBlockStates {
    public Block block = Blocks.AIR;
    public ObjectArrayList<BlockState> states = new ObjectArrayList<>();
    public ResourceLocation specialKey = null;
    public ObjectArrayList<ResourceLocation> specialLocations = new ObjectArrayList<>();
}
