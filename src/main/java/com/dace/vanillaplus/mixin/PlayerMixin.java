package com.dace.vanillaplus.mixin;

import com.dace.vanillaplus.custom.CustomPlayer;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public final class PlayerMixin implements CustomPlayer {
    @Shadow
    @Final
    private Abilities abilities;
    @Unique
    private boolean vp$isProneKeyDown = false;

    @Override
    public void vp$setProneKeyDown(boolean isProneKeyDown) {
        vp$isProneKeyDown = isProneKeyDown;
    }

    @ModifyReturnValue(method = "getDesiredPose", at = @At(value = "RETURN", ordinal = 4))
    private Pose modifyDesiredPose(Pose pose) {
        Player player = (Player) (Object) this;
        return vp$isProneKeyDown && !abilities.flying && player.onGround() ? Pose.SWIMMING : pose;
    }
}
