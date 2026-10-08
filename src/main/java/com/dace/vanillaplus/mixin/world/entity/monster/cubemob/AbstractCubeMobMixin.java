package com.dace.vanillaplus.mixin.world.entity.monster.cubemob;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractCubeMob.class)
public abstract class AbstractCubeMobMixin<T extends AbstractCubeMob> extends MobMixin<T> {
    @Definition(id = "entity", local = @Local(type = Entity.class, argsOnly = true))
    @Definition(id = "IronGolem", type = IronGolem.class)
    @Expression("entity instanceof IronGolem")
    @ModifyExpressionValue(method = "push", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyDamageCondition(boolean condition, @Local(argsOnly = true) Entity entity) {
        return condition || getConfigComponents().getBoolean(EntityConfigComponentTypes.ATTACK_NPCS) && entity instanceof AbstractVillager;
    }
}
