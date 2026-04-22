package com.dace.vanillaplus.mixin.world.item.alchemy;

import com.dace.vanillaplus.extension.world.item.alchemy.VPPotion;
import com.dace.vanillaplus.world.item.PotionModifier;
import lombok.NonNull;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.List;
import java.util.Optional;

@Mixin(Potion.class)
public abstract class PotionMixin implements VPPotion {
    @Unique
    @Nullable
    private PotionModifier dataModifier;
    @Mutable
    @Shadow
    @Final
    private List<MobEffectInstance> effects;

    @Override
    @NonNull
    public Optional<PotionModifier> getDataModifier() {
        return Optional.ofNullable(dataModifier);
    }

    public void setDataModifier(@Nullable PotionModifier dataModifier) {
        this.dataModifier = dataModifier;

        if (dataModifier != null)
            effects = dataModifier.getEffects();
    }
}
