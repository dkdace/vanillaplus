package com.dace.vanillaplus.mixin.world.item.equipment.trim;

import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import com.dace.vanillaplus.extension.world.item.equipment.trim.VPTrimMaterial;
import com.dace.vanillaplus.world.item.effect.ArmorTrimEffect;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(TrimMaterial.class)
public abstract class TrimMaterialMixin implements VPTrimMaterial {
    @Shadow
    @Final
    private Component description;
    @Unique
    @Nullable
    @Setter
    private ArmorTrimEffect.TrimMaterialEffect dataModifier;

    @Override
    @NonNull
    public Optional<ArmorTrimEffect.TrimMaterialEffect> getDataModifier() {
        return Optional.ofNullable(dataModifier);
    }

    @Override
    public void applyTooltip(@NonNull Consumer<Component> componentConsumer) {
        if (dataModifier != null)
            VPEnchantment.cast(dataModifier.getEnchantmentHolder().value()).applyTooltip(componentConsumer, description, 1);
    }
}
