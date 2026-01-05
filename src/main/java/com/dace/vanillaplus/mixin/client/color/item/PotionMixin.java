package com.dace.vanillaplus.mixin.client.color.item;

import com.dace.vanillaplus.ClientForgeEventManager;
import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.client.color.item.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Potion.class)
public abstract class PotionMixin implements VPMixin<Potion> {
    @ModifyArg(method = "calculate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ARGB;opaque(I)I", ordinal = 0))
    private int modifyColor(int color) {
        return ClientForgeEventManager.getMixedColor(PotionContents.BASE_POTION_COLOR, color);
    }
}
