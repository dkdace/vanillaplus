package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.world.entity.VPEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.Getter;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin<T extends Entity, U extends EntityModifier> implements VPEntity<T, U> {
    @Unique
    @Nullable
    @Getter
    protected U dataModifier;
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
    public abstract double getX();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract boolean onGround();

    @Shadow
    public abstract SynchedEntityData getEntityData();

    @Shadow
    @Nullable
    public abstract Entity getVehicle();

    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract boolean closerThan(Entity entity, double distance);

    @Shadow
    public abstract void playSound(SoundEvent soundEvent, float volume, float pitch);

    @Shadow
    @Nullable
    public abstract ItemEntity spawnAtLocation(ServerLevel serverLevel, ItemLike item);

    @ModifyReturnValue(method = "getBlockExplosionResistance", at = @At("RETURN"))
    protected float modifyBlockExplosionResistance(float resistance, @Local(argsOnly = true) BlockState blockState,
                                                   @Local(argsOnly = true) float explosionPower) {
        return resistance;
    }

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable U dataModifier) {
        this.dataModifier = dataModifier;
    }
}
