package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.world.item.RecoveryCompassItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Function;

@Mixin(Items.class)
public abstract class ItemsMixin implements VPMixin<Items> {
    @Shadow
    private static Item registerItem(ResourceKey<Item> id, Function<Item.Properties, Item> itemFactory, Item.Properties properties) {
        throw new UnsupportedOperationException();
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;",
            ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Items;RECOVERY_COMPASS:Lnet/minecraft/world/item/Item;",
            opcode = Opcodes.PUTSTATIC)))
    private static Item registerRecoveryCompassItem(ResourceKey<Item> id, Item.Properties properties) {
        return registerItem(id, RecoveryCompassItem::new, properties);
    }
}
