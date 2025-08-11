package com.dace.vanillaplus.mixin;

import com.dace.vanillaplus.Rebalance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Piglin.class)
public final class PiglinMixin {
    @Overwrite
    public void performRangedAttack(LivingEntity entity, float speed) {
        Piglin piglin = (Piglin) (Object) this;
        piglin.performCrossbowAttack(piglin, Rebalance.Crossbow.MOB_SHOOTING_POWER_ARROW);
    }
}
