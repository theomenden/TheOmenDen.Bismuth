package com.theomenden.bismuth.models;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.compress.utils.Lists;

import java.util.Collection;

public final class ApplicableBlockStates {
    public Block block = Blocks.AIR;
    public Collection<BlockState> states = Lists.newArrayList();
    public ResourceLocation specialKey = null;
    public Collection<ResourceLocation> specialLocations = Lists.newArrayList();
}
