package com.dace.vanillaplus.mixin.world.entity.raid;

import com.dace.vanillaplus.data.registryobject.VPGameRules;
import com.dace.vanillaplus.mixin.world.entity.monster.MonsterMixin;
import com.dace.vanillaplus.util.IdentifierUtil;
import com.dace.vanillaplus.world.entity.raid.RaiderConfig;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;

@Mixin(Raider.class)
public abstract class RaiderMixin<T extends Raider> extends MonsterMixin<T> {
    @Unique
    private static final String LOOT_TABLE_RAID_EQUIPMENT_PREFIX = "equipment/raid/";
    @Unique
    @Nullable
    private ResourceKey<LootTable> raidEquipmentResourceKey;

    @Shadow
    public abstract boolean hasRaid();

    @Unique
    protected void applyCustomRaidBuffs() {
        RaiderConfig.get(getThis()).raidMobEffects().apply(getThis(), getThis());

        if (raidEquipmentResourceKey != null)
            equip(raidEquipmentResourceKey, Collections.emptyMap());
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        if (!RaiderConfig.get(getThis()).sprintDuringRaids())
            return;

        MoveControl moveControl = getMoveControl();
        setSprinting(hasRaid() && moveControl.hasWanted() && moveControl.getSpeedModifier() >= 1);
    }

    @Override
    public ItemStack getProjectile(ItemStack heldWeapon) {
        return RaiderConfig.get(getThis()).ammoModifiers().apply(getThis(), super.getProjectile(heldWeapon));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setLootTableRaidEquipment(EntityType<? extends Raider> type, Level level, CallbackInfo ci) {
        if (RaiderConfig.get(getThis()).useDataDrivenRaidEquipment())
            raidEquipmentResourceKey = ResourceKey.create(Registries.LOOT_TABLE,
                    IdentifierUtil.fromPath(EntityType.getKey(getType()).getPath()).withPrefix(LOOT_TABLE_RAID_EQUIPMENT_PREFIX));
    }

    @WrapWithCondition(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;updateBossbar()V"))
    private boolean removeUpdateBossbarCall(Raid instance, @Local(argsOnly = true) ServerLevel level) {
        return VPGameRules.getValue(VPGameRules.RAID_TIME_LIMIT, level) <= 0;
    }
}
