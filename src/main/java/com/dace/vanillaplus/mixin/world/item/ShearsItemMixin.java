package com.dace.vanillaplus.mixin.world.item;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShearsItem.class)
public abstract class ShearsItemMixin extends ItemMixin<ShearsItem> {
    @Definition(id = "entity", local = @Local(type = LivingEntity.class, argsOnly = true))
    @Definition(id = "Shearable", type = Shearable.class)
    @Expression("entity instanceof Shearable")
    @ModifyExpressionValue(method = "interactLivingEntity", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyDefaultShearingCondition(boolean condition, @Local(argsOnly = true) LivingEntity entity) {
        return condition && !(entity instanceof SulfurCube);
    }
}
