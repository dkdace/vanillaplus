package com.dace.vanillaplus.mixin.client.particle;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Particle.class)
public abstract class ParticleMixin<T extends Particle> implements VPMixin<T> {
    @Shadow
    @Final
    protected ClientLevel level;
}
