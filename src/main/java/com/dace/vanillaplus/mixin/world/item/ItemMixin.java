package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.dace.vanillaplus.extension.world.item.VPItem;
import com.dace.vanillaplus.registryobject.VPAttributes;
import com.dace.vanillaplus.registryobject.VPDataComponentTypes;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;
import java.util.Optional;

@Mixin(Item.class)
public abstract class ItemMixin<T extends Item, U extends ItemModifier> implements VPItem<T, U> {
    @Unique
    @Nullable
    private U dataModifier;
    @Mutable
    @Shadow
    @Final
    private DataComponentMap components;

    @Unique
    private static <T extends ItemModifier> void applyModifier(@NonNull T dataModifier, @NonNull DataComponentMap.Builder.SimpleMap map) {
        PatchedDataComponentMap patchedDataComponentMap = PatchedDataComponentMap.fromPatch(map, dataModifier.getDataComponentPatch());

        map.map().forEach((dataComponentType, value) -> {
            Object newValue = patchedDataComponentMap.remove(dataComponentType);
            if (newValue == null)
                map.map().remove(dataComponentType);
            else
                map.map().put(dataComponentType, newValue);
        });

        patchedDataComponentMap.forEach(typedDataComponent ->
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

    @Shadow
    public abstract InteractionResult use(Level level, Player player, InteractionHand interactionHand);

    @Shadow
    public abstract ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity);

    @Override
    @NonNull
    public Optional<U> getDataModifier() {
        return Optional.ofNullable(dataModifier);
    }

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable U dataModifier) {
        this.dataModifier = dataModifier;

        if (!(components instanceof DataComponentMap.Builder.SimpleMap map))
            return;

        Integer maxDamage = components.get(DataComponents.MAX_DAMAGE);
        if (maxDamage != null) {
            map.map().put(VPDataComponentTypes.REPAIR_LIMIT.get(), 0);
            map.map().put(VPDataComponentTypes.REPAIR_WITH_XP.get(), VPDataComponentTypes.RepairWithXP.DEFAULT);
        }

        if (dataModifier != null)
            applyModifier(dataModifier, map);
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return !newStack.is(oldStack.getItem());
    }

    @ModifyReturnValue(method = "getUseDuration", at = @At(value = "RETURN", ordinal = 0))
    private int modifyEatingDuration(int duration, @Local(argsOnly = true) LivingEntity livingEntity) {
        return (int) (duration * livingEntity.getAttributeValue(VPAttributes.EATING_TIME.getHolder().orElseThrow()));
    }
}
