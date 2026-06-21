package com.dace.vanillaplus.mixin.world.item.component;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.util.IdentifierUtil;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemAttributeModifiers.class)
public abstract class ItemAttributeModifiersMixin implements VPMixin<ItemAttributeModifiers> {
    @Mixin(ItemAttributeModifiers.Display.Default.class)
    public abstract static class DefaultDisplayMixin implements VPMixin<ItemAttributeModifiers.Display.Default> {
        @Unique
        private static final Identifier BASE_SWEEPING_DAMAGE_RATIO = IdentifierUtil.fromPath("base_sweeping_damage_ratio");
        @Unique
        private static final Identifier BASE_SWEEPING_RANGE = IdentifierUtil.fromPath("base_sweeping_range");

        @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/core/Holder;)Z"))
        private boolean removeKnockbackResistanceMultiplier(Holder<Attribute> attributeHolder, Holder<Attribute> targetHolder) {
            return false;
        }

        @Inject(method = "apply", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;operation()Lnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;",
                ordinal = 0))
        private void applyExtraBaseAttributes(Consumer<Component> consumer, @Nullable Player player, Holder<Attribute> attribute,
                                              AttributeModifier modifier, CallbackInfo ci, @Local(name = "amount") LocalDoubleRef amount,
                                              @Local(name = "displayWithBase") LocalBooleanRef displayWithBase) {
            if (player == null || !modifier.is(BASE_SWEEPING_DAMAGE_RATIO) && !modifier.is(BASE_SWEEPING_RANGE))
                return;

            amount.set(amount.get() + player.getAttributeBaseValue(attribute));
            displayWithBase.set(true);
        }
    }
}
