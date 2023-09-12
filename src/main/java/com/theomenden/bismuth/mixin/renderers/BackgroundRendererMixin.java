package com.theomenden.bismuth.mixin.renderers;

import com.theomenden.bismuth.client.Bismuth;
import com.theomenden.bismuth.colors.interfaces.BismuthResolver;
import com.theomenden.bismuth.colors.mapping.BiomeColorMapping;
import com.theomenden.bismuth.colors.mapping.BiomeColorMappings;
import com.theomenden.bismuth.colors.resources.BiomeColorMappingResource;
import com.theomenden.bismuth.models.records.Coordinates;
import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.harmony.pack200.NewAttributeBands;
import org.apache.commons.lang3.ObjectUtils;
import org.joml.Vector3f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class BackgroundRendererMixin {

    @Shadow private static float fogRed;
    @Shadow private static float fogGreen;
    @Shadow private static float fogBlue;
    @Unique private static float redStore;
    @Unique private static float greenStore;
    @Unique private static float blueStore;

    @ModifyVariable(
            method = "setupColor",
            at = @At(
                    value="LOAD",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private static FogType detectBismuthFluidBlending(FogType fogType) {
        if(fogType == FogType.LAVA
            && (BiomeColorMappings.isFluidFogCustomColored(Fluids.LAVA)
            || BismuthColormaticResolution.hasCustomUnderLavaColors())) {
                return FogType.WATER;
        }

        return fogType;
    }

    @Redirect(
            method = "setupColor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Biome;getWaterFogColor()I"
            )
    )
    private static int proxyFogColorsForFluids(Biome biome, Camera camera, float tickDelta, ClientLevel world, int i, float f) {
        Fluid fluid;
        BiomeColorMappingResource colormapResource;
        var submersionType = camera.getFluidInCamera();
        if(submersionType == FogType.LAVA) {
            fluid = Fluids.LAVA;
            colormapResource = ObjectUtils.firstNonNull(BismuthColormaticResolution.COLORMATIC_UNDERLAVA_COLORS, BismuthColormaticResolution.UNDERLAVA_COLORS);
        } else {
            fluid = Fluids.WATER;
            colormapResource = ObjectUtils.firstNonNull(BismuthColormaticResolution.COLORMATIC_UNDERWATER_COLORS, BismuthColormaticResolution.UNDERWATER_COLORS);
        }
        int color = 0;
        if(BiomeColorMappings.isFluidFogCustomColored(fluid)) {
            BiomeColorMapping colormap = BiomeColorMappings.getFluidFog(world.registryAccess(), fluid, biome);
            if(colormap != null) {
                BlockPos pos = camera.getBlockPosition();
                var coordinates = new Coordinates(pos.getX(), pos.getY(), pos.getZ());
                color = colormap.getColorAtCoordinatesForBiome(world.registryAccess(), biome,coordinates );
            }
        }
        if(color == 0) {
            if(colormapResource.hasCustomColorMapping()) {
                BlockPos pos = camera.getBlockPosition();
                var coordinates = new Coordinates(pos.getX(), pos.getY(), pos.getZ());
                color = colormapResource
                        .getColorMapping()
                        .getColorAtCoordinatesForBiome(world.registryAccess(), biome,coordinates);
            } else {
                if(submersionType == FogType.LAVA) {
                    color = 0x991900;
                } else {
                    color = biome.getWaterFogColor();
                }
            }
        }
        return color;
    }

    @Dynamic("Cubic Sampler method in #setupColor")
    @Redirect(
            method="method_24873",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Biome;getFogColor()I"
            )
    )
    private static int customFogColorProxy(Biome self, ClientLevel level, BiomeManager manager, float angleDelta, int x, int y, int z) {
        if(Bismuth.configuration.shouldClearSky
        && level.dimensionType().hasSkyLight()) {
            return self.getFogColor();
        }

        var dimensionId = Bismuth.getDimensionId(level);
        BismuthResolver resolver = BiomeColorMappings.getTotalSkyFog(dimensionId);
        var coordinates = new Coordinates(
                QuartPos.toBlock(x),
                QuartPos.toBlock(y),
                QuartPos.toBlock(z)
        );
        return resolver.getColorAtCoordinatesForBiome(level.registryAccess(), self, coordinates);
    }

    @Inject(
            method = "setupColor",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/FogRenderer;fogBlue:F",
                    opcode = Opcodes.PUTSTATIC,
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/Camera;getLookVector()Lorg/joml/Vector3f;"
                    )
            )
    )
    private static void blendFogAndSkyColors(Camera activeRenderInfo, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci) {
        if(Bismuth.configuration.shouldClearSky
        && level.dimensionType().hasSkyLight()) {
            Vec3 color = level.getSkyColor(activeRenderInfo.getPosition(), partialTicks);
            BackgroundRendererMixin.fogRed  = (float)color.x() ;
            BackgroundRendererMixin.fogBlue  = (float)color.y();
            BackgroundRendererMixin.fogGreen  = (float)color.z();
        }
    }

    @Inject(
            method = "setupColor",
            at = @At(
                    value="INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"
            )
    )
    private static void saveStormColors(CallbackInfo ci) {
        if(Bismuth.configuration.shouldClearSky) {
            redStore = BackgroundRendererMixin.fogRed;
            greenStore = BackgroundRendererMixin.fogGreen;
            blueStore = BackgroundRendererMixin.fogBlue;
        }
    }

    @Inject(
            method = "setupColor",
            at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/renderer/FogRenderer;biomeChangedTime:J"),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"
                    )
            )
    )
    private static void resetStormColors(CallbackInfo ci) {
        if(Bismuth.configuration.shouldClearSky) {
            BackgroundRendererMixin.fogRed = redStore;
            BackgroundRendererMixin.fogGreen = greenStore;
            BackgroundRendererMixin.fogBlue = blueStore;
        }
    }

    @ModifyVariable(
            method = "setupColor",
            at = @At(value = "STORE", ordinal = 2),
            index = 7
    )
    private static float modifyVoidColor(float scale) {
        if(Bismuth.configuration.shouldClearVoid) {
            scale = 1.0f;
        }
        return scale;
    }
}
