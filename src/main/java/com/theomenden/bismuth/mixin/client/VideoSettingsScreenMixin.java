package com.theomenden.bismuth.mixin.client;

import com.theomenden.bismuth.client.Bismuth;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(VideoSettingsScreen.class)
public abstract class VideoSettingsScreenMixin extends OptionsSubScreen {

    public VideoSettingsScreenMixin(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @ModifyArg(
            method = "init",
            at = @At(
                    value="INVOKE",
                    target="Lnet/minecraft/client/gui/components/OptionsList;addBig(Lnet/minecraft/client/OptionInstance;)I",
                    ordinal = 1
            ),
            index = 0
    )
    private OptionInstance<?> modifyAddForBig(OptionInstance<?> arg) {
        var result = arg;

        if(arg == this.options.biomeBlendRadius()) {
            result = Bismuth.bismuthBlendingRadius;
        }

        return result;
    }
}
