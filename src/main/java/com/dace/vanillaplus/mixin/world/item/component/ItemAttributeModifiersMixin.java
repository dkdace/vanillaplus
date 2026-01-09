package com.dace.vanillaplus.mixin.world.item.component;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemAttributeModifiers.class)
public abstract class ItemAttributeModifiersMixin implements VPMixin<ItemAttributeModifiers> {
    @Mixin(ItemAttributeModifiers.Display.Default.class)
    public abstract static class DefaultDisplayMixin {
        @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/core/Holder;)Z"))
        private boolean removeKnockbackResistanceMultiplier(Holder<Attribute> attributeHolder, Holder<Attribute> targetHolder) {
            return false;
        }
    }
}
