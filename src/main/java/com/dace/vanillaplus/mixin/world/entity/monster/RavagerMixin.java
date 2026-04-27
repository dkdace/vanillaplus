package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.mixin.world.entity.raid.RaiderMixin;
import com.dace.vanillaplus.world.entity.EntityModifier;
import com.dace.vanillaplus.world.entity.raid.RaiderEffect;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Ravager.class)
public abstract class RavagerMixin extends RaiderMixin<Ravager, EntityModifier.RavagerModifier> {
    @Unique
    private static final int ROAR_DISTANCE = 5;

    @Unique
    private int roarCooldown = 0;
    @Shadow
    private int roarTick;

    @ModifyReturnValue(method = "lambda$registerGoals$0", at = @At("RETURN"))
    private static boolean removeVillagerAttackGoalCondition(boolean original) {
        return true;
    }

    @Shadow
    public abstract boolean hasLineOfSight(Entity entity);

    @Overwrite
    public void applyRaidBuffs(ServerLevel level, int wave, boolean isCaptain) {
        getRaiderEffect(RaiderEffect.RavagerEffect.class).ifPresent(ravagerEffect ->
                ravagerEffect.getMobEffectInfos().forEach(mobEffectEffect -> mobEffectEffect.applyMobEffect(getThis())));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
        output.putInt("RoarCooldown", roarCooldown);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        roarCooldown = input.getIntOr("RoarCooldown", 0);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/Ravager;getTarget()Lnet/minecraft/world/entity/LivingEntity;"))
    private void decreaseRoarCooldown(CallbackInfo ci) {
        if (roarCooldown > 0)
            roarCooldown--;
    }

    @Inject(method = "aiStep", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/monster/Ravager;roarTick:I", ordinal = 1,
            opcode = Opcodes.PUTFIELD))
    private void setRoarCooldown(CallbackInfo ci) {
        getDataModifier().ifPresent(ravagerModifier -> roarCooldown = ravagerModifier.getRoarCooldown());
    }

    @Inject(method = "aiStep", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/monster/Ravager;stunnedTick:I", ordinal = 0,
            opcode = Opcodes.GETFIELD))
    private void performRoar(CallbackInfo ci) {
        getDataModifier().ifPresent(ravagerModifier -> {
            LivingEntity target = getTarget();
            if (target == null || roarCooldown > 0 || !closerThan(target, ROAR_DISTANCE) || !hasLineOfSight(target))
                return;

            playSound(SoundEvents.RAVAGER_ROAR, 1, 1);
            roarTick = 20;
            roarCooldown = ravagerModifier.getRoarCooldown();
        });
    }
}
