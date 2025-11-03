package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.item.RecoveryCompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Function;

@Mixin(Items.class)
public abstract class ItemsMixin implements VPMixin<Items> {
    @Shadow
    @UnknownNullability
    public static Item registerItem(String name, Function<Item.Properties, Item> itemFunction, Item.Properties properties) {
        return null;
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Items;registerItem(Ljava/lang/String;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;",
            ordinal = 0), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=recovery_compass")))
    private static Item registerRecoveryCompassItem(String name, Item.Properties properties) {
        return registerItem(name, RecoveryCompassItem::new, properties);
    }
}
