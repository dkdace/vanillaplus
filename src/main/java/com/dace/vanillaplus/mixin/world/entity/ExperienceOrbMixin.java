package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.VPItemStack;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin extends EntityMixin<ExperienceOrb, EntityModifier.LivingEntityModifier> {
    @ModifyArg(method = "repairPlayerItems", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getRandomItemWith(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Optional;"),
            index = 2)
    private Predicate<ItemStack> preventRepair(Predicate<ItemStack> filter, @Local(argsOnly = true) ServerPlayer serverPlayer) {
        return filter.and(itemStack -> {
            VPItemStack vpItemStack = VPItemStack.cast(itemStack);
            if (serverPlayer.hasInfiniteMaterials() || vpItemStack.getRepairLimit() < vpItemStack.getMaxRepairLimit())
                return true;

            return serverPlayer.getInventory().getNonEquipmentItems().stream().anyMatch(targetItemStack -> {
                if (targetItemStack.is(Items.LAPIS_LAZULI)) {
                    targetItemStack.shrink(1);
                    vpItemStack.setRepairLimit(0);

                    return true;
                }

                return false;
            });
        });
    }

    @ModifyExpressionValue(method = "repairPlayerItems", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private int modifyRepairValue(int original, @Local(argsOnly = true) ServerPlayer serverPlayer, @Local ItemStack itemStack) {
        if (serverPlayer.hasInfiniteMaterials())
            return original;

        VPItemStack vpItemStack = VPItemStack.cast(itemStack);

        int repairLimit = vpItemStack.getRepairLimit();
        int finalValue = Math.min(original, vpItemStack.getMaxRepairLimit() - repairLimit);
        vpItemStack.setRepairLimit(repairLimit + finalValue);

        return finalValue;
    }
}
