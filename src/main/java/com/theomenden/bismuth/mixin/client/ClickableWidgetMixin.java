package com.theomenden.bismuth.mixin.client;

import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractButton.class)
public abstract class ClickableWidgetMixin extends AbstractWidget{

    public ClickableWidgetMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @ModifyConstant(method="renderWidget", constant = @Constant(intValue = 16777215))
    private int hoverColorProxy(int original) {
        var hoverColors = ObjectUtils.firstNonNull(
          BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
          BismuthColormaticResolution.COLOR_PROPERTIES
        );

        int hoverColor = hoverColors.getProperties().getHoveredButtonText();
        return hoverColor != 0
                && this.isActive()
                ? hoverColor
                : original;
    }

    @ModifyConstant(method="renderWidget", constant = @Constant(intValue = 10526880))
    private int disabledColorProxy(int original) {
        var hoverColors = ObjectUtils.firstNonNull(
                BismuthColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                BismuthColormaticResolution.COLOR_PROPERTIES
        );

        int disabledColor = hoverColors.getProperties().getDisabledButtonText();
        return disabledColor != 0 ? disabledColor : original;
    }
}
