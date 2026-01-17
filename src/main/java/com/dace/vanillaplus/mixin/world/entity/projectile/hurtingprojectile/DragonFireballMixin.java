package com.dace.vanillaplus.mixin.world.entity.projectile.hurtingprojectile;

import com.dace.vanillaplus.VPTags;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.world.entity.boss.enderdragon.VPEnderDragon;
import com.dace.vanillaplus.mixin.world.entity.EntityMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
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
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonFireball.class)
public abstract class DragonFireballMixin extends EntityMixin<DragonFireball, EntityModifier> {
    @Unique
    private static final int MAX_EXPLOSION_RESISTANCE = 1;

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos blockPos, BlockState blockState, FluidState fluidState,
                                             float explosionPower) {
        if (getThis().getOwner() instanceof EnderDragon enderDragon && VPEnderDragon.cast(enderDragon).getDataModifier().isPresent())
            return blockState.is(VPTags.Blocks.DRAGON_EXPLOSION_IMMUNE) ? explosionPower : Math.min(MAX_EXPLOSION_RESISTANCE, explosionPower);

        return super.getBlockExplosionResistance(explosion, level, blockPos, blockState, fluidState, explosionPower);
    }

    @ModifyExpressionValue(method = "onHit", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/hurtingprojectile/DragonFireball;ownedBy(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean modifyHitCondition(boolean original) {
        return getThis().getOwner() instanceof EnderDragon enderDragon && VPEnderDragon.cast(enderDragon).getDataModifier().isPresent() || original;
    }

    @ModifyArg(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setDuration(I)V"))
    private int modifyFlameDuration(int duration, @Local Entity owner) {
        if (owner instanceof EnderDragon enderDragon)
            return VPEnderDragon.cast(enderDragon).getDataModifier()
                    .map(enderDragonModifier -> enderDragonModifier.getPhaseInfo().getFireball().getFlameDuration())
                    .orElse(duration);

        return duration;
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/hurtingprojectile/DragonFireball;discard()V"))
    private void explode(HitResult hitResult, CallbackInfo ci, @Local Entity owner) {
        if (owner instanceof EnderDragon enderDragon)
            VPEnderDragon.cast(enderDragon).getDataModifier().ifPresent(enderDragonModifier ->
                    level().explode(getThis(), getX(), getY(), getZ(), enderDragonModifier.getPhaseInfo().getFireball().getExplosionRadius(),
                            Level.ExplosionInteraction.MOB));
    }

    @ModifyArg(method = "onHit", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/AreaEffectCloud;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)V"))
    private MobEffectInstance modifyFlameEffect(MobEffectInstance mobEffectInstance, @Local Entity owner) {
        return owner instanceof EnderDragon enderDragon && VPEnderDragon.cast(enderDragon).getDataModifier().isPresent()
                ? VPEnderDragon.cast(enderDragon).getFlameMobEffectInstance()
                : mobEffectInstance;
    }
}
