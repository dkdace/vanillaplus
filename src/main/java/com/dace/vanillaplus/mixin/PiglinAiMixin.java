package com.dace.vanillaplus.mixin;

import net.minecraft.world.entity.monster.piglin.PiglinAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PiglinAi.class)
public final class PiglinAiMixin {
    @ModifyArg(method = "initRetreatActivity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/Brain;addActivityAndRemoveMemoryWhenStopped(Lnet/minecraft/world/entity/schedule/Activity;ILcom/google/common/collect/ImmutableList;Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;)V"),
            index = 1)
    private static int getRetreatDistance(int retreatDistance) {
        return 16;
    }
}
