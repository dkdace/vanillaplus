package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.registryobject.VPDataComponentTypes;
import com.dace.vanillaplus.extension.world.item.VPItemStack;
import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import com.dace.vanillaplus.world.entity.EntityModifier;
import com.dace.vanillaplus.world.item.component.RepairWithXP;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin extends EntityMixin<ExperienceOrb, EntityModifier.LivingEntityModifier> {
    @Shadow
    protected abstract int repairPlayerItems(ServerPlayer player, int amount);

    @Redirect(method = "playerTouch", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ExperienceOrb;repairPlayerItems(Lnet/minecraft/server/level/ServerPlayer;I)I"))
    private int healWithXPBeforeRepair(ExperienceOrb instance, ServerPlayer player, int amount) {
        MutableFloat value = new MutableFloat(0);

        EnchantmentHelper.runIterationOnEquipment(player, (enchantmentHolder, level, enchantedItemInUse) ->
                VPEnchantment.cast(enchantmentHolder.value()).modifyHealPerXp(player.level(), level, enchantedItemInUse.itemStack(),
                        player, value));

        float healAmount = Math.min(amount * value.floatValue(), player.getMaxHealth() - player.getHealth());
        if (healAmount > 0) {
            player.heal(healAmount);
            amount -= (int) (healAmount / value.floatValue());
        }

        return repairPlayerItems(player, amount);
    }

    @ModifyArg(method = "repairPlayerItems", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getRandomItemWith(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Optional;"),
            index = 2)
    private Predicate<ItemStack> modifyRepairFilter(Predicate<ItemStack> predicate, @Local(argsOnly = true) ServerPlayer player) {
        return predicate.and(itemStack -> {
            RepairWithXP repairWithXP = itemStack.get(VPDataComponentTypes.REPAIR_WITH_XP.get());
            if (repairWithXP == null)
                return true;

            VPItemStack vpItemStack = VPItemStack.cast(itemStack);
            if (player.hasInfiniteMaterials() || vpItemStack.getRepairLimit() < vpItemStack.getMaxRepairLimit())
                return true;

            return repairWithXP.requiredItem().map(itemHolder ->
                    player.getInventory().getNonEquipmentItems().stream().anyMatch(targetItemStack -> {
                        if (targetItemStack.is(itemHolder)) {
                            targetItemStack.shrink(1);
                            vpItemStack.setRepairLimit(0);

                            return true;
                        }

                        return false;
                    })).orElse(false);
        });
    }

    @ModifyExpressionValue(method = "repairPlayerItems", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private int modifyRepairValue(int original, @Local(argsOnly = true) ServerPlayer player, @Local(name = "itemStack") ItemStack itemStack) {
        if (!itemStack.has(VPDataComponentTypes.REPAIR_WITH_XP.get()) || player.hasInfiniteMaterials())
            return original;

        VPItemStack vpItemStack = VPItemStack.cast(itemStack);

        int repairLimit = vpItemStack.getRepairLimit();
        int finalValue = Math.min(original, vpItemStack.getMaxRepairLimit() - repairLimit);
        vpItemStack.setRepairLimit(repairLimit + finalValue);

        return finalValue;
    }

    @ModifyArg(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;giveExperiencePoints(I)V"))
    private int modifyFinalXP(int points, @Local(name = "serverPlayer") ServerPlayer serverPlayer) {
        MutableFloat value = new MutableFloat(1);

        EnchantmentHelper.runIterationOnEquipment(serverPlayer, (enchantmentHolder, level, enchantedItemInUse) ->
                VPEnchantment.cast(enchantmentHolder.value()).modifyXpMultiplier(serverPlayer.level(), level, enchantedItemInUse.itemStack(),
                        serverPlayer, value));

        int finalXp = (int) Math.floor(points * value.floatValue());
        if (random.nextFloat() < points * value.floatValue() - finalXp)
            finalXp++;

        return finalXp;
    }
}
