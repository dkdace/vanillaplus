package com.dace.vanillaplus.mixin.world.entity.monster.zombie;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.monster.MonsterMixin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieVillager.class)
public abstract class ZombieVillagerMixin extends MonsterMixin<ZombieVillager, EntityModifier.LivingEntityModifier> {
    @Inject(method = "lambda$finishConversion$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/villager/Villager;refreshBrain(Lnet/minecraft/server/level/ServerLevel;)V"))
    private void dismountOnConversion(ServerLevel serverLevel, Villager villager, CallbackInfo ci) {
        if (villager.getVehicle() instanceof Chicken)
            villager.stopRiding();
    }
}
