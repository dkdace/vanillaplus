package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Monster.class)
public abstract class MonsterMixin<T extends Monster> extends MobMixin<T> {
    @Shadow
    public ItemStack getProjectile(ItemStack heldWeapon) {
        throw new UnsupportedOperationException();
    }
}
