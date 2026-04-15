package com.dace.vanillaplus.mixin.client.particle;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.particle.AttackSweepParticle;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AttackSweepParticle.class)
public abstract class AttackSweepParticleMixin extends SingleQuadParticleMixin {
    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/AttackSweepParticle;quadSize:F",
            opcode = Opcodes.PUTFIELD))
    private void modifyQuadSize(AttackSweepParticle instance, float value, @Local(ordinal = 3, argsOnly = true) double size) {
        quadSize = 1;
        scale((float) size);
    }
}
