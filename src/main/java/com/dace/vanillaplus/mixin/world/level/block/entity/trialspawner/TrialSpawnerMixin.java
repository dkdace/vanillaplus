package com.dace.vanillaplus.mixin.world.level.block.entity.trialspawner;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.entity.VPMob;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.UUID;

@Mixin(TrialSpawner.class)
public abstract class TrialSpawnerMixin implements VPMixin<TrialSpawner> {
    @Definition(id = "hasNoConfiguration", local = @Local(type = boolean.class, name = "hasNoConfiguration"))
    @Expression("hasNoConfiguration")
    @Inject(method = "spawnMob", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void setSpawnedMobTrialSpawnerPos(ServerLevel level, BlockPos spawnerPos, CallbackInfoReturnable<Optional<UUID>> cir,
                                              @Local(name = "mob") Mob mob) {
        VPMob.cast(mob).setTrialSpawnerPos(spawnerPos);
    }
}
