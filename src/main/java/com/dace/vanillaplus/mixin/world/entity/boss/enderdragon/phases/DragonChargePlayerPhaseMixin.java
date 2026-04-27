package com.dace.vanillaplus.mixin.world.entity.boss.enderdragon.phases;

import com.dace.vanillaplus.world.entity.EntityModifier;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonChargePlayerPhase;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonChargePlayerPhase.class)
public abstract class DragonChargePlayerPhaseMixin extends AbstractDragonPhaseInstanceMixin {
    @Unique
    private static final int ROAR_DURATION = 40;
    @Unique
    private static final int BREATH_START_DISTANCE = 10;
    @Unique
    private static final int BREATH_FLAME_LANDING_THRESHOLD = 8;

    @Unique
    private int flameTicks;
    @Unique
    private boolean isFlaming;

    @Unique
    private void playParticles() {
        Vec3 vec = dragon.getHeadLookVector(1).normalize().yRot((float) (-Math.PI / 4));

        for (int i = 0; i < 20; i++) {
            double x = dragon.head.getX() + dragon.getRandom().nextGaussian() / 4;
            double y = dragon.head.getY(1) + dragon.getRandom().nextGaussian() / 4;
            double z = dragon.head.getZ() + dragon.getRandom().nextGaussian() / 4;
            Vec3 speed = dragon.getDeltaMovement();

            dragon.level().addParticle(PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1), x, y, z,
                    -vec.x() * (0.1 + dragon.getRandom().nextDouble() * 0.2) + speed.x(),
                    -vec.y() * (0.3 + dragon.getRandom().nextDouble() * 0.2) + speed.y(),
                    -vec.z() * (0.1 + dragon.getRandom().nextDouble() * 0.2) + speed.z());
        }
    }

    @Unique
    private void createFlame(@NonNull EntityModifier.EnderDragonModifier.PhaseInfo.Charge charge, ServerLevel serverLevel, Vec3 pos, float radius) {
        double x = dragon.head.getX() + pos.x() * radius / 2;
        double y = dragon.head.getY();
        double z = dragon.head.getZ() + pos.z() * radius / 2;
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(x, y, z);

        if (serverLevel.isEmptyBlock(blockPos)) {
            for (int i = 0; serverLevel.isEmptyBlock(blockPos.move(Direction.DOWN)); i++)
                if (i >= BREATH_FLAME_LANDING_THRESHOLD)
                    return;

            blockPos.move(Direction.UP);
        } else {
            for (int i = 0; !serverLevel.isEmptyBlock(blockPos.move(Direction.UP)); i++)
                if (i >= BREATH_FLAME_LANDING_THRESHOLD)
                    return;
        }

        pos = blockPos.getBottomCenter();

        AreaEffectCloud flame = new AreaEffectCloud(serverLevel, pos.x(), pos.y(), pos.z());
        flame.setOwner(dragon);
        flame.setRadius(radius);
        flame.setDuration(charge.getFlameDuration());
        flame.setCustomParticle(PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1));
        flame.setPotionDurationScale(0.25F);
        flame.addEffect(getVPEnderDragon().getFlameMobEffectInstance());

        serverLevel.addFreshEntity(flame);
    }

    @Override
    protected void onClientTick(CallbackInfo ci) {
        if (getVPEnderDragon().getDataModifier().isEmpty())
            return;

        if (flameTicks++ < ROAR_DURATION)
            dragon.level().playLocalSound(dragon.getX(), dragon.getY(), dragon.getZ(), SoundEvents.ENDER_DRAGON_GROWL, dragon.getSoundSource(),
                    2.5F, 0.8F + dragon.getRandom().nextFloat() * 0.3F, false);

        playParticles();
    }

    @Override
    public float getTurnSpeed() {
        if (getVPEnderDragon().getDataModifier().isEmpty())
            return super.getTurnSpeed();

        float distance = (float) (dragon.getDeltaMovement().horizontalDistance() + 1);
        return Math.min(distance, 40) / distance;
    }

    @Definition(id = "distToTarget", local = @Local(type = double.class, name = "distToTarget"))
    @Expression("distToTarget < ?")
    @Inject(method = "doServerTick", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private void performBreathAttack(ServerLevel level, CallbackInfo ci, @Local(name = "distToTarget") double distToTarget) {
        getVPEnderDragon().getDataModifier().ifPresent(enderDragonModifier -> {
            if (distToTarget < BREATH_START_DISTANCE * BREATH_START_DISTANCE)
                isFlaming = true;

            if (!isFlaming || flameTicks++ % 4 != 0)
                return;

            Vec3 pos = new Vec3(dragon.head.getX() - dragon.getX(), 0, dragon.head.getZ() - dragon.getZ()).normalize();
            float radius = enderDragonModifier.getPhaseInfo().getCharge().getFlameRadius();

            createFlame(enderDragonModifier.getPhaseInfo().getCharge(), level, pos, radius);
        });
    }

    @Inject(method = "begin", at = @At("TAIL"))
    private void resetFlameTicks(CallbackInfo ci) {
        flameTicks = 0;
        isFlaming = false;
    }
}
