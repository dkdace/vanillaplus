package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.extension.world.entity.VPEntityType;
import com.dace.vanillaplus.world.entity.EntityConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import lombok.NonNull;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin implements VPEntityType {
    @Unique
    @Nullable
    private EntityConfig config;

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "CONSTANT", args = "intValue=2147483647"))
    private static int modifyMaxUpdateInterval(int updateInterval) {
        return 20;
    }

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
