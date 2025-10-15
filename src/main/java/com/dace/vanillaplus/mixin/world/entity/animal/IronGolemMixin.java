package com.dace.vanillaplus.mixin.world.entity.animal;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IronGolem.class)
public abstract class IronGolemMixin extends MobMixin<IronGolem, EntityModifier.LivingEntityModifier> {
    @Override
    protected AABB modifyAttackBoundingBox(AABB aabb) {
        return aabb.inflate(0.5, 0.1, 0.5);
    }
}
