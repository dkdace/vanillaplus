package com.dace.vanillaplus.mixin.world.entity;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.OminousItemSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OminousItemSpawner.class)
public abstract class OminousItemSpawnerMixin extends EntityMixin<OminousItemSpawner> {
    @Definition(id = "spawnItemAfterTicks", field = "Lnet/minecraft/world/entity/OminousItemSpawner;spawnItemAfterTicks:J")
    @Expression("this.spawnItemAfterTicks - 36")
    @ModifyExpressionValue(method = "tickServer", at = @At("MIXINEXTRAS:EXPRESSION"))
    private long modifyWarnSoundTickCount(long ticks) {
        return Math.max(0, ticks);
    }
}
