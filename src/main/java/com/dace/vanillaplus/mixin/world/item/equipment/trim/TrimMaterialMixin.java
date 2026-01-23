package com.dace.vanillaplus.mixin.world.item.equipment.trim;

import com.dace.vanillaplus.data.modifier.ArmorTrimEffect;
import com.dace.vanillaplus.extension.world.item.equipment.trim.VPTrimMaterial;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(TrimMaterial.class)
public abstract class TrimMaterialMixin implements VPTrimMaterial {
    @Unique
    @Nullable
    @Setter
    private ArmorTrimEffect.TrimMaterialEffect dataModifier;

    @Override
    @NonNull
    public Optional<ArmorTrimEffect.TrimMaterialEffect> getDataModifier() {
        return Optional.ofNullable(dataModifier);
    }
}
