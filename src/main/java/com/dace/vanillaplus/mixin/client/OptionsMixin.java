package com.dace.vanillaplus.mixin.client;

import com.dace.vanillaplus.extension.client.VPOptions;
import lombok.Getter;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin implements VPOptions {
    @Shadow
    @Final
    private static Component KEY_TOGGLE;
    @Shadow
    @Final
    private static Component KEY_HOLD;

    @Unique
    @Getter
    private final OptionInstance<Boolean> toggleProne = new OptionInstance<>("key.prone", OptionInstance.noTooltip(),
            (caption, value) -> value ? KEY_TOGGLE : KEY_HOLD, OptionInstance.BOOLEAN_VALUES, false, value -> {
    });

    @Inject(method = "processOptions", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/Options;toggleSprint:Lnet/minecraft/client/OptionInstance;", opcode = Opcodes.GETFIELD))
    private void addExtraOptions(Options.FieldAccess fieldAccess, CallbackInfo ci) {
        fieldAccess.process("toggleProne", toggleProne);
    }
}
