package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.VPModifiableData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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
    public abstract float getBbHeight();

    @Shadow
    public abstract Vec3 position();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract boolean onGround();

    @Shadow
    @Nullable
    public abstract Entity getVehicle();

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
    public void setDataModifier(@Nullable T dataModifier) {
        // 미사용
    }
}
