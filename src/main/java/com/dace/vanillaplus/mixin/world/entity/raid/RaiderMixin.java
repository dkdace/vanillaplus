package com.dace.vanillaplus.mixin.world.entity.raid;

import com.dace.vanillaplus.data.RaiderEffect;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.monster.MonsterMixin;
import lombok.NonNull;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(Raider.class)
public abstract class RaiderMixin<T extends Raider, U extends EntityModifier.LivingEntityModifier> extends MonsterMixin<T, U> {
    @Shadow
    @Nullable
    public abstract Raid getCurrentRaid();

    @Unique
    @NonNull
    protected <V extends RaiderEffect> Optional<V> getRaiderEffect(@NonNull Class<V> checkClass) {
        return RaiderEffect.DATA_GETTER.get(getType(), checkClass);
    }

    @Override
    protected float getWaterSlowDown() {
        return getCurrentRaid() == null ? super.getWaterSlowDown() : 0.9F;
    }
}
