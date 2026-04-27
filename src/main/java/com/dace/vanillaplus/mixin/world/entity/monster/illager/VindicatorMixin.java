package com.dace.vanillaplus.mixin.world.entity.monster.illager;

import com.dace.vanillaplus.world.entity.EntityModifier;
import com.dace.vanillaplus.world.entity.raid.RaiderEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Vindicator.class)
public abstract class VindicatorMixin extends AbstractIllagerMixin<Vindicator, EntityModifier.LivingEntityModifier> {
    @Overwrite
    public void applyRaidBuffs(ServerLevel level, int wave, boolean isCaptain) {
        getRaiderEffect(RaiderEffect.VindicatorEffect.class).ifPresent(vindicatorEffect -> {
            setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));

            vindicatorEffect.getEnchantItemInfos().forEach(enchantItemEffect -> enchantItemEffect.applyEnchantment(getThis()));
            vindicatorEffect.getMobEffectInfos().forEach(mobEffectEffect -> mobEffectEffect.applyMobEffect(getThis()));
        });
    }
}
