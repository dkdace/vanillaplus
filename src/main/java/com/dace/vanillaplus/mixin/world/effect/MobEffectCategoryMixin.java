package com.dace.vanillaplus.mixin.world.effect;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffectCategory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobEffectCategory.class)
public abstract class MobEffectCategoryMixin implements VPMixin<MobEffectCategory> {
    @Redirect(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/ChatFormatting;BLUE:Lnet/minecraft/ChatFormatting;",
            ordinal = 1, opcode = Opcodes.GETSTATIC))
    private static ChatFormatting modifyNeutralEffectColor() {
        return ChatFormatting.GRAY;
    }
}
