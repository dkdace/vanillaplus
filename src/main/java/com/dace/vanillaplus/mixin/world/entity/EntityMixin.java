package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.rebalance.modifier.EntityModifier;
import lombok.NonNull;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public abstract class EntityMixin<T extends EntityModifier> implements VPModifiableData<EntityType<?>, T> {
    @Shadow
    public boolean hasImpulse;
    @Shadow
    @Final
    protected RandomSource random;

    @Shadow
    public abstract float getBbWidth();

    @Shadow
    public abstract Vec3 position();

    @Shadow
    public abstract boolean onGround();

    @Shadow
    public abstract float getBbHeight();

    @Shadow
    public abstract double getY();

    @Shadow
    @Nullable
    public abstract Entity getVehicle();

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    public abstract void setDeltaMovement(double x, double y, double z);

    @Override
    @Nullable
    public final T getDataModifier() {
        return VPModifiableData.getDataModifier(getType());
    }

    @Override
    public void setDataModifier(@NonNull T dataModifier) {
        // 미사용
    }
}
