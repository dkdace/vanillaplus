package com.dace.vanillaplus.mixin;

import net.minecraft.world.entity.monster.creaking.Creaking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Creaking.class)
public final class CreakingMixin {
    @ModifyArg(method = "createAttributes", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;",
            ordinal = 1), index = 1)
    private static double getMovementSpeed(double movementSpeed) {
        return 0.5;
    }

    @ModifyArg(method = "createAttributes", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;",
            ordinal = 2), index = 1)
    private static double getAttackDamage(double attackDamage) {
        return 6;
    }
}
