package com.dace.vanillaplus.mixin;

import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Items.class)
public final class ItemsMixin {
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Item$Properties;durability(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 8))
    private static int getDurability(int durability) {
        return 260;
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/component/BlocksAttacks;<init>(FFLjava/util/List;Lnet/minecraft/world/item/component/BlocksAttacks$ItemDamageFunction;Ljava/util/Optional;Ljava/util/Optional;Ljava/util/Optional;)V"),
            index = 0)
    private static float getBlockDelaySeconds(float blockDelaySeconds) {
        return 0;
    }
}
