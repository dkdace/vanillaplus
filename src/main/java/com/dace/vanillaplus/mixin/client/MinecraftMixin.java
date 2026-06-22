package com.dace.vanillaplus.mixin.client;

import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements VPMixin<Minecraft> {
    @Shadow
    @Nullable
    public LocalPlayer player;

    @Definition(id = "rightClickDelay", field = "Lnet/minecraft/client/Minecraft;rightClickDelay:I")
    @Expression("this.rightClickDelay > 0")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyRightClickDelayTickCondition(boolean condition) {
        return condition && (player == null || !player.isUsingItem());
    }
}
