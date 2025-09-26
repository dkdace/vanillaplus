package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.item.RecoveryCompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(Items.class)
public abstract class ItemsMixin {
    @Shadow
    public static Item registerItem(String name, Function<Item.Properties, Item> itemFunction, Item.Properties properties) {
        return null;
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Items;registerItem(Ljava/lang/String;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;",
            ordinal = 75))
    private static Item registerRecoveryCompassItem(String name, Item.Properties properties) {
        return registerItem(name, RecoveryCompassItem::new, properties);
    }
}
