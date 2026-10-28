package com.dace.vanillaplus.mixin.world.entity;

import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityTypes.class)
public abstract class EntityTypesMixin implements VPMixin<EntityTypes> {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "CONSTANT", args = "intValue=2147483647"))
    private static int modifyMaxUpdateInterval(int updateInterval) {
        return 20;
    }
}
