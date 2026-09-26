package com.dace.vanillaplus.mixin.world.entity.monster.cubemob;

import com.dace.vanillaplus.data.VPTags;
import com.dace.vanillaplus.data.registryobject.VPParticleTypes;
import com.dace.vanillaplus.extension.world.level.block.entity.VPPotentSulfurBlockEntity;
import com.dace.vanillaplus.world.entity.monster.cubemob.SulfurCubeConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SulfurCube.class)
public abstract class SulfurCubeMixin extends AbstractCubeMobMixin<SulfurCube> {
    @Unique
    private static final double BURNING_NOXIOUS_GAS_RADIUS = 1.5;

    @Shadow
    public abstract boolean hasBodyItem();

    @Unique
    private boolean isBurning() {
        return SulfurCubeConfig.get().canBurn() && isAlive() && !isBaby() && isOnFire() && !isInWater();
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
        if (!isBurning())
            return;

        BlockPos blockPos = VPPotentSulfurBlockEntity.findBurningNoxiousGasSourceBlock(level(), blockPosition().below());
        if (blockPos == null)
            return;

        Vec3 vec = new Vec3(random.triangle(0, BURNING_NOXIOUS_GAS_RADIUS), 0, random.triangle(0, BURNING_NOXIOUS_GAS_RADIUS));
        Vec3 pos = Vec3.atCenterOf(blockPos).add(vec).subtract(0, 0.25, 0);

        if (VPPotentSulfurBlockEntity.isBurningNoxiousGasPassable(level(), blockPos, pos))
            level().addParticle(VPParticleTypes.NOXIOUS_GAS_BURNING.get(), pos.x(), pos.y(), pos.z(), 0, 0, 0);
    }

    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void applyBurningNoxiousGasEffects(ServerLevel level, CallbackInfo ci) {
        if (!isBurning() || tickCount % 10 != 0)
            return;

        BlockPos blockPos = VPPotentSulfurBlockEntity.findBurningNoxiousGasSourceBlock(level, blockPosition().below());
        if (blockPos == null)
            return;

        AABB aabb = new AABB(blockPos).inflate(BURNING_NOXIOUS_GAS_RADIUS, 0, BURNING_NOXIOUS_GAS_RADIUS)
                .expandTowards(0, BURNING_NOXIOUS_GAS_RADIUS, 0);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb, EntitySelector.NO_SPECTATORS
                .and(EntitySelector.ENTITY_STILL_ALIVE));

        for (LivingEntity entity : entities)
            if (!entity.is(VPTags.EntityTypes.NOT_AFFECTED_BY_NOXIOUS_GAS)
                    && VPPotentSulfurBlockEntity.isBurningNoxiousGasPassable(level, blockPos, entity.getEyePosition()))
                SulfurCubeConfig.get().burningNoxiousGasEffects().forEach(mobEffectInstance ->
                        entity.addEffect(new MobEffectInstance(mobEffectInstance)));
    }
}
