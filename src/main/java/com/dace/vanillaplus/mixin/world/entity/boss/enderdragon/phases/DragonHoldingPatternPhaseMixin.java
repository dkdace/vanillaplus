package com.dace.vanillaplus.mixin.world.entity.boss.enderdragon.phases;

import com.dace.vanillaplus.extension.world.entity.boss.enderdragon.VPEnderDragon;
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
    protected abstract void strafePlayer(Player player);

    @Unique
    private void performStrafeOrCharge(@NonNull Player target) {
        double chargeChance = Mth.clampedLerp(CHARGE_CHANCE_MIN, CHARGE_CHANCE_MAX, dragon.distanceTo(target) / CHARGE_CHANCE_MAX_DISTANCE);
        if (chargeChance > dragon.getRandom().nextDouble()) {
            dragon.getPhaseManager().setPhase(EnderDragonPhase.CHARGING_PLAYER);
            dragon.getPhaseManager().getPhase(EnderDragonPhase.CHARGING_PLAYER).setTarget(target.position());
        } else
            strafePlayer(target);

        VPEnderDragon vpEnderDragon = VPEnderDragon.cast(dragon);

        double attackCooldownSeconds = vpEnderDragon.getDataModifier().getPhaseInfo().getAttackCooldownSeconds().get(dragon);
        vpEnderDragon.setAttackCooldown((int) (attackCooldownSeconds * 20.0));
    }

    @Unique
    private void performMeteorAttack(@NonNull Player target) {
        VPEnderDragon vpEnderDragon = VPEnderDragon.cast(dragon);
        if (!vpEnderDragon.dropMeteor(target.position()))
            return;

        double cooldownSeconds = vpEnderDragon.getDataModifier().getPhaseInfo().getMeteor().getCooldownSeconds().get(dragon);
        vpEnderDragon.setMeteorAttackCooldown((int) (cooldownSeconds * 20.0));
    }

    @Redirect(method = "findNewTarget", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getNearestPlayer(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;Lnet/minecraft/world/entity/LivingEntity;DDD)Lnet/minecraft/world/entity/player/Player;"))
    private Player cancelDefaultStrafe(ServerLevel serverLevel, TargetingConditions targetingConditions, LivingEntity entity, double x, double y,
                                       double z) {
        return null;
    }

    @Definition(id = "dragon", field = "Lnet/minecraft/world/entity/boss/enderdragon/phases/DragonHoldingPatternPhase;dragon:Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;")
    @Definition(id = "getRandom", method = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;getRandom()Lnet/minecraft/util/RandomSource;")
    @Definition(id = "nextInt", method = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
    @Expression("this.dragon.getRandom().nextInt(?) == 0")
    @ModifyExpressionValue(method = "findNewTarget", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private boolean modifyLandingCondition(boolean original) {
        return VPEnderDragon.cast(dragon).getDataModifier().getPhaseInfo().getLandingChance().get(dragon) > dragon.getRandom().nextDouble();
    }

    @Inject(method = "doServerTick", at = @At("TAIL"))
    private void performAttack(ServerLevel serverLevel, CallbackInfo ci) {
        VPEnderDragon vpEnderDragon = VPEnderDragon.cast(dragon);
        if (vpEnderDragon.getAttackCooldown() > 0 && vpEnderDragon.getMeteorAttackCooldown() > 0)
            return;

        Player target = serverLevel.getNearestPlayer(vpEnderDragon.getDefaultTargetingConditions(), dragon);
        if (target == null)
            return;

        if (vpEnderDragon.getAttackCooldown() <= 0)
            performStrafeOrCharge(target);
        if (vpEnderDragon.getMeteorAttackCooldown() <= 0)
            performMeteorAttack(target);
    }

    @Redirect(method = "onCrystalDestroyed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/enderdragon/phases/DragonHoldingPatternPhase;strafePlayer(Lnet/minecraft/world/entity/player/Player;)V"))
    private void resetCooldownOnCrystalDestroyed(DragonHoldingPatternPhase dragonHoldingPatternPhase, Player player) {
        VPEnderDragon.cast(dragon).setAttackCooldown(0);
    }
}
