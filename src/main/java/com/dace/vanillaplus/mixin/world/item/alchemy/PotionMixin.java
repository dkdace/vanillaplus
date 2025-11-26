package com.dace.vanillaplus.mixin.world.item.alchemy;

import com.dace.vanillaplus.data.modifier.PotionModifier;
import com.dace.vanillaplus.extension.world.item.alchemy.VPPotion;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.List;
import java.util.Optional;

@Mixin(Potion.class)
public abstract class PotionMixin implements VPPotion {
    @Unique
    @NonNull
    @Getter
    private Optional<Integer> color = Optional.empty();
    @Unique
    @Getter
    @Setter
    private boolean isGlistering;
    @Mutable
    @Shadow
    @Final
    @Setter
    private List<MobEffectInstance> effects;

    @Unique
    private static void applyEffects(@NonNull VPPotion vpPotion, @NonNull Optional<Integer> color, boolean isGlistering,
                                     @NonNull List<MobEffectInstance> mobEffectInstances) {
        color.ifPresent(vpPotion::setColor);
        vpPotion.setGlistering(isGlistering);
        vpPotion.setEffects(mobEffectInstances);
    }

    @Override
    public void setDataModifier(@Nullable PotionModifier dataModifier) {
        if (dataModifier == null)
            return;

        applyEffects(VPPotion.cast(getThis()), dataModifier.getColor(), dataModifier.isGlistering(), dataModifier.getBaseEffects());

        dataModifier.getUpgradeEffectsMap().forEach((name, mobEffectInstances) -> {
            Registry<Potion> potionRegistry = BuiltInRegistries.POTION;
            ResourceLocation resourceLocation = potionRegistry.getKey(getThis());

            if (resourceLocation != null)
                potionRegistry.getOptional(resourceLocation.withPrefix(name + "_")).ifPresent(potion ->
                        applyEffects(VPPotion.cast(potion), dataModifier.getColor(), dataModifier.isGlistering(), mobEffectInstances));
        });
    }

    @Override
    public void setColor(int color) {
        this.color = Optional.of(color);
    }
}
