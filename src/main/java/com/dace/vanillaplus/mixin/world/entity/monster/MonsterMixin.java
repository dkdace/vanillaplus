package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.dace.vanillaplus.world.entity.modifier.MobModifier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Monster.class)
public abstract class MonsterMixin<T extends Monster, U extends MobModifier> extends MobMixin<T, U> {
    @Shadow
    public ItemStack getProjectile(ItemStack heldWeapon) {
        throw new UnsupportedOperationException();
    }
}
