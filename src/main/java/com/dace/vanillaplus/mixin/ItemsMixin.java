package com.dace.vanillaplus.mixin;

import com.dace.vanillaplus.item.RecoveryCompassItem;
import com.dace.vanillaplus.rebalance.Rebalance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Item$Properties;durability(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 8))
    private static int modifyShieldDurability(int durability) {
        return Rebalance.SHIELD_DURABILITY;
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/component/BlocksAttacks;<init>(FFLjava/util/List;Lnet/minecraft/world/item/component/BlocksAttacks$ItemDamageFunction;Ljava/util/Optional;Ljava/util/Optional;Ljava/util/Optional;)V"),
            index = 0)
    private static float modifyShieldBlockDelaySeconds(float blockDelaySeconds) {
        return 0;
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Items;registerItem(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;",
            ordinal = 84), index = 2)
    private static Item.Properties modifyShieldProperties(Item.Properties properties) {
        return properties.enchantable(1);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Items;registerItem(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;",
            ordinal = 67), index = 2)
    private static Item.Properties modifyShearsProperties(Item.Properties properties) {
        return properties.enchantable(1);
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Items;registerItem(Ljava/lang/String;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;",
            ordinal = 17), index = 1)
    private static Item.Properties modifyElytraProperties(Item.Properties properties) {
        return properties.enchantable(1);
    }
}
