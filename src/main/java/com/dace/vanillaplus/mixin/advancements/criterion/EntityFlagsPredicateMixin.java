package com.dace.vanillaplus.mixin.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.advancements.criterion.EntityFlagsPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(EntityFlagsPredicate.class)
public abstract class EntityFlagsPredicateMixin {
    @Shadow
    @Final
    public static final Codec<EntityFlagsPredicate> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("is_on_ground").forGetter(EntityFlagsPredicate::isOnGround),
                    Codec.BOOL.optionalFieldOf("is_on_fire").forGetter(EntityFlagsPredicate::isOnFire),
                    Codec.BOOL.optionalFieldOf("is_sneaking").forGetter(EntityFlagsPredicate::isCrouching),
                    Codec.BOOL.optionalFieldOf("is_sprinting").forGetter(EntityFlagsPredicate::isSprinting),
                    Codec.BOOL.optionalFieldOf("is_swimming").forGetter(EntityFlagsPredicate::isSwimming),
                    Codec.BOOL.optionalFieldOf("is_flying").forGetter(EntityFlagsPredicate::isFlying),
                    Codec.BOOL.optionalFieldOf("is_baby").forGetter(EntityFlagsPredicate::isBaby),
                    Codec.BOOL.optionalFieldOf("is_in_water").forGetter(EntityFlagsPredicate::isInWater),
                    Codec.BOOL.optionalFieldOf("is_fall_flying").forGetter(EntityFlagsPredicate::isFallFlying),
                    Codec.BOOL.optionalFieldOf("is_spin_attacking").forGetter(entityFlagsPredicate ->
                            ((EntityFlagsPredicateMixin) (Object) entityFlagsPredicate).isSpinAttacking),
                    Codec.BOOL.optionalFieldOf("is_in_rain").forGetter(entityFlagsPredicate ->
                            ((EntityFlagsPredicateMixin) (Object) entityFlagsPredicate).isInRain))
            .apply(instance, EntityFlagsPredicateMixin::create));

    @Unique
    @Getter
    @Setter
    private Optional<Boolean> isSpinAttacking;
    @Unique
    @Getter
    @Setter
    private Optional<Boolean> isInRain;

    @Unique
    @NonNull
    private static EntityFlagsPredicate create(Optional<Boolean> isOnGround, Optional<Boolean> isOnFire, Optional<Boolean> isCrouching,
                                               Optional<Boolean> isSprinting, Optional<Boolean> isSwimming, Optional<Boolean> isFlying,
                                               Optional<Boolean> isBaby, Optional<Boolean> isInWater, Optional<Boolean> isFallFlying,
                                               Optional<Boolean> isSpinAttacking, Optional<Boolean> isInRain) {
        EntityFlagsPredicate entityFlagsPredicate = new EntityFlagsPredicate(isOnGround, isOnFire, isCrouching, isSprinting, isSwimming, isFlying,
                isBaby, isInWater, isFallFlying);

        EntityFlagsPredicateMixin entityFlagsPredicateMixin = (EntityFlagsPredicateMixin) (Object) entityFlagsPredicate;
        entityFlagsPredicateMixin.isSpinAttacking = isSpinAttacking;
        entityFlagsPredicateMixin.isInRain = isInRain;

        return entityFlagsPredicate;
    }

    @Inject(method = "matches", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z", ordinal = 6), cancellable = true)
    private void checkExtraConditions(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (isSpinAttacking.map(target -> !(entity instanceof LivingEntity livingEntity) || target != livingEntity.isAutoSpinAttack())
                .orElse(false))
            cir.setReturnValue(false);

        if (isInRain.map(target -> target != entity.isInRain()).orElse(false))
            cir.setReturnValue(false);
    }
}
