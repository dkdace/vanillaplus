package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.RaiderEffect;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.raid.RaiderMixin;
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

import java.util.Objects;

@Mixin(Ravager.class)
public abstract class RavagerMixin extends RaiderMixin<Ravager, EntityModifier.RavagerModifier> {
    @Unique
    private static final int ROAR_DISTANCE = 5;

    @Unique
    private int roarCooldown = 0;
    @Shadow
    private int roarTick;

    @ModifyReturnValue(method = "lambda$registerGoals$3", at = @At("RETURN"))
    private static boolean modifyVillagerAttackGoalCondition(boolean original) {
        return true;
    }

    @Shadow
    public abstract boolean hasLineOfSight(Entity entity);

    @Overwrite
    public void applyRaidBuffs(ServerLevel serverLevel, int wave, boolean ignored) {
        RaiderEffect.RavagerEffect ravagerEffect = RaiderEffect.fromEntityType(getType());
        ravagerEffect.getMobEffectInfos().forEach(mobEffectEffect -> mobEffectEffect.applyMobEffect(getThis()));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditionalSaveData(ValueOutput valueOutput, CallbackInfo ci) {
        valueOutput.putInt("RoarCooldown", roarCooldown);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditionalSaveData(ValueInput valueInput, CallbackInfo ci) {
        roarCooldown = valueInput.getIntOr("RoarCooldown", 0);
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
        roarCooldown = Objects.requireNonNull(dataModifier).getRoarCooldown();
    }

    @Inject(method = "aiStep", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/monster/Ravager;stunnedTick:I", ordinal = 0,
            opcode = Opcodes.GETFIELD))
    private void performRoar(CallbackInfo ci) {
        LivingEntity target = getTarget();
        if (target == null || roarCooldown > 0 || !closerThan(target, ROAR_DISTANCE) || !hasLineOfSight(target))
            return;

        playSound(SoundEvents.RAVAGER_ROAR, 1, 1);
        roarTick = 20;
        roarCooldown = Objects.requireNonNull(dataModifier).getRoarCooldown();
    }
}
