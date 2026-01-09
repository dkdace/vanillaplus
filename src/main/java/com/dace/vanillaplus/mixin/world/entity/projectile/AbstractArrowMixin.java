package com.dace.vanillaplus.mixin.world.entity.projectile;

import com.dace.vanillaplus.data.modifier.DataModifierInfo;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.dace.vanillaplus.mixin.world.entity.EntityMixin;
import com.dace.vanillaplus.registryobject.VPAttributes;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin<T extends AbstractArrow, U extends EntityModifier> extends EntityMixin<T, U> {
    @Shadow
    private double baseDamage;

    @ModifyExpressionValue(method = "doKnockback", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    private double modifyKnockbackStrength(double original, @Local(argsOnly = true) LivingEntity livingEntity,
                                           @Local(argsOnly = true) DamageSource damageSource) {
        return VPAttributes.getFinalKnockbackResistance(livingEntity, damageSource);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;DDDLnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("TAIL"))
    private void modifyBaseDamage(EntityType<? extends AbstractArrow> entityType, double x, double y, double z, Level level, ItemStack pickupItemStack,
                                  ItemStack weaponItemStack, CallbackInfo ci) {
        if (weaponItemStack == null)
            return;

        ItemModifier.ProjectileWeaponModifier projectileWeaponModifier = DataModifierInfo.ITEM_MODIFIER.get(weaponItemStack.getItem());

        if (projectileWeaponModifier != null)
            baseDamage = projectileWeaponModifier.getBaseDamage();
    }

    @Definition(id = "livingentity", local = @Local(type = LivingEntity.class))
    @Definition(id = "Player", type = Player.class)
    @Expression("livingentity instanceof Player")
    @ModifyExpressionValue(method = "onHitEntity", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyHitSoundCondition(boolean original) {
        return true;
    }

    @ModifyArg(method = "onHitEntity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
    private float modifyDamage(float damage, @Local float velocity, @Local double baseDamage) {
        return (float) Math.clamp(velocity * baseDamage, 0, Integer.MAX_VALUE);
    }
}
