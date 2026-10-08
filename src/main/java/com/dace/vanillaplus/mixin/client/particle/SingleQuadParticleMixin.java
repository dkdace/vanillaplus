package com.dace.vanillaplus.mixin.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SingleQuadParticle.class)
public abstract class SingleQuadParticleMixin<T extends SingleQuadParticle> extends ParticleMixin<T> {
    @Shadow
    protected float quadSize;

    @Shadow
    public abstract Particle scale(float scale);
}
