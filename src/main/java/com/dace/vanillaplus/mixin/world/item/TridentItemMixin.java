package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.world.item.ItemModifier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin extends ItemMixin<TridentItem, ItemModifier.TridentModifier> {
    @Inject(method = "releaseUsing", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;startAutoSpinAttack(IFLnet/minecraft/world/item/ItemStack;)V"))
    private void applyCooldownOnRiptide(ItemStack itemStack, Level level, LivingEntity entity, int remainingTime, CallbackInfoReturnable<Boolean> cir,
                                        @Local(name = "player") Player player) {
        getDataModifier().ifPresent(tridentModifier ->
                player.getCooldowns().addCooldown(itemStack, tridentModifier.getRiptideCooldown()));
    }
}
