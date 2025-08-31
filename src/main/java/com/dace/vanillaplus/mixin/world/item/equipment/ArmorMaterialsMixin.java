package com.dace.vanillaplus.mixin.world.item.equipment;

import com.dace.vanillaplus.rebalance.Rebalance;
import net.minecraft.world.item.equipment.ArmorMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ArmorMaterials.class)
public interface ArmorMaterialsMixin {
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/equipment/ArmorMaterial;<init>(ILjava/util/Map;ILnet/minecraft/core/Holder;FFLnet/minecraft/tags/TagKey;Lnet/minecraft/resources/ResourceKey;)V",
            ordinal = 1), index = 2)
    private static int modifyChainmailEnchantability(int enchantability) {
        return Rebalance.Chainmail.ENCHANTABILITY;
    }

    @ModifyArgs(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/equipment/ArmorMaterials;makeDefense(IIIII)Ljava/util/Map;", ordinal = 1))
    private static void modifyChainmailDefense(Args args) {
        for (int i = 0; i < 4; i++)
            args.set(i, Rebalance.Chainmail.DEFENSE.get(i));
    }
}
