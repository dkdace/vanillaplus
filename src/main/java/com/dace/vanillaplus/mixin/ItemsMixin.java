package com.dace.vanillaplus.mixin;

import com.dace.vanillaplus.rebalance.Rebalance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Items.class)
public abstract class ItemsMixin {
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
