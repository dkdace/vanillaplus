package com.dace.vanillaplus.mixin.world.entity.ai.sensing;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.world.entity.npc.NpcConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(VillagerHostilesSensor.class)
public abstract class VillagerHostilesSensorMixin implements VPMixin<VillagerHostilesSensor> {
    @Inject(method = "isMatchingEntity", at = @At("RETURN"), cancellable = true)
    private void checkAvoidEntities(ServerLevel level, LivingEntity body, LivingEntity mob, CallbackInfoReturnable<Boolean> cir) {
        if (!(body instanceof AbstractVillager abstractVillager))
            return;

        Map<EntityType<?>, Integer> avoidEntityDistanceMap = NpcConfig.get(abstractVillager).avoidEntityDistanceMap();
        if (avoidEntityDistanceMap.isEmpty())
            return;

        Integer distance = avoidEntityDistanceMap.get(mob.getType());
        cir.setReturnValue(distance != null && mob.distanceToSqr(body) <= distance * distance);
    }
}
