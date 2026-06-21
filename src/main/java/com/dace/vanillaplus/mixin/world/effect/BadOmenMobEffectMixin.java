package com.dace.vanillaplus.mixin.world.effect;

import com.dace.vanillaplus.data.registryobject.VPGameRules;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.BadOmenMobEffect;
import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BadOmenMobEffect.class)
public abstract class BadOmenMobEffectMixin extends MobEffectMixin<BadOmenMobEffect> {
    @Redirect(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;getMaxRaidOmenLevel()I"))
    private int modifyMaxBadOmenLevel(Raid raid, @Local(argsOnly = true) ServerLevel level) {
        return VPGameRules.getValue(VPGameRules.MAX_BAD_OMEN_LEVEL, level);
    }
}
