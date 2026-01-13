package com.dace.vanillaplus.mixin.client.particle;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SingleQuadParticle.class)
public abstract class SingleQuadParticleMixin implements VPMixin<SingleQuadParticle> {
    @Shadow
    protected float quadSize;

    @Shadow
    public abstract Particle scale(float scale);
}
