package com.theomenden.bismuth.colors.particles;

import net.minecraft.core.particles.DustParticleOptions;
import org.joml.Vector3f;

public class CustomRedDustParticle extends DustParticleOptions {

    public CustomRedDustParticle(Vector3f vector3f, float alpha) {
        super(vector3f, alpha);
    }

    @Override
    public Vector3f getColor() {
        return super.getColor();
    }

    @Override
    public float getScale() {
        return super.getScale();
    }

    private int getFullPoweredColor() {
        return 15;
    }
}
