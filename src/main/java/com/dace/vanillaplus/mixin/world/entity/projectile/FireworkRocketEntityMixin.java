package com.dace.vanillaplus.mixin.world.entity.projectile;

import com.dace.vanillaplus.custom.CustomModifiableData;
import com.dace.vanillaplus.mixin.world.entity.EntityMixin;
import com.dace.vanillaplus.rebalance.modifier.EntityModifier;
import com.dace.vanillaplus.rebalance.modifier.ItemModifier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends EntityMixin<EntityModifier.LivingEntityModifier> {
    @ModifyArgs(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;",
            ordinal = 0))
    @SuppressWarnings("unchecked")
    private void modifyElytraDeltaMovement(Args args, @Local(ordinal = 0) Vec3 direction, @Local(ordinal = 1) Vec3 velocity) {
        ItemModifier.ElytraModifier itemModifier = Objects.requireNonNull(((CustomModifiableData<Item, ItemModifier.ElytraModifier>) Items.ELYTRA)
                .getDataModifier());

        args.setAll(direction.x * 0.1 + (direction.x * itemModifier.getFireworkAddSpeedMultiplier() - velocity.x)
                        * itemModifier.getFireworkFinalSpeedModifier(),
                direction.y * 0.1 + (direction.y * itemModifier.getFireworkAddSpeedMultiplier() - velocity.y)
                        * itemModifier.getFireworkFinalSpeedModifier(),
                direction.z * 0.1 + (direction.z * itemModifier.getFireworkAddSpeedMultiplier() - velocity.z)
                        * itemModifier.getFireworkFinalSpeedModifier());
    }
}
