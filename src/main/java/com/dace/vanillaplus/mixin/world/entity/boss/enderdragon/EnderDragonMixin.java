package com.dace.vanillaplus.mixin.world.entity.boss.enderdragon;

import com.dace.vanillaplus.data.VPTags;
import com.dace.vanillaplus.data.registryobject.VPSoundEvents;
import com.dace.vanillaplus.extension.world.entity.boss.enderdragon.VPEnderDragon;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.dace.vanillaplus.world.entity.EntityModifier;
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
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
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
import java.util.Optional;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin extends MobMixin<EnderDragon, EntityModifier.EnderDragonModifier> implements VPEnderDragon {
    @Unique
    private static final int MAX_EXPLOSION_RESISTANCE = 1;
    @Unique
    private static final int METEOR_COLOR = ARGB.color(223, 0, 249);
    @Unique
    private static final int METEOR_POS_SPREAD = 20;
    @Unique
    private static final int ENDERMITE_POS_SPREAD = 4;
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
    @Shadow
    @Final
    private EnderDragonPhaseManager phaseManager;

    @Shadow
    public abstract SoundSource getSoundSource();

    @Unique
    private float getMovementSpeedMultiplier(@NonNull EntityModifier.EnderDragonModifier enderDragonModifier,
                                             @NonNull DragonPhaseInstance dragonPhaseInstance) {
        return dragonPhaseInstance.getPhase() == EnderDragonPhase.DYING ? 1 : (float) enderDragonModifier.getMovementSpeedMultiplier().get(getThis());
    }

    @Unique
    private void spawnEndermites(@NonNull EntityModifier.EnderDragonModifier enderDragonModifier, @NonNull ServerLevel level) {
        for (int i = 0; i < enderDragonModifier.getEndermiteCount(); i++) {
            Endermite endermite = EntityType.ENDERMITE.create(level, EntitySpawnReason.MOB_SUMMONED);

            if (endermite != null) {
                Vec3 pos = position().offsetRandomXZ(random, ENDERMITE_POS_SPREAD);

                endermite.snapTo(pos);
                level.addFreshEntity(endermite);
            }
        }
    }

    @Unique
    private void stepMeteorClient(@NonNull BlockPos blockPos) {
        level().addParticle(ColorParticleOption.create(ParticleTypes.FLASH, METEOR_COLOR), true, false, blockPos.getX(),
                blockPos.getY(), blockPos.getZ(), 0, 0, 0);
        level().playLocalSound(blockPos, VPSoundEvents.ENDER_DRAGON_FALL_METEOR.get(), getSoundSource(), 3,
                1 + random.nextFloat() * 0.2F, false);

        for (int i = 0; i < 30; i++) {
            Vec3 pos = blockPos.getCenter().offsetRandom(random, 3);

            level().addParticle(new DustParticleOptions(METEOR_COLOR, 4), true, false, pos.x(), pos.y(), pos.z(),
                    0, 0, 0);
        }
    }

    @Unique
    private void stepMeteorServer(@NonNull EntityModifier.EnderDragonModifier enderDragonModifier, @NonNull ServerLevel level,
                                  @NonNull BlockPos meteorPos) {
        int velocity = enderDragonModifier.getPhaseInfo().getMeteor().getVelocity();

        for (int i = 0; i < velocity; i++) {
            if (meteorPos.getY() > level.getMinY() && level.isEmptyBlock(meteorPos)) {
                meteorPos = meteorPos.below();
                setMeteorPos(meteorPos);

                continue;
            }

            setMeteorPos(null);
            level().explode(getThis(), meteorPos.getX(), meteorPos.getY() + 1, meteorPos.getZ(),
                    enderDragonModifier.getPhaseInfo().getMeteor().getExplosionRadius(), Level.ExplosionInteraction.MOB);

            return;
        }
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState block, FluidState fluid,
                                             float resistance) {
        if (getDataModifier().isPresent())
            return block.is(VPTags.Blocks.DRAGON_EXPLOSION_IMMUNE) ? resistance : Math.min(MAX_EXPLOSION_RESISTANCE, resistance);

        return super.getBlockExplosionResistance(explosion, level, pos, block, fluid, resistance);
    }

    @Override
    @NonNull
    public TargetingConditions getDefaultTargetingConditions() {
        return TargetingConditions.forCombat().range(getAttributeValue(Attributes.FOLLOW_RANGE)).ignoreLineOfSight().ignoreInvisibilityTesting()
                .selector((entity, _) -> entity instanceof ServerPlayer && targets.contains(entity));
    }

    @Override
    @NonNull
    public MobEffectInstance getFlameMobEffectInstance() {
        Difficulty difficulty = level().getDifficulty();

        return difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD
                ? new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, 1)
                : new MobEffectInstance(MobEffects.INSTANT_DAMAGE);
    }

    @Override
    public BlockPos getMeteorPos() {
        return getEntityData().get(METEOR_POS).map(BlockPos::immutable).orElse(null);
    }

    @Unique
    private void setMeteorPos(@Nullable BlockPos blockPos) {
        getEntityData().set(METEOR_POS, Optional.ofNullable(blockPos));
    }

    @Override
    public boolean dropMeteor(@NonNull Vec3 pos) {
        if (getHealth() >= getMaxHealth() || getMeteorPos() != null)
            return false;

        pos = pos.offsetRandom(random, METEOR_POS_SPREAD);
        setMeteorPos(BlockPos.containing(pos).atY(level().getMaxY()));

        level().playSound(null, pos.x(), pos.y(), pos.z(), VPSoundEvents.ENDER_DRAGON_SPAWN_METEOR.get(), getSoundSource(), 5, 1);
        return true;
    }

    @ModifyArg(method = "onCrystalDestroyed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getNearestPlayer(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;DDD)Lnet/minecraft/world/entity/player/Player;"),
            index = 0)
    private TargetingConditions modifyCrystalDestroyTargetConditions(TargetingConditions targetConditions) {
        return getDataModifier().isPresent() ? getDefaultTargetingConditions() : targetConditions;
    }

    @ModifyArg(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"),
            index = 1)
    private double modifyUpwardVelocityMultiplier(double velocity, @Local(name = "currentPhase") DragonPhaseInstance currentPhase) {
        if (getDataModifier().isEmpty())
            return velocity;

        EnderDragonPhase<? extends DragonPhaseInstance> enderDragonPhase = currentPhase.getPhase();

        if (enderDragonPhase == EnderDragonPhase.CHARGING_PLAYER)
            return velocity * 5;
        else if (enderDragonPhase == EnderDragonPhase.LANDING)
            return velocity * 2;

        return velocity;
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "CONSTANT", args = "floatValue=0.06", ordinal = 1))
    private float setMovementVelocityMultiplier(float velocity, @Local(name = "currentPhase") DragonPhaseInstance currentPhase) {
        return getDataModifier()
                .map(enderDragonModifier -> velocity * getMovementSpeedMultiplier(enderDragonModifier, currentPhase))
                .orElse(velocity);
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/enderdragon/phases/DragonPhaseInstance;getTurnSpeed()F"))
    private float setTurnVelocityMultiplier(float velocity, @Local(name = "currentPhase") DragonPhaseInstance currentPhase) {
        return getDataModifier()
                .map(enderDragonModifier -> velocity * getMovementSpeedMultiplier(enderDragonModifier, currentPhase))
                .orElse(velocity);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynchedData(SynchedEntityData.Builder entityData, CallbackInfo ci) {
        entityData.define(METEOR_POS, Optional.empty());
    }

    @Inject(method = "onSyncedDataUpdated", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V"))
    private void onSyncedDataUpdated(EntityDataAccessor<?> accessor, CallbackInfo ci) {
        if (!METEOR_POS.equals(accessor) || !level().isClientSide())
            return;

        BlockPos meteorPos = getMeteorPos();
        if (meteorPos != null)
            stepMeteorClient(meteorPos);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
        output.putInt("AttackCooldown", attackCooldown);
        output.putInt("MeteorAttackCooldown", meteorAttackCooldown);
        output.putDouble("EnderPearlDropRate", enderPearlDropRate);
        output.putInt("EnderPearlDropCount", enderPearlDropCount);
        output.storeNullable("MeteorPos", BlockPos.CODEC, getMeteorPos());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        attackCooldown = input.getIntOr("AttackCooldown", 0);
        meteorAttackCooldown = input.getIntOr("MeteorAttackCooldown", 0);
        enderPearlDropRate = input.getDoubleOr("EnderPearlDropRate", 0);
        enderPearlDropCount = input.getIntOr("EnderPearlDropCount", 0);
        setMeteorPos(input.read("MeteorPos", BlockPos.CODEC).orElse(null));
    }

    @Definition(id = "hurtTime", field = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;hurtTime:I")
    @Expression("this.hurtTime == 0")
    @ModifyExpressionValue(method = "aiStep", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyMeleeAttackCondition(boolean condition) {
        if (getDataModifier().isEmpty())
            return condition;

        EnderDragonPhase<? extends DragonPhaseInstance> enderDragonPhase = phaseManager.getCurrentPhase().getPhase();
        return condition && enderDragonPhase != EnderDragonPhase.SITTING_SCANNING && enderDragonPhase != EnderDragonPhase.SITTING_FLAMING;
    }

    @ModifyExpressionValue(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/boss/enderdragon/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At(value = "CONSTANT", args = "floatValue=0.25"))
    private float modifySittingAllowedDamage(float allowedDamage) {
        return getDataModifier()
                .map(enderDragonModifier -> enderDragonModifier.getPhaseInfo().getSitting().getAllowedDamageRatio())
                .orElse(allowedDamage);
    }

    @Inject(method = "reallyHurt", at = @At("TAIL"))
    private void onHurt(ServerLevel level, DamageSource source, float damage, CallbackInfo ci) {
        getDataModifier().ifPresent(enderDragonModifier -> {
            if (!shouldDropLoot(level) || enderPearlDropCount >= enderDragonModifier.getMaxEnderPearlDrops())
                return;

            enderPearlDropRate += enderDragonModifier.getEnderPearlDropChance();
            if (enderPearlDropRate <= random.nextDouble())
                return;

            enderPearlDropRate = 0;
            enderPearlDropCount++;

            spawnAtLocation(level, Items.ENDER_PEARL);
            spawnEndermites(enderDragonModifier, level);

            level.playSound(null, getX(), getY(), getZ(), VPSoundEvents.ENDER_DRAGON_DROP_PEARL.get(), getSoundSource(), 5,
                    1 + random.nextFloat() * 0.2F);
        });
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/enderdragon/phases/DragonPhaseInstance;doServerTick(Lnet/minecraft/server/level/ServerLevel;)V",
            ordinal = 0))
    private void tickAttack(CallbackInfo ci, @Local(name = "level") ServerLevel level) {
        getDataModifier().ifPresent(enderDragonModifier -> {
            level.players().stream().filter(this::hasLineOfSight).forEach(targets::add);
            targets.removeIf(target -> !level.players().contains(target));

            if (attackCooldown > 0)
                attackCooldown--;
            if (meteorAttackCooldown > 0)
                meteorAttackCooldown--;

            BlockPos meteorPos = getMeteorPos();
            if (meteorPos != null)
                stepMeteorServer(enderDragonModifier, level, meteorPos);
        });
    }

    @ModifyExpressionValue(method = "tickDeath", at = @At(value = "CONSTANT", args = "intValue=12000"))
    private int modifyDropXPFirst(int xp) {
        return getDataModifier().map(enderDragonModifier -> enderDragonModifier.getExperience().getFirst()).orElse(xp);
    }

    @ModifyExpressionValue(method = "tickDeath", at = @At(value = "CONSTANT", args = "intValue=500"))
    private int modifyDropXPSecond(int xp) {
        return getDataModifier().map(enderDragonModifier -> enderDragonModifier.getExperience().getSecond()).orElse(xp);
    }
}
