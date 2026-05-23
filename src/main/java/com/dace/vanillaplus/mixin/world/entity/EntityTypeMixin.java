package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.extension.world.entity.VPEntityType;
import com.dace.vanillaplus.world.entity.EntityConfig;
import lombok.NonNull;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin implements VPEntityType {
    @Unique
    @Nullable
    private EntityConfig dataModifier;

    @Override
    @NonNull
    public VPDataComponentMap getConfigComponents() {
        return getDataModifier().map(EntityConfig::components).orElse(VPDataComponentMap.EMPTY);
    }

    @Override
    @NonNull
    public Optional<EntityConfig> getDataModifier() {
        return Optional.ofNullable(dataModifier);
    }

    @Override
    public void setDataModifier(@Nullable EntityConfig dataModifier) {
        this.dataModifier = dataModifier;
    }
}
