package com.dace.vanillaplus.mixin.client.gui.screens.inventory;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreenMixin<CreativeModeInventoryScreen, CreativeModeInventoryScreen.ItemPickerMenu> {
    @Definition(id = "linesToDisplay", local = @Local(type = List.class, name = "linesToDisplay"))
    @Expression("linesToDisplay")
    @Inject(method = "getTooltipFromContainerItem", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 2))
    private void addEmptyLineAfterTabLine(ItemStack itemStack, CallbackInfoReturnable<List<Component>> cir,
                                          @Local(name = "originalLines") List<Component> originalLines,
                                          @Local(name = "linesToDisplay") List<Component> linesToDisplay, @Local(name = "i") int i) {
        if (i > 1 && originalLines.size() > (minecraft.options.advancedItemTooltips ? 3 : 1) && !linesToDisplay.get(i).equals(Component.empty()))
            linesToDisplay.add(i, Component.empty());
    }
}
