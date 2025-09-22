package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.custom.CustomModifiableData;
import com.dace.vanillaplus.rebalance.modifier.ItemModifier;
import lombok.NonNull;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Item.class)
public abstract class ItemMixin implements CustomModifiableData<Item, ItemModifier> {
    @Mutable
    @Shadow
    @Final
    private DataComponentMap components;
    @Shadow
    private DataComponentMap builtComponents;

    @Override
    @MustBeInvokedByOverriders
    public void apply(@NonNull ItemModifier modifier) {
        components = DataComponentMap.builder().addAll(components).addAll(modifier.getDataComponentMap()).build();
        builtComponents = components;
    }
}
