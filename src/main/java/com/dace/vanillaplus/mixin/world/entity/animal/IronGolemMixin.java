package com.dace.vanillaplus.mixin.world.entity.animal;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(IronGolem.class)
public abstract class IronGolemMixin extends MobMixin<IronGolem, EntityModifier.LivingEntityModifier> {
    @Override
    protected AABB modifyAttackBoundingBox(AABB aabb) {
        return aabb.inflate(0.5, 0.1, 0.5);
    }

    @ModifyArg(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/IronGolem;heal(F)V"))
    private float modifyHealAmount(float amount, @Local(argsOnly = true) Player player) {
        MutableFloat value = new MutableFloat(1);

        EnchantmentHelper.runIterationOnEquipment(player, (enchantmentHolder, level, enchantedItemInUse) ->
                VPEnchantment.cast(enchantmentHolder.value()).modifyIronGolemHealMultiplier(level, enchantedItemInUse.itemStack(), player, value));

        return amount * value.floatValue();
    }
}
