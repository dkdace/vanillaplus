package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Monster.class)
public abstract class MonsterMixin<T extends Monster, U extends EntityModifier.LivingEntityModifier> extends MobMixin<T, U> {
    @Shadow
    @UnknownNullability
    public ItemStack getProjectile(ItemStack weapon) {
        return null;
    }
}
