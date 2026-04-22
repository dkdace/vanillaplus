package com.dace.vanillaplus.mixin.world.entity.item;

import com.dace.vanillaplus.mixin.world.entity.EntityMixin;
import com.dace.vanillaplus.world.entity.EntityModifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends EntityMixin<ItemEntity, EntityModifier> {
    @Unique
    private static final int LIGHTNING_IMMUNE_DURATION = 8;

    @Override
    public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
        if (tickCount > LIGHTNING_IMMUNE_DURATION)
            super.thunderHit(serverLevel, lightningBolt);
    }
}
