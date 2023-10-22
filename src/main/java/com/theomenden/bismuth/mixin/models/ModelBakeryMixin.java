package com.theomenden.bismuth.mixin.models;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.theomenden.bismuth.colors.decorators.ModelIdContext;
import com.theomenden.bismuth.colors.mapping.BiomeColorMappings;
import com.theomenden.bismuth.mixin.coloring.blocks.BlockColorsAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiFunction;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Unique
    private static final BlockStateArgument BLOCK_STATE_PARSER = BlockStateArgument.block(
            CommandBuildContext.configurable(
                    RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY),
                    FeatureFlags.DEFAULT_FLAGS
            )
    );

    @Inject(
            method="method_45877",
            at= @At(value ="INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery$ModelBakerImpl;bake(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/ModelState;)Lnet/minecraft/client/resources/model/BakedModel;")
    )
    private void setModelIdContext(BiFunction<ResourceLocation, Material, TextureAtlasSprite> biFunction, ResourceLocation resourceLocation, CallbackInfo ci) {
        boolean finished = false;
        ModelIdContext.shouldTintCurrentModel = false;
        if (resourceLocation instanceof ModelResourceLocation modelId) {
            BlockState blockState = null;
            if (modelId
                    .getVariant()
                    .equals("inventory")) {
                var blockId = new ResourceLocation(modelId.getNamespace(), modelId.getPath());
                blockState = BuiltInRegistries.BLOCK
                        .get(blockId)
                        .defaultBlockState();
            } else {
                var blockStateDesc = modelId.getNamespace() + ":" + modelId.getPath() + "[" + modelId.getVariant() + "]";
                try {
                    blockState = BLOCK_STATE_PARSER
                            .parse(new StringReader(blockStateDesc))
                            .getState();
                } catch (CommandSyntaxException e) {
                    finished = true;
                }
            }
            if (!finished) {
                if (BiomeColorMappings.isCustomColored(blockState)) {
                    var colorProviders = ((BlockColorsAccessor) Minecraft
                            .getInstance()
                            .getBlockColors()).getBlockColors();
                    if (!colorProviders.contains(BuiltInRegistries.BLOCK.getId(blockState.getBlock()))) {
                        ModelIdContext.shouldTintCurrentModel = true;
                    }
                }
            }
        }
    }

    @Inject(
            method = "loadModel",
            at= @At(value ="HEAD")
    )
    private void setModelIdContext(ResourceLocation blockstateLocation, CallbackInfo ci) {
        ModelIdContext.shouldTintCurrentModel = false;
        if (blockstateLocation instanceof ModelResourceLocation modelId) {
            BlockState blockState;
            if (modelId.getVariant().equals("inventory")) {
                var blockId = new ResourceLocation(modelId.getNamespace(), modelId.getPath());
                blockState = BuiltInRegistries.BLOCK.get(blockId).defaultBlockState();
            } else {
                var blockStateDesc = modelId.getNamespace() + ":" + modelId.getPath() + "[" + modelId.getVariant() + "]";
                try {
                    blockState = BLOCK_STATE_PARSER
                            .parse(new StringReader(blockStateDesc))
                            .getState();
                } catch (CommandSyntaxException e) {
                    return;
                }
            }

            if (BiomeColorMappings.isCustomColored(blockState)) {
                var colorProviders = ((BlockColorsAccessor) Minecraft
                            .getInstance()
                            .getBlockColors()).getBlockColors();
                if (!colorProviders.contains(BuiltInRegistries.BLOCK.getId(blockState.getBlock()))) {
                    ModelIdContext.shouldTintCurrentModel = true;
               }
           }
        }
    }
}
