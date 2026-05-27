package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.dace.vanillaplus.data.registryobject.VPDataComponentTypes;
import com.dace.vanillaplus.extension.world.item.VPItem;
import com.dace.vanillaplus.world.item.ItemConfig;
import com.dace.vanillaplus.world.item.component.RepairWithXP;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(Item.class)
public abstract class ItemMixin<T extends Item> implements VPItem<T> {
    @Shadow
    @Final
    private Holder.Reference<Item> builtInRegistryHolder;
    @Unique
    @Nullable
    private ItemConfig dataModifier;

    @Unique
    private static void applyDataComponentPatch(@NonNull DataComponentMap.Builder.SimpleMap map, @NonNull DataComponentPatch dataComponentPatch) {
        PatchedDataComponentMap patchedDataComponentMap = PatchedDataComponentMap.fromPatch(map, dataComponentPatch);

        map.map().forEach((dataComponentType, _) -> {
            Object newValue = patchedDataComponentMap.remove(dataComponentType);
            if (newValue == null)
                map.map().remove(dataComponentType);
            else
                map.map().put(dataComponentType, newValue);
        });

        patchedDataComponentMap.forEach(typedDataComponent ->
                map.map().put(typedDataComponent.type(), typedDataComponent.value()));
    }

    @Shadow
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity);

    @Override
    @NonNull
    public VPDataComponentMap getConfigComponents() {
        return getDataModifier().map(ItemConfig::components).orElse(VPDataComponentMap.EMPTY);
    }

    @Override
    @NonNull
    public Optional<ItemConfig> getDataModifier() {
        return Optional.ofNullable(dataModifier);
    }

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable ItemConfig dataModifier) {
        this.dataModifier = dataModifier;
        applyConfigItemComponents();
    }

    @Override
    public final void applyConfigItemComponents() {
        DataComponentMap components = builtInRegistryHolder.components();
        if (!(components instanceof DataComponentMap.Builder.SimpleMap map))
            return;

        Integer maxDamage = components.get(DataComponents.MAX_DAMAGE);
        if (maxDamage != null) {
            map.map().put(VPDataComponentTypes.REPAIR_LIMIT.get(), 0);
            map.map().put(VPDataComponentTypes.REPAIR_WITH_XP.get(), RepairWithXP.DEFAULT);
        }

        getDataModifier().ifPresent(itemConfig -> applyDataComponentPatch(map, itemConfig.dataComponentPatch()));
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return !newStack.is(oldStack.getItem());
    }

    @ModifyReturnValue(method = "getUseDuration", at = @At(value = "RETURN", ordinal = 0))
    private int modifyEatingDuration(int duration, @Local(argsOnly = true) LivingEntity user) {
        return (int) (duration * user.getAttributeValue(VPAttributes.EATING_TIME.getHolder().orElseThrow()));
    }
}
