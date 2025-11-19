package com.dace.vanillaplus.mixin.world.entity.boss.enderdragon.phases;

import net.minecraft.world.entity.boss.enderdragon.phases.DragonTakeoffPhase;
import net.minecraft.world.level.block.LevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonTakeoffPhase.class)
public abstract class DragonTakeoffPhaseMixin extends AbstractDragonPhaseInstanceMixin {
    @Inject(method = "begin", at = @At("TAIL"))
    private void playTakeoffEffects(CallbackInfo ci) {
        for (int i = 0; i < 3; i++)
            dragon.level().levelEvent(LevelEvent.PARTICLES_DRAGON_FIREBALL_SPLASH, dragon.blockPosition(), dragon.isSilent() ? -1 : 1);
    }
}
