package com.dace.vanillaplus.mixin.world.entity.projectile;

import com.dace.vanillaplus.VPTags;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.world.entity.boss.enderdragon.VPEnderDragon;
import com.dace.vanillaplus.mixin.world.entity.EntityMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonFireball.class)
public abstract class DragonFireballMixin extends EntityMixin<DragonFireball, EntityModifier> {
    @ModifyExpressionValue(method = "onHit", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/DragonFireball;ownedBy(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean modifyHitCondition(boolean original) {
        return true;
    }

    @ModifyArg(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setDuration(I)V"))
    private int modifyFlameDuration(int duration) {
        return ((EntityModifier.EnderDragonModifier) EntityModifier.fromEntityTypeOrThrow(EntityType.ENDER_DRAGON)).getPhaseInfo().getFireball()
                .getFlameDuration();
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/DragonFireball;discard()V"))
    private void explode(HitResult hitResult, CallbackInfo ci) {
        float explosionRadius = ((EntityModifier.EnderDragonModifier) EntityModifier.fromEntityTypeOrThrow(EntityType.ENDER_DRAGON)).getPhaseInfo()
                .getFireball().getExplosionRadius();

        level().explode(getThis(), getX(), getY(), getZ(), explosionRadius, Level.ExplosionInteraction.MOB);
    }

    @ModifyArg(method = "onHit", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/AreaEffectCloud;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)V"))
    private MobEffectInstance modifyFlameEffect(MobEffectInstance mobEffectInstance, @Local Entity owner) {
        return owner instanceof EnderDragon enderDragon ? VPEnderDragon.cast(enderDragon).getFlameMobEffectInstance() : mobEffectInstance;
    }

    @Override
    protected float modifyBlockExplosionResistance(float resistance, BlockState blockState, float explosionPower) {
        return blockState.is(VPTags.Blocks.DRAGON_EXPLOSION_IMMUNE) ? explosionPower : Math.min(1, explosionPower);
    }
}
