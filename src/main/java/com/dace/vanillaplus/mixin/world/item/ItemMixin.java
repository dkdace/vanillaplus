package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.modifier.GeneralModifier;
import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.registryobject.VPDataComponentTypes;
import com.dace.vanillaplus.util.ReflectionUtil;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.lang.reflect.Field;

@Mixin(Item.class)
public abstract class ItemMixin<T extends Item, U extends ItemModifier> implements VPModifiableData<Item, U>, VPMixin<T> {
    @Unique
    @Nullable
    protected U dataModifier;
    @Mutable
    @Shadow
    @Final
    private DataComponentMap components;

    @Override
    @MustBeInvokedByOverriders
    @SuppressWarnings("unchecked")
    public void setDataModifier(@Nullable U dataModifier) {
        this.dataModifier = dataModifier;

        try {
            Class<?> simpleMapClass = ReflectionUtil.getClass("net.minecraft.core.component.DataComponentMap$Builder$SimpleMap");
            Field mapField = ReflectionUtil.getField(simpleMapClass, "map");

            Reference2ObjectMap<DataComponentType<?>, Object> map = (Reference2ObjectMap<DataComponentType<?>, Object>) mapField.get(components);

            if (dataModifier != null)
                dataModifier.getDataComponentMap().forEach(typedDataComponent ->
                        map.put(typedDataComponent.type(), typedDataComponent.value()));

            Integer maxDamage = components.get(DataComponents.MAX_DAMAGE);
            if (maxDamage != null) {
                float maxRepairLimitRatio = GeneralModifier.get().getMaxRepairLimitRatio();

                map.put(VPDataComponentTypes.REPAIR_LIMIT.get(), 0);
                map.put(VPDataComponentTypes.MAX_REPAIR_LIMIT.get(), (int) (maxDamage * maxRepairLimitRatio));
            }
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
