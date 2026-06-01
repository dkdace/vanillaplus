package com.dace.vanillaplus.mixin.world.entity.boss.enderdragon.phases;

import com.dace.vanillaplus.world.entity.boss.enderdragon.EnderDragonConfig;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import lombok.NonNull;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonHoldingPatternPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonHoldingPatternPhase.class)
public abstract class DragonHoldingPatternPhaseMixin extends AbstractDragonPhaseInstanceMixin {
    @Unique
    private static final double CHARGE_CHANCE_MIN = 0.3;
    @Unique
    private static final double CHARGE_CHANCE_MAX = 0.7;
    @Unique
    private static final double CHARGE_CHANCE_MAX_DISTANCE = 60;

    @Shadow
    protected abstract void strafePlayer(Player playerNearestToEgg);

    @Unique
    private void performStrafeOrCharge(@NonNull EnderDragonConfig.PhaseInfo phaseInfo, @NonNull Player target) {
        double chargeChance = Mth.clampedLerp(dragon.distanceTo(target) / CHARGE_CHANCE_MAX_DISTANCE, CHARGE_CHANCE_MIN, CHARGE_CHANCE_MAX);
        if (chargeChance > dragon.getRandom().nextDouble()) {
            dragon.getPhaseManager().setPhase(EnderDragonPhase.CHARGING_PLAYER);
            dragon.getPhaseManager().getPhase(EnderDragonPhase.CHARGING_PLAYER).setTarget(target.position());
        } else
            strafePlayer(target);

        getVPEnderDragon().setAttackCooldown((int) phaseInfo.attackCooldown().get(dragon));
    }

    @Unique
    private void performMeteorAttack(@NonNull EnderDragonConfig.PhaseInfo.Meteor meteor, @NonNull Player target) {
        if (getVPEnderDragon().dropMeteor(target.position()))
            getVPEnderDragon().setMeteorAttackCooldown((int) meteor.cooldown().get(dragon));
    }

    @WrapOperation(method = "findNewTarget", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getNearestPlayer(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;Lnet/minecraft/world/entity/LivingEntity;DDD)Lnet/minecraft/world/entity/player/Player;"))
    private Player cancelDefaultStrafe(ServerLevel instance, TargetingConditions targetConditions, LivingEntity source, double x, double y,
                                       double z, Operation<Player> original) {
        return EnderDragonConfig.get().phaseInfo().isPresent() ? null : original.call(instance, targetConditions, source, x, y, z);
    }

    @Definition(id = "dragon", field = "Lnet/minecraft/world/entity/boss/enderdragon/phases/DragonHoldingPatternPhase;dragon:Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;")
    @Definition(id = "getRandom", method = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;getRandom()Lnet/minecraft/util/RandomSource;")
    @Definition(id = "nextInt", method = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
    @Expression("this.dragon.getRandom().nextInt(?) == 0")
    @ModifyExpressionValue(method = "findNewTarget", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private boolean modifyLandingCondition(boolean condition) {
        return EnderDragonConfig.get().phaseInfo()
                .map(phaseInfo -> phaseInfo.landingChance().get(dragon) > dragon.getRandom().nextDouble())
                .orElse(condition);
    }

    @Inject(method = "doServerTick", at = @At("TAIL"))
    private void performAttack(ServerLevel level, CallbackInfo ci) {
        EnderDragonConfig.get().phaseInfo().ifPresent(phaseInfo -> {
            if (getVPEnderDragon().getAttackCooldown() > 0 && getVPEnderDragon().getMeteorAttackCooldown() > 0)
                return;

            Player target = level.getNearestPlayer(getVPEnderDragon().getDefaultTargetingConditions(), dragon);
            if (target == null)
                return;

            if (getVPEnderDragon().getAttackCooldown() <= 0)
                performStrafeOrCharge(phaseInfo, target);
            if (getVPEnderDragon().getMeteorAttackCooldown() <= 0)
                performMeteorAttack(phaseInfo.meteor(), target);
        });
    }

    @WrapWithCondition(method = "onCrystalDestroyed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/enderdragon/phases/DragonHoldingPatternPhase;strafePlayer(Lnet/minecraft/world/entity/player/Player;)V"))
    private boolean resetCooldownOnCrystalDestroyed(DragonHoldingPatternPhase instance, Player playerNearestToEgg) {
        if (EnderDragonConfig.get().phaseInfo().isEmpty())
            return true;

        getVPEnderDragon().setAttackCooldown(0);
        return false;
    }
}
