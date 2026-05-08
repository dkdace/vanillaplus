package com.dace.vanillaplus.mixin.world.entity.raid;

import com.dace.vanillaplus.data.ReloadableDataManager;
import com.dace.vanillaplus.data.registryobject.VPGameRules;
import com.dace.vanillaplus.mixin.world.entity.monster.MonsterMixin;
import com.dace.vanillaplus.world.entity.modifier.MobModifier;
import com.dace.vanillaplus.world.entity.raid.RaiderEffect;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(Raider.class)
public abstract class RaiderMixin<T extends Raider, U extends MobModifier> extends MonsterMixin<T, U> {
    @Shadow
    @Nullable
    public abstract Raid getCurrentRaid();

    @Unique
    @NonNull
    protected <V extends RaiderEffect> Optional<V> getRaiderEffect(@NonNull Class<V> checkClass) {
        return ReloadableDataManager.RAIDER_EFFECT.get(getType(), checkClass);
    }

    @Override
    protected float getWaterSlowDown() {
        return getCurrentRaid() == null ? super.getWaterSlowDown() : 0.9F;
    }

    @WrapWithCondition(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;updateBossbar()V"))
    private boolean removeUpdateBossbarCall(Raid instance, @Local(argsOnly = true) ServerLevel level) {
        return VPGameRules.getValue(VPGameRules.RAID_TIME_LIMIT, level) <= 0;
    }
}
