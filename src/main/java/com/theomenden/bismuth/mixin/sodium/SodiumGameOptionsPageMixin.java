package com.theomenden.bismuth.mixin.sodium;

import com.theomenden.bismuth.client.Bismuth;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptionPages;
import me.jellysquid.mods.sodium.client.gui.options.*;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = SodiumGameOptionPages.class, remap = false)
public abstract class SodiumGameOptionsPageMixin {
        @Shadow
        @Final
        private static MinecraftOptionsStorage vanillaOpts;

        @Inject(method = "quality",
                at = @At(
                        value = "INVOKE",
                        target = "Lme/jellysquid/mods/sodium/client/gui/options/OptionImpl;createBuilder(Ljava/lang/Class;Lme/jellysquid/mods/sodium/client/gui/options/storage/OptionStorage;)Lme/jellysquid/mods/sodium/client/gui/options/OptionImpl$Builder;",
                        ordinal = 2,
                        shift = At.Shift.AFTER
                ),
                locals = LocalCapture.CAPTURE_FAILSOFT,
                remap = false)
        private static void quality(CallbackInfoReturnable<OptionPage> cir, List<OptionGroup> groups) {
            groups.add(OptionGroup.createBuilder()
                                  .add(OptionImpl
                                          .createBuilder(int.class, vanillaOpts)
                                          .setName(Component.translatable("text.autoconfig.bismuth.option.blendingRadius"))
                                          .setTooltip(Component.translatable("text.autoconfig.bismuth.option.blendingRadius.@Tooltip"))
                                          .setControl(option -> new SliderControl(option, 0, 14, 1, ControlValueFormatter.biomeBlend()))
                                          .setBinding((opts, value) -> Bismuth.bismuthBlendingRadius.set(value), opts -> Bismuth.bismuthBlendingRadius.get())
                                          .setImpact(OptionImpact.LOW)
                                          .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                          .build())
                                  .build());
        }
    }
