package com.dace.vanillaplus.mixin.world.entity;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.level.ClipContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
    @Shadow
    protected Brain<?> brain;

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
}
