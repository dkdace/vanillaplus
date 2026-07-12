package com.dace.vanillaplus.mixin.client;

import com.dace.vanillaplus.extension.client.VPOptions;
import com.mojang.serialization.Codec;
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

@Mixin(Options.class)
public abstract class OptionsMixin implements VPOptions {
    @Unique
    private static final Component COMPONENT_ATTACK_MARKER_TOOLTIP = Component.translatable("options.attack_marker.tooltip");
    @Unique
    private static final Component COMPONENT_MOB_HEALTH_INDICATOR_TOOLTIP = Component.translatable("options.mob_health_indicator.tooltip");
    @Unique
    private static final double MAX_ITEM_TOOLTIP_WIDTH_MIN = 0.1;
    @Unique
    private static final double MAX_ITEM_TOOLTIP_WIDTH_MAX = 1;
    @Unique
    private static final double MAX_ITEM_TOOLTIP_WIDTH_DEFAULT = 0.3;
    @Shadow
    @Final
    private static Component KEY_TOGGLE;
    @Shadow
    @Final
    private static Component KEY_HOLD;

    @Unique
    @Getter
    private OptionInstance<Boolean> toggleProne;
    @Unique
    @Getter
    private OptionInstance<Double> maxItemTooltipWidth;
    @Unique
    @Getter
    private OptionInstance<Boolean> attackMarker;
    @Unique
    @Getter
    private OptionInstance<Boolean> mobHealthIndicator;
    @Unique
    @Getter
    private ToggleKeyMapping keyProne;

    @Shadow
    private static Component pixelValueLabel(Component caption, int value) {
        throw new UnsupportedOperationException();
    }

    @Inject(method = "setForgeKeybindProperties", at = @At(value = "TAIL"))
    private void init(CallbackInfo ci) {
        toggleProne = new OptionInstance<>("key.prone", OptionInstance.noTooltip(),
                (_, value) -> value ? KEY_TOGGLE : KEY_HOLD, OptionInstance.BOOLEAN_VALUES, false, _ -> {
        });
        maxItemTooltipWidth = new OptionInstance<>("options.item_tooltip_width", OptionInstance.noTooltip(),
                (caption, value) -> pixelValueLabel(caption, (int) (value * Minecraft.getInstance().getWindow().getGuiScaledWidth())),
                new OptionInstance.IntRange((int) (MAX_ITEM_TOOLTIP_WIDTH_MIN * 100), (int) (MAX_ITEM_TOOLTIP_WIDTH_MAX * 100))
                        .xmap(to -> to / 100.0, from -> (int) (from * 100), true),
                Codec.doubleRange(MAX_ITEM_TOOLTIP_WIDTH_MIN, MAX_ITEM_TOOLTIP_WIDTH_MAX), MAX_ITEM_TOOLTIP_WIDTH_DEFAULT, _ -> {
        });
        attackMarker = OptionInstance.createBoolean("options.attack_marker",
                OptionInstance.cachedConstantTooltip(COMPONENT_ATTACK_MARKER_TOOLTIP), true);
        mobHealthIndicator = OptionInstance.createBoolean("options.mob_health_indicator",
                OptionInstance.cachedConstantTooltip(COMPONENT_MOB_HEALTH_INDICATOR_TOOLTIP), true);

        keyProne = new ToggleKeyMapping("key.prone", GLFW.GLFW_KEY_LEFT_ALT, KeyMapping.Category.MOVEMENT, toggleProne::get, true);
        keyProne.setKeyConflictContext(KeyConflictContext.IN_GAME);
    }

    @Inject(method = "processOptions", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/Options;toggleSprint:Lnet/minecraft/client/OptionInstance;", opcode = Opcodes.GETFIELD))
    private void addExtraOptions0(Options.FieldAccess access, CallbackInfo ci) {
        access.process("toggleProne", toggleProne);
    }

    @Inject(method = "processOptions", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/Options;tutorialStep:Lnet/minecraft/client/tutorial/TutorialSteps;", opcode = Opcodes.PUTFIELD))
    private void addExtraOptions1(Options.FieldAccess access, CallbackInfo ci) {
        access.process("attackMarker", attackMarker);
        access.process("mobHealthIndicator", mobHealthIndicator);
    }
}
