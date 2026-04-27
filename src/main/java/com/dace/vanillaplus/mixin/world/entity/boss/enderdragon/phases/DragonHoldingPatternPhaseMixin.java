package com.dace.vanillaplus.mixin.world.entity.boss.enderdragon.phases;

import com.dace.vanillaplus.world.entity.EntityModifier;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
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
import org.spongepowered.asm.mixin.injection.Redirect;
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
    private void performStrafeOrCharge(@NonNull EntityModifier.EnderDragonModifier.PhaseInfo phaseInfo, @NonNull Player target) {
        double chargeChance = Mth.clampedLerp(dragon.distanceTo(target) / CHARGE_CHANCE_MAX_DISTANCE, CHARGE_CHANCE_MIN, CHARGE_CHANCE_MAX);
        if (chargeChance > dragon.getRandom().nextDouble()) {
            dragon.getPhaseManager().setPhase(EnderDragonPhase.CHARGING_PLAYER);
            dragon.getPhaseManager().getPhase(EnderDragonPhase.CHARGING_PLAYER).setTarget(target.position());
        } else
            strafePlayer(target);

        double attackCooldownSeconds = phaseInfo.getAttackCooldownSeconds().get(dragon);
        getVPEnderDragon().setAttackCooldown((int) (attackCooldownSeconds * 20.0));
    }

    @Unique
    private void performMeteorAttack(@NonNull EntityModifier.EnderDragonModifier.PhaseInfo.Meteor meteor, @NonNull Player target) {
        if (!getVPEnderDragon().dropMeteor(target.position()))
            return;

        double cooldownSeconds = meteor.getCooldownSeconds().get(dragon);
        getVPEnderDragon().setMeteorAttackCooldown((int) (cooldownSeconds * 20.0));
    }

    @Redirect(method = "findNewTarget", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getNearestPlayer(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;Lnet/minecraft/world/entity/LivingEntity;DDD)Lnet/minecraft/world/entity/player/Player;"))
    private Player cancelDefaultStrafe(ServerLevel instance, TargetingConditions targetConditions, LivingEntity source, double x, double y, double z) {
        return getVPEnderDragon().getDataModifier().isPresent() ? null : instance.getNearestPlayer(targetConditions, source, x, y, z);
    }

    @Definition(id = "dragon", field = "Lnet/minecraft/world/entity/boss/enderdragon/phases/DragonHoldingPatternPhase;dragon:Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;")
    @Definition(id = "getRandom", method = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;getRandom()Lnet/minecraft/util/RandomSource;")
    @Definition(id = "nextInt", method = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
    @Expression("this.dragon.getRandom().nextInt(?) == 0")
    @ModifyExpressionValue(method = "findNewTarget", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private boolean modifyLandingCondition(boolean condition) {
        return getVPEnderDragon().getDataModifier()
                .map(enderDragonModifier ->
                        enderDragonModifier.getPhaseInfo().getLandingChance().get(dragon) > dragon.getRandom().nextDouble())
                .orElse(condition);
    }

    @Inject(method = "doServerTick", at = @At("TAIL"))
    private void performAttack(ServerLevel level, CallbackInfo ci) {
        getVPEnderDragon().getDataModifier().ifPresent(enderDragonModifier -> {
            if (getVPEnderDragon().getAttackCooldown() > 0 && getVPEnderDragon().getMeteorAttackCooldown() > 0)
                return;

            Player target = level.getNearestPlayer(getVPEnderDragon().getDefaultTargetingConditions(), dragon);
            if (target == null)
                return;

            if (getVPEnderDragon().getAttackCooldown() <= 0)
                performStrafeOrCharge(enderDragonModifier.getPhaseInfo(), target);
            if (getVPEnderDragon().getMeteorAttackCooldown() <= 0)
                performMeteorAttack(enderDragonModifier.getPhaseInfo().getMeteor(), target);
        });
    }

    @Redirect(method = "onCrystalDestroyed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/enderdragon/phases/DragonHoldingPatternPhase;strafePlayer(Lnet/minecraft/world/entity/player/Player;)V"))
    private void resetCooldownOnCrystalDestroyed(DragonHoldingPatternPhase instance, Player playerNearestToEgg) {
        getVPEnderDragon().setAttackCooldown(0);
    }
}
