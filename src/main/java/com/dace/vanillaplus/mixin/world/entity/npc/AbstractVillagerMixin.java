package com.dace.vanillaplus.mixin.world.entity.npc;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin<T extends AbstractVillager, U extends EntityModifier.LivingEntityModifier> extends MobMixin<T, U> {
    @Shadow
    public abstract MerchantOffers getOffers();

    @Shadow
    public abstract boolean isTrading();
}
