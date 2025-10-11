package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.VPModifiableData;
import lombok.Getter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin<T extends Entity, U extends EntityModifier> implements VPModifiableData<EntityType<?>, U> {
    @Unique
    @Nullable
    @Getter
    protected U dataModifier;
    @Mutable
    @Shadow
    @Final
    private EntityType.EntityFactory<T> factory;

    @Override
    @SuppressWarnings("unchecked")
    public void setDataModifier(@Nullable U dataModifier) {
        this.dataModifier = dataModifier;
        EntityType.EntityFactory<T> oldFactory = factory;

        factory = (entityType, level) -> {
            T entity = oldFactory.create(entityType, level);
            if (entity != null)
                ((VPModifiableData<EntityType<?>, EntityModifier>) entity).setDataModifier(dataModifier);

            return entity;
        };
    }
}
