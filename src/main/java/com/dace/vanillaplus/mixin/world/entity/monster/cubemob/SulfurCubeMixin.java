package com.dace.vanillaplus.mixin.world.entity.monster.cubemob;

import com.dace.vanillaplus.extension.world.level.block.entity.VPPotentSulfurBlockEntity;
import com.dace.vanillaplus.world.entity.monster.cubemob.SulfurCubeConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SulfurCube.class)
public abstract class SulfurCubeMixin extends AbstractCubeMobMixin<SulfurCube> {
    @Unique
    private static final double BURNING_NOXIOUS_GAS_RADIUS = 2;

    @Shadow
    public abstract boolean hasBodyItem();

    @Unique
    private boolean isBurning() {
        return SulfurCubeConfig.get().canBurn() && !isBaby() && isAlive() && isOnFire() && !isInWall() && !isInWater();
    }

    @Override
    protected boolean shouldPlayLavaHurtSound() {
        return !hasBodyItem();
    }

    @Override
    protected boolean shouldDropLoot(ServerLevel level) {
        return level.getGameRules().get(GameRules.MOB_DROPS);
    }

    @ModifyExpressionValue(method = "knockback", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/cubemob/SulfurCube;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    private double modifyKnockbackResistance(double knockbackResistance, @Local(argsOnly = true) DamageSource damageSource) {
        return getFinalKnockbackResistance(knockbackResistance, damageSource);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/cubemob/AbstractCubeMob;tick()V"))
    private void playBurningNoxiousGasParticles(CallbackInfo ci) {
        if (level() instanceof ClientLevel clientLevel && isBurning())
            VPPotentSulfurBlockEntity.playBurningNoxiousGasParticles(clientLevel, BlockPos.containing(position().subtract(0, 0.1, 0)),
                    BURNING_NOXIOUS_GAS_RADIUS);
    }

    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void applyBurningNoxiousGasEffects(ServerLevel level, CallbackInfo ci) {
        if (isBurning() && tickCount % 10 == 0)
            VPPotentSulfurBlockEntity.createBurningNoxiousGas(level, BlockPos.containing(position().subtract(0, 0.1, 0)),
                    SulfurCubeConfig.get().burningNoxiousGasEffects(), BURNING_NOXIOUS_GAS_RADIUS);
    }
}
