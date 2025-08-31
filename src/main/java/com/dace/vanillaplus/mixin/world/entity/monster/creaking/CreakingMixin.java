package com.dace.vanillaplus.mixin.world.entity.monster.creaking;

import com.dace.vanillaplus.rebalance.Rebalance;
import net.minecraft.world.entity.monster.creaking.Creaking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Creaking.class)
public abstract class CreakingMixin {
    @ModifyArg(method = "createAttributes", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;",
            ordinal = 1), index = 1)
    private static double modifyMovementSpeed(double movementSpeed) {
        return Rebalance.Creaking.MOVEMENT_SPEED;
    }

    @ModifyArg(method = "createAttributes", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;",
            ordinal = 2), index = 1)
    private static double modifyAttackDamage(double attackDamage) {
        return Rebalance.Creaking.ATTACK_DAMAGE;
    }
}
