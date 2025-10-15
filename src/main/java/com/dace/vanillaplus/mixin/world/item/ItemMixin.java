package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.VPRegistries;
import com.dace.vanillaplus.data.modifier.GeneralModifier;
import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.registryobject.VPDataComponentTypes;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(Item.class)
public abstract class ItemMixin<T extends Item, U extends ItemModifier> implements VPModifiableData<Item, U>, VPMixin<T> {
    @Unique
    @Nullable
    protected U dataModifier;
    @Mutable
    @Shadow
    @Final
    private DataComponentMap components;
    @Shadow
    private DataComponentMap builtComponents;

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable U dataModifier) {
        this.dataModifier = dataModifier;

        DataComponentMap.Builder builder = DataComponentMap.builder().addAll(components);
        if (dataModifier != null)
            builder.addAll(dataModifier.getDataComponentMap());

        Integer maxDamage = components.get(DataComponents.MAX_DAMAGE);
        if (maxDamage != null) {
            float maxRepairLimitRatio = VPRegistries.getValueOrThrow(GeneralModifier.RESOURCE_KEY).getMaxRepairLimitRatio();

            builder.set(VPDataComponentTypes.REPAIR_LIMIT.get(), 0)
                    .set(VPDataComponentTypes.MAX_REPAIR_LIMIT.get(), (int) (maxDamage * maxRepairLimitRatio));
        }

        components = builder.build();
        builtComponents = components;
    }
}
