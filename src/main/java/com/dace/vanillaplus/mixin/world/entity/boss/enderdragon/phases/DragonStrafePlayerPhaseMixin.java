package com.dace.vanillaplus.mixin.world.entity.boss.enderdragon.phases;

import com.dace.vanillaplus.extension.world.entity.boss.enderdragon.VPEnderDragon;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonStrafePlayerPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.entity.projectile.DragonFireball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonStrafePlayerPhase.class)
public abstract class DragonStrafePlayerPhaseMixin extends AbstractDragonPhaseInstanceMixin {
    @Unique
    private int fireCount = 0;
    @Unique
    private int maxFireCount = 0;

    @WrapWithCondition(method = "doServerTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/enderdragon/phases/EnderDragonPhaseManager;setPhase(Lnet/minecraft/world/entity/boss/enderdragon/phases/EnderDragonPhase;)V",
            ordinal = 1))
    private boolean checkFireCount(EnderDragonPhaseManager instance, EnderDragonPhase<?> enderDragonPhase) {
        return ++fireCount >= maxFireCount;
    }

    @Inject(method = "doServerTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void applyFireballVelocity(ServerLevel serverLevel, CallbackInfo ci, @Local DragonFireball dragonFireball) {
        dragonFireball.accelerationPower *= VPEnderDragon.cast(dragon).getDataModifier().getPhaseInfo().getFireball().getVelocityMultiplier();
    }

    @ModifyExpressionValue(method = "doServerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getX()D",
            ordinal = 2))
    private double modifyFireballTargetX(double x) {
        return dragon.getRandom().triangle(x, 8);
    }

    @ModifyExpressionValue(method = "doServerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getZ()D",
            ordinal = 2))
    private double modifyFireballTargetZ(double z) {
        return dragon.getRandom().triangle(z, 8);
    }

    @Inject(method = "begin", at = @At("TAIL"))
    private void resetFireCount(CallbackInfo ci) {
        fireCount = 0;
        maxFireCount = (int) VPEnderDragon.cast(dragon).getDataModifier().getPhaseInfo().getFireball().getMaxShots().get(dragon);
    }
}
