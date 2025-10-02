package com.dace.vanillaplus.mixin.world.item;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiConsumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Redirect(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;forEachModifier(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V"))
    private void removeEnchantmentAttributeDisplay(ItemStack itemStack, EquipmentSlotGroup equipmentSlotGroup,
                                                   BiConsumer<Holder<Attribute>, AttributeModifier> onApply) {
        // 미사용
    }
}
