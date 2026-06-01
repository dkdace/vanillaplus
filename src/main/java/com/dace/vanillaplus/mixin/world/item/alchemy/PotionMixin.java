package com.dace.vanillaplus.mixin.world.item.alchemy;

import com.dace.vanillaplus.extension.world.item.alchemy.VPPotion;
import com.dace.vanillaplus.world.item.PotionConfig;
import lombok.NonNull;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.List;
import java.util.Objects;

@Mixin(Potion.class)
public abstract class PotionMixin implements VPPotion {
    @Unique
    @Nullable
    private PotionConfig config;
    @Mutable
    @Shadow
    @Final
    private List<MobEffectInstance> effects;

    @Override
    @NonNull
    public PotionConfig getConfig() {
        return Objects.requireNonNull(config, "Not initialized yet");
    }

    public void setConfig(@Nullable PotionConfig config) {
        this.config = config == null ? PotionConfig.DEFAULT : config;
        getConfig().effects().ifPresent(mobEffectInstances -> this.effects = mobEffectInstances);
    }
}
