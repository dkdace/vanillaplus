package com.dace.vanillaplus.mixin.client.resources.sounds;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.client.resources.sounds.GuardianAttackSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuardianAttackSoundInstance.class)
public abstract class GuardianAttackSoundInstanceMixin implements VPMixin<GuardianAttackSoundInstance> {
    @Redirect(method = "<init>", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/resources/sounds/SoundInstance$Attenuation;NONE:Lnet/minecraft/client/resources/sounds/SoundInstance$Attenuation;",
            opcode = Opcodes.GETSTATIC))
    private SoundInstance.Attenuation modifyAttenuation() {
        return SoundInstance.Attenuation.LINEAR;
    }
}
