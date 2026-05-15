package com.dace.vanillaplus.world.entity.raid;

import com.dace.vanillaplus.world.entity.config.ConditionalModifierList;
import com.dace.vanillaplus.world.entity.config.ItemFunctionsModifier;
import com.dace.vanillaplus.world.entity.config.MobEffectModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;

/**
 * {@link Raider}의 엔티티 설정 데이터 요소 클래스.
 *
 * @param attackBabyVillagers        어린 주민 공격 여부
 * @param alwaysOpenDoors            항상 문을 열 수 있는지 여부
 * @param sprintDuringRaids          습격 중 달리기 여부
 * @param useDataDrivenRaidEquipment 데이터 팩 습격 장비 사용 여부
 * @param raidMobEffects             습격 중 적용되는 상태 효과 목록
 * @param raidSummonedMobEffects     습격 중 소환된 몹에게 적용되는 상태 효과 목록
 * @param ammoModifiers              탄약에 적용되는 전리품 수정자 목록
 */
public record RaiderConfig(boolean attackBabyVillagers, boolean alwaysOpenDoors, boolean sprintDuringRaids, boolean useDataDrivenRaidEquipment,
                           @NonNull ConditionalModifierList<MobEffectModifier, LivingEntity> raidMobEffects,
                           @NonNull ConditionalModifierList<MobEffectModifier, LivingEntity> raidSummonedMobEffects,
                           @NonNull ConditionalModifierList<ItemFunctionsModifier, ItemStack> ammoModifiers) {
    /** 기본값 */
    public static final RaiderConfig DEFAULT = new RaiderConfig(false, false, false,
            false, ConditionalModifierList.empty(), ConditionalModifierList.empty(), ConditionalModifierList.empty());
    /** JSON 코덱 */
    public static final Codec<RaiderConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.BOOL.optionalFieldOf("attack_baby_villagers", DEFAULT.attackBabyVillagers).forGetter(RaiderConfig::attackBabyVillagers),
                    Codec.BOOL.optionalFieldOf("always_open_doors", DEFAULT.alwaysOpenDoors).forGetter(RaiderConfig::alwaysOpenDoors),
                    Codec.BOOL.optionalFieldOf("sprint_during_raids", DEFAULT.sprintDuringRaids).forGetter(RaiderConfig::sprintDuringRaids),
                    Codec.BOOL.optionalFieldOf("use_data_driven_raid_equipment", DEFAULT.useDataDrivenRaidEquipment)
                            .forGetter(RaiderConfig::useDataDrivenRaidEquipment),
                    ConditionalModifierList.createCodec(MobEffectModifier.CODEC).optionalFieldOf("raid_mob_effects", DEFAULT.raidMobEffects)
                            .forGetter(RaiderConfig::raidMobEffects),
                    ConditionalModifierList.createCodec(MobEffectModifier.CODEC)
                            .optionalFieldOf("raid_summoned_mob_effects", DEFAULT.raidSummonedMobEffects)
                            .forGetter(RaiderConfig::raidSummonedMobEffects),
                    ConditionalModifierList.createCodec(ItemFunctionsModifier.CODEC).optionalFieldOf("ammo_modifiers", DEFAULT.ammoModifiers)
                            .forGetter(RaiderConfig::ammoModifiers))
            .apply(instance, RaiderConfig::new));
}
