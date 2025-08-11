package com.dace.vanillaplus.mixin;

import com.dace.vanillaplus.rebalance.Rebalance;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperienceOrb.class)
public final class ExperienceOrbMixin {
    @Inject(method = "repairPlayerItems", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;modifyDurabilityToRepairFromXp(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;I)I"),
            cancellable = true)
    private void preventRepair(ServerPlayer player, int amount, CallbackInfoReturnable<Integer> cir, @Local ItemStack itemStack) {
        if (itemStack.getDamageValue() <= itemStack.getMaxDamage() * Rebalance.MENDING_REPAIR_LIMIT)
            cir.setReturnValue(amount);
    }

    @ModifyArg(method = "repairPlayerItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setDamageValue(I)V"))
    private int modifyRepairAmount(int damageValue, @Local ItemStack itemStack) {
        return (int) Math.max(damageValue, itemStack.getMaxDamage() * Rebalance.MENDING_REPAIR_LIMIT);
    }
}
