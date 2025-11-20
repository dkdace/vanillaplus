package com.dace.vanillaplus.mixin.world.entity.boss.enderdragon;

import com.dace.vanillaplus.VPTags;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.world.entity.boss.enderdragon.VPEnderDragon;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.dace.vanillaplus.registryobject.VPSoundEvents;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin extends MobMixin<EnderDragon, EntityModifier.EnderDragonModifier> implements VPEnderDragon {
    @Unique
    private static final int COLOR_METEOR = ARGB.color(223, 0, 249);
    @Unique
    private static final EntityDataAccessor<Optional<BlockPos>> METEOR_POS = SynchedEntityData.defineId(EnderDragon.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    @Unique
    private final HashSet<ServerPlayer> targets = new HashSet<>();
    @Unique
    @Getter
    @Setter
    private int attackCooldown = 0;
    @Unique
    @Getter
    @Setter
    private int meteorAttackCooldown = 0;
    @Unique
    private double enderPearlDropRate = 0;
    @Unique
    private int enderPearlDropCount = 0;
    @Unique
    private boolean isMeteorExploding = false;
    @Shadow
    @Final
    private EnderDragonPhaseManager phaseManager;

    @Shadow
    public abstract SoundSource getSoundSource();

    @Unique
    private double getMovementSpeedMultiplier(@NonNull DragonPhaseInstance dragonPhaseInstance) {
        return dragonPhaseInstance.getPhase() == EnderDragonPhase.DYING
                ? 1
                : Objects.requireNonNull(dataModifier).getMovementSpeedMultiplier().get(getThis());
    }

    @Override
    public BlockPos getMeteorPos() {
        return getEntityData().get(METEOR_POS).map(BlockPos::immutable).orElse(null);
    }

    @Unique
    public void setMeteorPos(@Nullable BlockPos blockPos) {
        getEntityData().set(METEOR_POS, Optional.ofNullable(blockPos));
    }

    @Unique
    private void stepMeteorClient(@NonNull BlockPos blockPos) {
        level().addParticle(ColorParticleOption.create(ParticleTypes.FLASH, COLOR_METEOR), true, false, blockPos.getX(),
                blockPos.getY(), blockPos.getZ(), 0, 0, 0);
        level().playLocalSound(blockPos, VPSoundEvents.ENDER_DRAGON_FALL_METEOR.get(), getSoundSource(), 3,
                1 + random.nextFloat() * 0.2F, false);

        for (int i = 0; i < 30; i++) {
            Vec3 pos = blockPos.getCenter().offsetRandom(random, 3);

            level().addParticle(new DustParticleOptions(COLOR_METEOR, 4), true, false, pos.x(), pos.y(), pos.z(),
                    0, 0, 0);
        }
    }

    @Unique
    private void stepMeteorServer(@NonNull BlockPos blockPos) {
        if (!isMeteorExploding && !level().isEmptyBlock(blockPos))
            isMeteorExploding = true;

        if (!isMeteorExploding)
            return;

        float explosionRadius = Objects.requireNonNull(dataModifier).getPhaseInfo().getMeteor().getExplosionRadius();
        level().explode(getThis(), blockPos.getX(), blockPos.getY() + 1.0, blockPos.getZ(), explosionRadius, Level.ExplosionInteraction.MOB);
    }

    @ModifyArg(method = "onCrystalDestroyed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getNearestPlayer(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;DDD)Lnet/minecraft/world/entity/player/Player;"),
            index = 0)
    private TargetingConditions modifyCrystalDestroyTargetingConditions(TargetingConditions targetingConditions) {
        return getDefaultTargetingConditions();
    }

    @ModifyArg(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"),
            index = 1)
    private double setUpwardVelocityMultiplier(double velocity, @Local DragonPhaseInstance dragonPhaseInstance) {
        EnderDragonPhase<? extends DragonPhaseInstance> enderDragonPhase = dragonPhaseInstance.getPhase();

        if (enderDragonPhase == EnderDragonPhase.CHARGING_PLAYER)
            return velocity * 4;
        else if (enderDragonPhase == EnderDragonPhase.LANDING)
            return velocity * 2;

        return velocity;
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "CONSTANT", args = "floatValue=0.06", ordinal = 1))
    private float setMovementVelocityMultiplier(float velocity, @Local DragonPhaseInstance dragonPhaseInstance) {
        return (float) (velocity * getMovementSpeedMultiplier(dragonPhaseInstance));
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/enderdragon/phases/DragonPhaseInstance;getTurnSpeed()F"))
    private float setTurnVelocityMultiplier(float velocity, @Local DragonPhaseInstance dragonPhaseInstance) {
        return (float) (velocity * getMovementSpeedMultiplier(dragonPhaseInstance));
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(METEOR_POS, Optional.empty());
    }

    @Inject(method = "onSyncedDataUpdated", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V"))
    private void onSynchedDataUpdated(EntityDataAccessor<?> entityDataAccessor, CallbackInfo ci) {
        if (!METEOR_POS.equals(entityDataAccessor) || !this.level().isClientSide())
            return;

        BlockPos meteorPos = getMeteorPos();
        if (meteorPos != null)
            stepMeteorClient(meteorPos);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditionalSaveData(ValueOutput valueOutput, CallbackInfo ci) {
        valueOutput.putInt("AttackCooldown", attackCooldown);
        valueOutput.putInt("MeteorAttackCooldown", meteorAttackCooldown);
        valueOutput.putDouble("EnderPearlDropRate", enderPearlDropRate);
        valueOutput.putInt("EnderPearlDropCount", enderPearlDropCount);
        valueOutput.storeNullable("MeteorPos", BlockPos.CODEC, getMeteorPos());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditionalSaveData(ValueInput valueInput, CallbackInfo ci) {
        attackCooldown = valueInput.getIntOr("AttackCooldown", 0);
        meteorAttackCooldown = valueInput.getIntOr("MeteorAttackCooldown", 0);
        enderPearlDropRate = valueInput.getDoubleOr("EnderPearlDropRate", 0);
        enderPearlDropCount = valueInput.getIntOr("EnderPearlDropCount", 0);
        setMeteorPos(valueInput.read("MeteorPos", BlockPos.CODEC).orElse(null));
    }

    @Definition(id = "hurtTime", field = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;hurtTime:I")
    @Expression("this.hurtTime == 0")
    @ModifyExpressionValue(method = "aiStep", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyMeleeAttackCondition(boolean original) {
        EnderDragonPhase<? extends DragonPhaseInstance> enderDragonPhase = phaseManager.getCurrentPhase().getPhase();
        return original && enderDragonPhase != EnderDragonPhase.SITTING_SCANNING && enderDragonPhase != EnderDragonPhase.SITTING_FLAMING;
    }

    @ModifyExpressionValue(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/boss/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At(value = "CONSTANT", args = "floatValue=0.25"))
    private float modifySittingAllowedDamage(float allowedDamage) {
        return Objects.requireNonNull(dataModifier).getPhaseInfo().getSitting().getAllowedDamageRatio();
    }

    @Inject(method = "reallyHurt", at = @At("TAIL"))
    private void dropEnderPearlOnHurt(ServerLevel serverLevel, DamageSource damageSource, float damage, CallbackInfo ci) {
        Objects.requireNonNull(dataModifier);

        if (!shouldDropLoot(serverLevel) || enderPearlDropCount >= dataModifier.getMaxEnderPearlDrops())
            return;

        enderPearlDropRate += dataModifier.getEnderPearlDropChance();
        if (enderPearlDropRate <= random.nextDouble())
            return;

        enderPearlDropRate = 0;
        enderPearlDropCount++;

        spawnAtLocation(serverLevel, Items.ENDER_PEARL);

        serverLevel.playSound(null, getX(), getY(), getZ(), VPSoundEvents.ENDER_DRAGON_DROP_PEARL.get(), getSoundSource(), 5,
                1 + random.nextFloat() * 0.2F);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/enderdragon/phases/DragonPhaseInstance;doServerTick(Lnet/minecraft/server/level/ServerLevel;)V",
            ordinal = 0))
    private void tickAttack(CallbackInfo ci, @Local ServerLevel serverLevel) {
        serverLevel.players().stream().filter(this::hasLineOfSight).forEach(targets::add);
        targets.removeIf(target -> !serverLevel.players().contains(target));

        if (attackCooldown > 0)
            attackCooldown--;
        if (meteorAttackCooldown > 0)
            meteorAttackCooldown--;

        BlockPos meteorPos = getMeteorPos();
        if (meteorPos == null)
            return;

        int velocity = Objects.requireNonNull(dataModifier).getPhaseInfo().getMeteor().getVelocity();

        for (int i = 0; i < velocity; i++) {
            if (meteorPos.getY() > serverLevel.getMinY() && serverLevel.isEmptyBlock(meteorPos)) {
                meteorPos = meteorPos.below();
                setMeteorPos(meteorPos);
                stepMeteorServer(meteorPos);

                continue;
            }

            setMeteorPos(null);
            isMeteorExploding = false;
            return;
        }
    }

    @ModifyExpressionValue(method = "tickDeath", at = @At(value = "CONSTANT", args = "intValue=12000"))
    private int modifyDropXPFirst(int xp) {
        return Objects.requireNonNull(dataModifier).getExperience().getFirst();
    }

    @ModifyExpressionValue(method = "tickDeath", at = @At(value = "CONSTANT", args = "intValue=500"))
    private int modifyDropXPSecond(int xp) {
        return Objects.requireNonNull(dataModifier).getExperience().getSecond();
    }

    @Override
    protected float modifyBlockExplosionResistance(float resistance, BlockState blockState, float explosionPower) {
        return blockState.is(VPTags.Blocks.DRAGON_EXPLOSION_IMMUNE) ? explosionPower : Math.min(3F, explosionPower);
    }

    @Override
    @NonNull
    public TargetingConditions getDefaultTargetingConditions() {
        return TargetingConditions.forCombat().range(getAttributeValue(Attributes.FOLLOW_RANGE)).ignoreLineOfSight().ignoreInvisibilityTesting()
                .selector((entity, level) -> entity instanceof ServerPlayer && targets.contains(entity));
    }

    @Override
    public boolean dropMeteor(@NonNull Vec3 pos) {
        if (level().getDifficulty() != Difficulty.HARD || getHealth() >= getMaxHealth() || getMeteorPos() != null)
            return false;

        pos = pos.offsetRandom(random, 20);
        setMeteorPos(BlockPos.containing(pos).atY(level().getMaxY()));

        level().playSound(null, pos.x(), pos.y(), pos.z(), VPSoundEvents.ENDER_DRAGON_SPAWN_METEOR.get(), getSoundSource(), 5, 1);
        return true;
    }

    @Override
    @NonNull
    public MobEffectInstance getFlameMobEffectInstance() {
        Difficulty difficulty = level().getDifficulty();

        return difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD
                ? new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, 1)
                : new MobEffectInstance(MobEffects.INSTANT_DAMAGE);
    }
}
