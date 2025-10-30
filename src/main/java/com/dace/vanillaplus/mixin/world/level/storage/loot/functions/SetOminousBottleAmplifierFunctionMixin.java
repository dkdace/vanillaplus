package com.dace.vanillaplus.mixin.world.level.storage.loot.functions;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.world.level.storage.loot.functions.SetOminousBottleAmplifierFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SetOminousBottleAmplifierFunction.class)
public abstract class SetOminousBottleAmplifierFunctionMixin implements VPMixin<SetOminousBottleAmplifierFunction> {
    @ModifyArg(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(III)I"), index = 2)
    private int modifyMaxAmplifier(int max) {
        return Byte.MAX_VALUE;
    }
}
