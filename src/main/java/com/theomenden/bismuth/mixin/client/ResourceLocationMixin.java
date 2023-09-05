package com.theomenden.bismuth.mixin.client;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ResourceLocation.class)
public abstract class ResourceLocationMixin {

    @ModifyArg(
            method="<init>(Ljava/lang/String;Ljava/lang/String;)V",
            index = 0,
            at = @At(
                    value = "INVOKE",
                    target="Lnet/minecraft/resources/ResourceLocation;assertValidPath(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
            )
    )
    private static String skipValidationForBismuth(String namespace, String path) {
        if(namespace.equals("minecraft")
        && path.startsWith("optifine/")) {
            path = "safe_id_for_allowing_invalid_chars";
        }
        return path;
    }
}
