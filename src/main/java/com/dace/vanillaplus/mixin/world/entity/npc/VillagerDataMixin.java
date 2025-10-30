package com.dace.vanillaplus.mixin.world.entity.npc;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.world.entity.npc.VillagerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VillagerData.class)
public abstract class VillagerDataMixin implements VPMixin<VillagerData> {
    @Shadow
    @Final
    private static final int[] NEXT_LEVEL_XP_THRESHOLDS = {0, 100, 300, 600, 1000};
}
