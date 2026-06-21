package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.dace.vanillaplus.extension.world.entity.VPEntity;
import com.dace.vanillaplus.extension.world.entity.VPEntityType;
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
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public abstract class EntityMixin<T extends Entity> implements VPEntity<T> {
    @Shadow
    public int tickCount;
    @Shadow
    @Final
    protected RandomSource random;

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
    public abstract boolean closerThan(Entity other, double distance);

    @Shadow
    public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Shadow
    @Nullable
    public abstract ItemEntity spawnAtLocation(ServerLevel level, ItemLike resource);

    @Shadow
    @Nullable
    public abstract LivingEntity getControllingPassenger();

    @Shadow
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState block, FluidState fluid,
                                             float resistance) {
        return 0;
    }

    @Shadow
    public void thunderHit(ServerLevel level, LightningBolt lightningBolt) {
    }

    @Override
    @NonNull
    public final VPDataComponentMap getConfigComponents() {
        return VPEntityType.cast(getType()).getConfigComponents();
    }

    @ModifyArg(method = {"playStepSound", "playMuffledStepSound", "playCombinationStepSounds"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"), index = 1)
    private float modifyFootstepVolume(float volume) {
        return getThis() instanceof LivingEntity livingEntity
                ? (float) (volume * livingEntity.getAttributeValue(VPAttributes.VIBRATION_TRANSMIT_RANGE.getHolder().orElseThrow()))
                : volume;
    }
}
