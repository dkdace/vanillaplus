package com.dace.vanillaplus.mixin;

import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IronGolem.class)
public final class IronGolemMixin extends MobMixin {
    @Override
    protected AABB modifyAttackBoundingBox(AABB aabb) {
        return aabb.inflate(0.1, 0.1, 0.1);
    }
}
