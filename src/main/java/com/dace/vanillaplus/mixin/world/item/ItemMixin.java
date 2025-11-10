package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.modifier.GeneralModifier;
import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.registryobject.VPDataComponentTypes;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.HashSet;

@Mixin(Item.class)
public abstract class ItemMixin<T extends Item, U extends ItemModifier> implements VPModifiableData<Item, U>, VPMixin<T> {
    @Unique
    @Nullable
    protected U dataModifier;
    @Mutable
    @Shadow
    @Final
    private DataComponentMap components;

    @Unique
    private static <T extends ItemModifier> void applyModifier(@NotNull T dataModifier, DataComponentMap.Builder.SimpleMap map) {
        dataModifier.getDataComponentMap().forEach(typedDataComponent ->
                map.map().put(typedDataComponent.type(), typedDataComponent.value()));

        ItemAttributeModifiers itemAttributeModifiers = map.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (itemAttributeModifiers == null)
            return;

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        HashSet<ItemAttributeModifiers.Entry> newItemAttributeModifierEntries = new HashSet<>(dataModifier.getItemAttributeModifiers().modifiers());

        itemAttributeModifiers.modifiers().forEach(entry -> {
            boolean isAdded = newItemAttributeModifierEntries.removeIf(newEntry -> {
                if (!newEntry.matches(entry.attribute(), entry.modifier().id()))
                    return false;

                builder.add(newEntry.attribute(), newEntry.modifier(), newEntry.slot());
                return true;
            });

            if (!isAdded)
                builder.add(entry.attribute(), entry.modifier(), entry.slot());
        });

        newItemAttributeModifierEntries.forEach(entry -> builder.add(entry.attribute(), entry.modifier(), entry.slot()));

        map.map().put(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
    }

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable U dataModifier) {
        this.dataModifier = dataModifier;

        if (!(components instanceof DataComponentMap.Builder.SimpleMap map))
            return;

        if (dataModifier != null)
            applyModifier(dataModifier, map);

        Integer maxDamage = components.get(DataComponents.MAX_DAMAGE);
        if (maxDamage != null) {
            float maxRepairLimitRatio = GeneralModifier.get().getMaxRepairLimitRatio();

            map.map().put(VPDataComponentTypes.REPAIR_LIMIT.get(), 0);
            map.map().put(VPDataComponentTypes.MAX_REPAIR_LIMIT.get(), (int) (maxDamage * maxRepairLimitRatio));
        }
    }
}
