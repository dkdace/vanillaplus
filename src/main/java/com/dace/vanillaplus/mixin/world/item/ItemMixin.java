package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.VPDataComponentTypes;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.rebalance.modifier.ItemModifier;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(Item.class)
public abstract class ItemMixin<T extends ItemModifier> implements VPModifiableData<Item, T> {
    @Unique
    @Nullable
    @Getter
    protected T dataModifier;
    @Mutable
    @Shadow
    @Final
    private DataComponentMap components;
    @Shadow
    private DataComponentMap builtComponents;

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@NonNull T dataModifier) {
        this.dataModifier = dataModifier;

        DataComponentMap.Builder builder = DataComponentMap.builder().addAll(components).addAll(dataModifier.getDataComponentMap());

        Integer maxDamage = components.get(DataComponents.MAX_DAMAGE);
        if (maxDamage != null) {
            builder.set(VPDataComponentTypes.REPAIR_LIMIT.get(), 0)
                    .set(VPDataComponentTypes.MAX_REPAIR_LIMIT.get(), (int) (maxDamage * 0.4));
        }

        components = builder.build();
        builtComponents = components;
    }
}
