package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Monster.class)
public abstract class MonsterMixin<T extends Monster, U extends EntityModifier.LivingEntityModifier> extends MobMixin<T, U> {
    @ModifyReturnValue(method = "getProjectile", at = @At("RETURN"))
    public ItemStack modifyProjectileItem(ItemStack itemStack) {
        return itemStack;
    }
}
