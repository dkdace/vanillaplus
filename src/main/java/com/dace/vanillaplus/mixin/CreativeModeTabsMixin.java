package com.dace.vanillaplus.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CreativeModeTabs.class)
public final class CreativeModeTabsMixin {
    @Overwrite
    private static void generateOminousBottles(CreativeModeTab.Output output, CreativeModeTab.TabVisibility tabVisibility) {
        for (int i = 0; i <= 9; i++) {
            ItemStack itemstack = new ItemStack(Items.OMINOUS_BOTTLE);
            itemstack.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifier(i));
            output.accept(itemstack, tabVisibility);
        }
    }
}
