package com.dace.vanillaplus.mixin.client;

import com.dace.vanillaplus.extension.client.VPOptions;
import lombok.Getter;
import net.minecraft.client.*;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

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
            (_, value) -> value ? KEY_TOGGLE : KEY_HOLD, OptionInstance.BOOLEAN_VALUES, false, _ -> {
    });
    @Unique
    @Getter
    private ToggleKeyMapping keyProne;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void init(Minecraft minecraft, File workingDirectory, CallbackInfo ci) {
        keyProne = new ToggleKeyMapping("key.prone", GLFW.GLFW_KEY_LEFT_ALT, KeyMapping.Category.MOVEMENT, toggleProne::get, true);
        keyProne.setKeyConflictContext(KeyConflictContext.IN_GAME);
    }

    @Inject(method = "processOptions", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/Options;toggleSprint:Lnet/minecraft/client/OptionInstance;", opcode = Opcodes.GETFIELD))
    private void addExtraOptions(Options.FieldAccess access, CallbackInfo ci) {
        access.process("toggleProne", toggleProne);
    }
}
