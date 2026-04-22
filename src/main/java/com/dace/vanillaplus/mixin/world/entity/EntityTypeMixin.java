package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.extension.world.entity.VPEntity;
import com.dace.vanillaplus.world.entity.EntityModifier;
import lombok.NonNull;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.Optional;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin<T extends Entity, U extends EntityModifier> implements VPModifiableData<EntityType<?>, U>, VPMixin<EntityType<?>> {
    @Unique
    @Nullable
    private U dataModifier;
    @Mutable
    @Shadow
    @Final
    private EntityType.EntityFactory<T> factory;

    @Override
    @NonNull
    public Optional<U> getDataModifier() {
        return Optional.ofNullable(dataModifier);
    }

    @Override
    public void setDataModifier(@Nullable U dataModifier) {
        this.dataModifier = dataModifier;
        EntityType.EntityFactory<T> oldFactory = factory;

        factory = (entityType, level) -> {
            T entity = oldFactory.create(entityType, level);
            if (entity != null)
                VPEntity.cast(entity).setDataModifier(dataModifier);

            return entity;
        };
    }
}
