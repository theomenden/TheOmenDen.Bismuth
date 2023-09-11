package com.theomenden.bismuth.colors.particles;

import com.theomenden.bismuth.utils.BismuthColormaticResolution;
import com.theomenden.bismuth.utils.ColorConverter;
import com.theomenden.bismuth.utils.MathUtils;
import net.minecraft.core.particles.DustParticleOptions;
import org.apache.commons.lang3.ObjectUtils;
import org.joml.Vector3f;

public class CustomRedDustParticle extends DustParticleOptions {

    public CustomRedDustParticle(Vector3f vector3f, float alpha) {
        super(vector3f, alpha);
    }

    @Override
    public Vector3f getColor() {
        if(BismuthColormaticResolution.hasCustomRedstoneColors()) {
            return ColorConverter.createColorVector(getFullPoweredColor());
        }
        return super.getColor();
    }

    @Override
    public float getScale() {
        if(BismuthColormaticResolution.hasCustomRedstoneColors()) {
            return ((getFullPoweredColor() >> 24) & 0xff) * MathUtils.INV_255;
        }
        return super.getScale();
    }

    private int getFullPoweredColor() {
        var redstoneColors = ObjectUtils.firstNonNull(
                BismuthColormaticResolution.COLORMATIC_REDSTONE_COLORS,
                BismuthColormaticResolution.REDSTONE_COLORS
        );
        return redstoneColors.getColorAtIndex(15);
    }
}
