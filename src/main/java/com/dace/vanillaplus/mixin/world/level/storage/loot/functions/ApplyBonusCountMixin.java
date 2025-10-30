package com.dace.vanillaplus.mixin.world.level.storage.loot.functions;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ApplyBonusCount.class)
public abstract class ApplyBonusCountMixin implements VPMixin<ApplyBonusCount> {
    @Mixin(targets = "net.minecraft.world.level.storage.loot.functions.ApplyBonusCount$OreDrops")
    public abstract static class OreDropsMixin {
        @Overwrite
        public int calculateNewCount(RandomSource randomSource, int count, int level) {
            if (level <= 0)
                return count;

            double chance = level * 0.3;
            return chance > randomSource.nextFloat() ? count + 1 : count;
        }
    }
}
