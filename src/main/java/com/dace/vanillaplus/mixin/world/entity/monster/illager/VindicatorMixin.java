package com.dace.vanillaplus.mixin.world.entity.monster.illager;

import com.dace.vanillaplus.data.RaiderEffect;
import com.dace.vanillaplus.data.modifier.EntityModifier;
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
    public void applyRaidBuffs(ServerLevel serverLevel, int wave, boolean ignored) {
        getRaiderEffect(RaiderEffect.VindicatorEffect.class).ifPresent(vindicatorEffect -> {
            setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));

            vindicatorEffect.getEnchantItemInfos().forEach(enchantItemEffect -> enchantItemEffect.applyEnchantment(getThis()));
            vindicatorEffect.getMobEffectInfos().forEach(mobEffectEffect -> mobEffectEffect.applyMobEffect(getThis()));
        });
    }
}
