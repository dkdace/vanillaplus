package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.registryobject.VPGameRules;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeTabs.class)
public abstract class CreativeModeTabsMixin implements VPMixin<CreativeModeTabs> {
    @ModifyExpressionValue(method = "generateOminousBottles", at = @At(value = "CONSTANT", args = "intValue=4"))
    private static int modifyOminousBottleCount(int original) {
        return VPGameRules.MAX_POSSIBLE_BAD_OMEN_LEVEL - 1;
    }

    @Inject(method = "lambda$bootstrap$23", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/item/Items;SWEET_BERRIES:Lnet/minecraft/world/item/Item;", opcode = Opcodes.GETSTATIC))
    private static void addGoldenCarrotToFoods(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output,
                                               CallbackInfo ci) {
        output.accept(Items.GLISTERING_MELON_SLICE);
    }

    @Inject(method = "lambda$bootstrap$23", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/item/Items;COOKIE:Lnet/minecraft/world/item/Item;", opcode = Opcodes.GETSTATIC))
    private static void addSugarToFoods(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output, CallbackInfo ci) {
        output.accept(Items.SUGAR);
    }

    @Inject(method = "lambda$bootstrap$23", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/item/Items;CARROT:Lnet/minecraft/world/item/Item;", opcode = Opcodes.GETSTATIC))
    private static void addPoppedChorusFruitToFoods(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output,
                                                    CallbackInfo ci) {
        output.accept(Items.POPPED_CHORUS_FRUIT);
    }
}
