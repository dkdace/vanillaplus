package com.dace.vanillaplus.mixin.world.level.block.entity.trialspawner;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.level.block.entity.trialspawner.VPTrialSpawnerConfig;
import com.dace.vanillaplus.extension.world.level.block.entity.trialspawner.VPTrialSpawnerStateData;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerStateData;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(TrialSpawnerState.class)
public abstract class TrialSpawnerStateMixin implements VPMixin<TrialSpawnerState> {
    @ModifyExpressionValue(method = "lambda$spawnOminousOminousItemSpawner$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/trialspawner/TrialSpawnerConfig;ticksBetweenItemSpawners()J"))
    private static long modifyOminousItemCooldown(long ticksBetweenItemSpawners, @Local(argsOnly = true) TrialSpawner trialSpawner,
                                                  @Local(argsOnly = true) TrialSpawnerStateData data) {
        return VPTrialSpawnerStateData.cast(data).getTrialOmenLevel()
                .map(level -> {
                    List<Integer> values = VPTrialSpawnerConfig.cast(trialSpawner.ominousConfig()).getOminous().ominousItemCooldowns();
                    return values.isEmpty() ? null : (long) values.get(Math.min(level, values.size()) - 1);
                })
                .orElse(ticksBetweenItemSpawners);
    }

    @Inject(method = "lambda$spawnOminousOminousItemSpawner$0", at = @At("TAIL"))
    private static void modifyOminousItemDelay(ServerLevel level, ItemStack itemToDispense, TrialSpawnerStateData data, TrialSpawner trialSpawner,
                                               Vec3 pos, CallbackInfo ci, @Local(name = "itemSpawner") OminousItemSpawner itemSpawner) {
        VPTrialSpawnerStateData.cast(data).getTrialOmenLevel().ifPresent(trialOmenLevel -> {
            List<Integer> values = VPTrialSpawnerConfig.cast(trialSpawner.ominousConfig()).getOminous().ominousItemActivationDelays();

            if (!values.isEmpty())
                itemSpawner.spawnItemAfterTicks = values.get(Math.min(trialOmenLevel, values.size()) - 1);
        });
    }

    @Inject(method = "tickAndGetNext", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;)V",
            ordinal = 0))
    private void awardXP(BlockPos spawnerPos, TrialSpawner trialSpawner, ServerLevel serverLevel, CallbackInfoReturnable<TrialSpawnerState> cir,
                         @Local(name = "data") TrialSpawnerStateData data, @Local(name = "config") TrialSpawnerConfig config) {
        VPTrialSpawnerStateData.cast(data).getTrialOmenLevel().ifPresent(level -> {
            List<Integer> values = VPTrialSpawnerConfig.cast(config).getOminous().experiences();

            if (!values.isEmpty())
                ExperienceOrb.award(serverLevel, Vec3.atCenterOf(spawnerPos), values.get(Math.min(level, values.size()) - 1) *
                        data.detectedPlayers.size());
        });
    }

    @Definition(id = "data", local = @Local(type = TrialSpawnerStateData.class, name = "data"))
    @Definition(id = "ejectingLootTable", field = "Lnet/minecraft/world/level/block/entity/trialspawner/TrialSpawnerStateData;ejectingLootTable:Ljava/util/Optional;")
    @Expression("data.ejectingLootTable")
    @ModifyExpressionValue(method = "tickAndGetNext", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
    private Optional<ResourceKey<LootTable>> modifyLootTable(Optional<ResourceKey<LootTable>> ejectingLootTable,
                                                             @Local(name = "data") TrialSpawnerStateData data,
                                                             @Local(name = "config") TrialSpawnerConfig config,
                                                             @Local(name = "random") RandomSource random) {
        return VPTrialSpawnerStateData.cast(data).getTrialOmenLevel().flatMap(level ->
                VPTrialSpawnerConfig.cast(config).getOminous().overrideLootTable().flatMap(overrideLootTable -> {
                    List<Float> values = overrideLootTable.chances();

                    return values.get(Math.min(level, values.size()) - 1) > random.nextFloat()
                            ? Optional.of(overrideLootTable.lootTableResourceKey())
                            : ejectingLootTable;
                }));
    }
}
