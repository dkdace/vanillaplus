package com.dace.vanillaplus.mixin.world.item.component;

import com.dace.vanillaplus.rebalance.Rebalance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.component.DeathProtection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(DeathProtection.class)
public abstract class DeathProtectionMixin {
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/consume_effects/ApplyStatusEffectsConsumeEffect;<init>(Ljava/util/List;)V"))
    private static List<MobEffectInstance> modifyTotemMobEffects(List<MobEffectInstance> par1) {
        return Rebalance.Totem.MOB_EFFECTS;
    }
}
