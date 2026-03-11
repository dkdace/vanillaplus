package com.dace.vanillaplus.mixin.world.item.equipment.trim;

import com.dace.vanillaplus.data.ArmorTrimEffect;
import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import com.dace.vanillaplus.extension.world.item.equipment.trim.VPTrimPattern;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(TrimPattern.class)
public abstract class TrimPatternMixin implements VPTrimPattern {
    @Shadow
    @Final
    private Component description;
    @Unique
    @Nullable
    @Setter
    private ArmorTrimEffect.TrimPatternEffect dataModifier;

    @Override
    @NonNull
    public Optional<ArmorTrimEffect.TrimPatternEffect> getDataModifier() {
        return Optional.ofNullable(dataModifier);
    }

    @Override
    public void applyTooltip(@NonNull Consumer<Component> componentConsumer) {
        if (dataModifier != null)
            VPEnchantment.cast(dataModifier.getEnchantmentHolder().value()).applyTooltip(componentConsumer, description, 1);
    }
}
