package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.extension.VPItemStack;
import com.dace.vanillaplus.registryobject.VPDataComponentTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements VPItemStack {
    @Shadow
    public abstract <T extends TooltipProvider> void addToTooltip(DataComponentType<T> dataComponentType, Item.TooltipContext tooltipContext,
                                                                  TooltipDisplay tooltipDisplay, Consumer<Component> componentConsumer,
                                                                  TooltipFlag tooltipFlag);

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    @Nullable
    public abstract <T> T set(DataComponentType<T> dataComponentType, @Nullable T value);

    @Redirect(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;forEachModifier(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V"))
    private void removeEnchantmentAttributeDisplay(ItemStack itemStack, EquipmentSlotGroup equipmentSlotGroup,
                                                   BiConsumer<Holder<Attribute>, AttributeModifier> onApply) {
        // 미사용
    }

    @Inject(method = "addDetailsToTooltip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;addAttributeTooltips(Ljava/util/function/Consumer;Lnet/minecraft/world/item/component/TooltipDisplay;Lnet/minecraft/world/entity/player/Player;)V"))
    @SuppressWarnings("unchecked")
    private void addTooltips(Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, @Nullable Player player, TooltipFlag tooltipFlag,
                             Consumer<Component> componentConsumer, CallbackInfo ci) {
        addToTooltip(((DataComponentType<TooltipProvider>) (Object) DataComponents.FOOD), tooltipContext, tooltipDisplay, componentConsumer,
                tooltipFlag);
    }

    @Inject(method = "addDetailsToTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
            ordinal = 6))
    private void addRepairLimitDataComponent(Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, @Nullable Player player,
                                             TooltipFlag tooltipFlag, Consumer<Component> componentConsumer, CallbackInfo ci) {
        if (EnchantmentHelper.has(((ItemStack) (Object) this), EnchantmentEffectComponents.REPAIR_WITH_XP)
                && tooltipDisplay.shows(VPDataComponentTypes.REPAIR_LIMIT.get()))
            componentConsumer.accept(Component.translatable("item.repairLimit",
                    getMaxRepairLimit() - getRepairLimit(),
                    getMaxRepairLimit()));
    }

    @Override
    public int getRepairLimit() {
        return Math.clamp(((ItemStack) (Object) this).getOrDefault(VPDataComponentTypes.REPAIR_LIMIT.get(), 0), 0, getMaxRepairLimit());
    }

    @Override
    public void setRepairLimit(int repairLimit) {
        set(VPDataComponentTypes.REPAIR_LIMIT.get(), Math.clamp(repairLimit, 0, getMaxRepairLimit()));
    }

    @Override
    public int getMaxRepairLimit() {
        return ((ItemStack) (Object) this).getOrDefault(VPDataComponentTypes.MAX_REPAIR_LIMIT.get(), 0);
    }
}
