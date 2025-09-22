package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.custom.CustomModifiableData;
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
public abstract class EntityMixin implements CustomModifiableData<EntityType<?>, EntityModifier> {
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

    @Override
    public void apply(@NonNull EntityModifier modifier) {
        // 미사용
    }
}
