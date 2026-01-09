package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.dace.vanillaplus.extension.world.item.component.VPTooltipProvider;
import com.dace.vanillaplus.registryobject.VPDataComponentTypes;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(SmithingTemplateItem.class)
public abstract class SmithingTemplateItemMixin extends ItemMixin<SmithingTemplateItem, ItemModifier> {
    @Inject(method = "appendHoverText", at = @At(value = "FIELD",
            target = "Lnet/minecraft/network/chat/CommonComponents;EMPTY:Lnet/minecraft/network/chat/Component;", opcode = Opcodes.GETSTATIC))
    private void addEffectTooltip(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay,
                                  Consumer<Component> componentConsumer, TooltipFlag tooltipFlag, CallbackInfo ci) {
        Holder<TrimPattern> trimPatternHolder = itemStack.get(VPDataComponentTypes.PROVIDES_TRIM_PATTERN.get());
        if (trimPatternHolder != null)
            VPTooltipProvider.applyTrimPatternEffectsTooltip(componentConsumer, trimPatternHolder);
    }
}
