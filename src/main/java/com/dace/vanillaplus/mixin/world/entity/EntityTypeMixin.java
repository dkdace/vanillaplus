package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.extension.world.entity.VPEntityType;
import com.dace.vanillaplus.world.entity.EntityConfig;
import lombok.NonNull;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin implements VPEntityType {
    @Unique
    @Nullable
    private EntityConfig config;

    @Override
    @NonNull
    public VPDataComponentMap getConfigComponents() {
        return getConfig().components();
    }

    @Override
    @NonNull
    public EntityConfig getConfig() {
        return Objects.requireNonNull(config, "Not initialized yet");
    }

    @Override
    public void setConfig(@Nullable EntityConfig config) {
        this.config = config == null ? EntityConfig.DEFAULT : config;
    }
}
