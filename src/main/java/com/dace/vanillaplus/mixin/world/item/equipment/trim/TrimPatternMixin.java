package com.dace.vanillaplus.mixin.world.item.equipment.trim;

import com.dace.vanillaplus.data.modifier.ArmorTrimEffect;
import com.dace.vanillaplus.extension.world.item.equipment.trim.VPTrimPattern;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(TrimPattern.class)
public abstract class TrimPatternMixin implements VPTrimPattern {
    @Unique
    @Nullable
    @Setter
    private ArmorTrimEffect.TrimPatternEffect dataModifier;

    @Override
    @NonNull
    public Optional<ArmorTrimEffect.TrimPatternEffect> getDataModifier() {
        return Optional.ofNullable(dataModifier);
    }
}
