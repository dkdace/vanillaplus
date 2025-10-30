package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.RaiderEffect;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Vindicator.class)
public abstract class VindicatorMixin extends AbstractIllagerMixin<Vindicator, EntityModifier.LivingEntityModifier> {
    @Overwrite
    public void applyRaidBuffs(ServerLevel serverLevel, int wave, boolean ignored) {
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));

        RaiderEffect.VindicatorEffect vindicatorEffect = RaiderEffect.fromEntityType(getType());

        vindicatorEffect.getEnchantItemInfos().forEach(enchantItemEffect -> enchantItemEffect.applyEnchantment(getThis()));
        vindicatorEffect.getMobEffectInfos().forEach(mobEffectEffect -> mobEffectEffect.applyMobEffect(getThis()));
    }
}
