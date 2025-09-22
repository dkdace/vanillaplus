package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.custom.CustomModifiableData;
import com.dace.vanillaplus.rebalance.modifier.EntityModifier;
import lombok.NonNull;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin<T extends Entity> implements CustomModifiableData<EntityType<?>, EntityModifier> {
    @Mutable
    @Shadow
    @Final
    private EntityType.EntityFactory<T> factory;

    @Override
    @SuppressWarnings("unchecked")
    public void apply(@NonNull EntityModifier modifier) {
        EntityType.EntityFactory<T> oldFactory = factory;

        factory = (entityType, level) -> {
            T entity = oldFactory.create(entityType, level);
            if (entity != null)
                ((CustomModifiableData<EntityType<?>, EntityModifier>) entity).apply(modifier);

            return entity;
        };
    }
}
