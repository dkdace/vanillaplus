package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.dace.vanillaplus.extension.world.entity.VPEntity;
import com.dace.vanillaplus.world.entity.EntityModifier;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin<T extends Entity, U extends EntityModifier> implements VPEntity<T, U> {
    @Shadow
    public int tickCount;
    @Shadow
    @Final
    protected RandomSource random;
    @Unique
    @Nullable
    private U dataModifier;

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
    public abstract float getYRot();

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

    @Shadow
    @Nullable
    public abstract LivingEntity getControllingPassenger();

    @Shadow
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos blockPos, BlockState blockState, FluidState fluidState,
                                             float explosionPower) {
        return 0;
    }

    @Shadow
    public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
    }

    @Override
    @NonNull
    public Optional<U> getDataModifier() {
        return Optional.ofNullable(dataModifier);
    }

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable U dataModifier) {
        this.dataModifier = dataModifier;
    }

    @Unique
    private float getFinalFootstepVolume(float volume) {
        return getThis() instanceof LivingEntity livingEntity
                ? (float) (volume * livingEntity.getAttributeValue(VPAttributes.VIBRATION_TRANSMIT_RANGE.getHolder().orElseThrow()))
                : volume;
    }

    @ModifyArg(method = "playStepSound", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"), index = 1)
    private float modifyFootstepVolume0(float volume) {
        return getFinalFootstepVolume(volume);
    }

    @ModifyArg(method = "playMuffledStepSound", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"), index = 1)
    private float modifyFootstepVolume1(float volume) {
        return getFinalFootstepVolume(volume);
    }

    @ModifyArg(method = "playCombinationStepSounds", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"), index = 1)
    private float modifyFootstepVolume2(float volume) {
        return getFinalFootstepVolume(volume);
    }
}
