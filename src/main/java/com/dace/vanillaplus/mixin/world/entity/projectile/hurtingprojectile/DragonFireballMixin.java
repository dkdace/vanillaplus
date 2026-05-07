package com.dace.vanillaplus.mixin.world.entity.projectile.hurtingprojectile;

import com.dace.vanillaplus.data.VPTags;
import com.dace.vanillaplus.extension.world.entity.boss.enderdragon.VPEnderDragon;
import com.dace.vanillaplus.mixin.world.entity.projectile.ProjectileMixin;
import com.dace.vanillaplus.world.entity.modifier.EntityModifier;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonFireball.class)
public abstract class DragonFireballMixin extends ProjectileMixin<DragonFireball, EntityModifier> {
    @Unique
    private static final int MAX_EXPLOSION_RESISTANCE = 1;

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState block, FluidState fluid,
                                             float resistance) {
        if (getOwner() instanceof EnderDragon enderDragon && VPEnderDragon.cast(enderDragon).getDataModifier().getPhaseInfo().isPresent())
            return block.is(VPTags.Blocks.DRAGON_EXPLOSION_IMMUNE) ? resistance : Math.min(MAX_EXPLOSION_RESISTANCE, resistance);

        return super.getBlockExplosionResistance(explosion, level, pos, block, fluid, resistance);
    }

    @WrapOperation(method = "onHit", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/hurtingprojectile/DragonFireball;ownedBy(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean redirectHitCondition(DragonFireball instance, Entity entity, Operation<Boolean> original,
                                         @Local(argsOnly = true) HitResult hitResult) {
        return getOwner() instanceof EnderDragon enderDragon && VPEnderDragon.cast(enderDragon).getDataModifier().getPhaseInfo().isPresent()
                ? ((EntityHitResult) hitResult).getEntity().is(enderDragon)
                : original.call(instance, entity);
    }

    @ModifyArg(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setDuration(I)V"))
    private int modifyFlameDuration(int duration, @Local(name = "owner") Entity owner) {
        if (owner instanceof EnderDragon enderDragon)
            return VPEnderDragon.cast(enderDragon).getDataModifier().getPhaseInfo()
                    .map(phaseInfo -> phaseInfo.fireball().flameDuration())
                    .orElse(duration);

        return duration;
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/hurtingprojectile/DragonFireball;discard()V"))
    private void explode(HitResult hitResult, CallbackInfo ci) {
        if (getOwner() instanceof EnderDragon enderDragon)
            VPEnderDragon.cast(enderDragon).getDataModifier().getPhaseInfo().ifPresent(phaseInfo ->
                    level().explode(getThis(), getX(), getY(), getZ(), phaseInfo.fireball().explosionRadius(), Level.ExplosionInteraction.MOB));
    }

    @ModifyArg(method = "onHit", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/AreaEffectCloud;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)V"))
    private MobEffectInstance modifyFlameEffect(MobEffectInstance effect, @Local(name = "owner") Entity owner) {
        return owner instanceof EnderDragon enderDragon && VPEnderDragon.cast(enderDragon).getDataModifier().getPhaseInfo().isPresent()
                ? VPEnderDragon.cast(enderDragon).getFlameMobEffectInstance()
                : effect;
    }
}
