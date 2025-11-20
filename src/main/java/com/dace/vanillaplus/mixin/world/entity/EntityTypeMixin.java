package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.extension.world.entity.VPEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin<T extends Entity, U extends EntityModifier> implements VPModifiableData<EntityType<?>, U>, VPMixin<EntityType<?>> {
    @Mutable
    @Shadow
    @Final
    private EntityType.EntityFactory<T> factory;

    @Override
    public void setDataModifier(@Nullable U dataModifier) {
        EntityType.EntityFactory<T> oldFactory = factory;

        factory = (entityType, level) -> {
            T entity = oldFactory.create(entityType, level);
            if (entity != null)
                VPEntity.cast(entity).setDataModifier(dataModifier);

            return entity;
        };
    }
}
