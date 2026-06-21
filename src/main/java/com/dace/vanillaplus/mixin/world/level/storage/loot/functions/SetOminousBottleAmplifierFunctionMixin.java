package com.dace.vanillaplus.mixin.world.level.storage.loot.functions;

import com.dace.vanillaplus.data.registryobject.VPGameRules;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.SetOminousBottleAmplifierFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SetOminousBottleAmplifierFunction.class)
public abstract class SetOminousBottleAmplifierFunctionMixin extends LootItemConditionalFunctionMixin<SetOminousBottleAmplifierFunction> {
    @ModifyArg(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(III)I"), index = 2)
    private int modifyMaxAmplifier(int max, @Local(argsOnly = true) LootContext context) {
        return VPGameRules.getValue(VPGameRules.MAX_BAD_OMEN_LEVEL, context.getLevel()) - 1;
    }
}
