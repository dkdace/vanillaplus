package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.registryobject.VPAttributes;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin<T extends LivingEntity, U extends EntityModifier.LivingEntityModifier> extends EntityMixin<T, U> {
    @Shadow
    protected Brain<?> brain;
    @Mutable
    @Shadow
    @Final
    private AttributeMap attributes;

    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder modifyDefaultAttributes(AttributeSupplier.Builder builder) {
        return builder.add(VPAttributes.PROJECTILE_KNOCKBACK_RESISTANCE.getHolder().orElseThrow());
    }

    @Shadow
    public abstract void stopRiding();

    @Shadow
    public abstract boolean hasLineOfSight(Entity target);

    @ModifyArg(method = "hasLineOfSight(Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;hasLineOfSight(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/ClipContext$Block;Lnet/minecraft/world/level/ClipContext$Fluid;D)Z"),
            index = 1)
    private ClipContext.Block modifyLineOfSightClipContextBlock(ClipContext.Block block) {
        return ClipContext.Block.VISUAL;
    }

    @Inject(method = "die", at = @At("TAIL"))
    protected void onDie(DamageSource damageSource, CallbackInfo ci) {
        // 미사용
    }

    @Definition(id = "deathprotection", local = @Local(type = DeathProtection.class))
    @Expression("deathprotection != null")
    @ModifyExpressionValue(method = "checkTotemDeathProtection", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    protected boolean canUseTotem(boolean canUse, @Local(ordinal = 1) ItemStack itemStack) {
        return canUse;
    }

    @Inject(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    protected void onUseTotem(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir, @Local ItemStack itemStack) {
        // 미사용
    }

    @Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    public void redirectKnockback(LivingEntity instance, double strength, double ratioX, double ratioZ, @Local(argsOnly = true) DamageSource damageSource) {
        knockback(damageSource, strength, ratioX, ratioZ);
    }

    @Unique
    private void knockback(@Nullable DamageSource damageSource, double strength, double ratioX, double ratioZ) {
        LivingKnockBackEvent event = ForgeEventFactory.onLivingKnockBack(self(), (float) strength, ratioX, ratioZ);
        if (event == null)
            return;

        strength = event.getStrength();
        ratioX = event.getRatioX();
        ratioZ = event.getRatioZ();

        strength *= 1.0 - VPAttributes.getFinalKnockbackResistance(self(), damageSource);

        if (strength > 0) {
            hasImpulse = true;
            Vec3 oldVelocity = getDeltaMovement();

            while (ratioX * ratioX + ratioZ * ratioZ < 1.0E-5F) {
                ratioX = (Math.random() - Math.random()) * 0.01;
                ratioZ = (Math.random() - Math.random()) * 0.01;
            }

            Vec3 velocity = new Vec3(ratioX, 0.0, ratioZ).normalize().scale(strength);
            setDeltaMovement(oldVelocity.x / 2.0 - velocity.x, onGround() ? Math.min(0.4, oldVelocity.y / 2.0 + strength) : oldVelocity.y,
                    oldVelocity.z / 2.0 - velocity.z);
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable U dataModifier) {
        super.setDataModifier(dataModifier);

        if (dataModifier != null)
            attributes.apply(dataModifier.getPackedAttributes());
    }
}
