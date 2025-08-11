package com.dace.vanillaplus.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(OminousBottleAmplifier.class)
public final class OminousBottleAmplifierMixin {
    @Shadow
    @Final
    public static final int MAX_AMPLIFIER = 9;
    @Shadow
    @Final
    public static final Codec<OminousBottleAmplifier> CODEC =
            ExtraCodecs.intRange(0, MAX_AMPLIFIER).xmap(OminousBottleAmplifier::new, OminousBottleAmplifier::value);
}
