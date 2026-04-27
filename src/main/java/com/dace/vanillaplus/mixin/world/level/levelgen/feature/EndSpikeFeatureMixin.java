package com.dace.vanillaplus.mixin.world.level.levelgen.feature;

import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.levelgen.feature.EndSpikeFeature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EndSpikeFeature.class)
public abstract class EndSpikeFeatureMixin implements VPMixin<SpikeFeature> {
    @Mixin(targets = "net.minecraft.world.level.levelgen.feature.EndSpikeFeature$SpikeCacheLoader")
    public abstract static class SpikeCacheLoaderMixin {
        @ModifyExpressionValue(method = "load(Ljava/lang/Long;)Ljava/util/List;", at = @At(value = "CONSTANT", args = "intValue=76"))
        private int modifyBaseHeight(int height) {
            return 70;
        }

        @ModifyExpressionValue(method = "load(Ljava/lang/Long;)Ljava/util/List;", at = @At(value = "CONSTANT", args = "intValue=3", ordinal = 0))
        private int modifyRadiusDivider(int divider) {
            return 4;
        }

        @ModifyExpressionValue(method = "load(Ljava/lang/Long;)Ljava/util/List;", at = @At(value = "CONSTANT", args = "intValue=3", ordinal = 1))
        private int modifyHeightMultiplier(int multiplier) {
            return 1;
        }

        @Definition(id = "guarded", local = @Local(type = boolean.class, name = "guarded"))
        @Expression("guarded")
        @ModifyExpressionValue(method = "load(Ljava/lang/Long;)Ljava/util/List;", at = @At("MIXINEXTRAS:EXPRESSION"))
        private boolean modifyGuardedCrystalCondition(boolean original, @Local(name = "size") int size) {
            return size == 2 || size == 4 || size == 6 || size == 8;
        }
    }
}
