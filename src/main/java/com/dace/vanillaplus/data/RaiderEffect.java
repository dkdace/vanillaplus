package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 흉조 레벨에 따라 습격대 엔티티에게 적용되는 효과를 관리하는 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class RaiderEffect implements CodecUtil.CodecComponent<RaiderEffect> {
    /** 코덱 레지스트리 */
    private static final VPRegistry<MapCodec<? extends RaiderEffect>> CODEC_REGISTRY = VPRegistry.RAIDER_EFFECT.createRegistry("type");
    /** 유형별 코덱 */
    private static final Codec<RaiderEffect> TYPE_CODEC = CodecUtil.fromCodecRegistry(CODEC_REGISTRY);

    static {
        CODEC_REGISTRY.register("pillager", () -> PillagerEffect.CODEC);
        CODEC_REGISTRY.register("vindicator", () -> VindicatorEffect.CODEC);
        CODEC_REGISTRY.register("witch", () -> WitchEffect.CODEC);
        CODEC_REGISTRY.register("ravager", () -> RavagerEffect.CODEC);
        CODEC_REGISTRY.register("evoker", () -> EvokerEffect.CODEC);
        CODEC_REGISTRY.register("illusioner", () -> IllusionerEffect.CODEC);
    }

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.RAIDER_EFFECT.getRegistryKey(), TYPE_CODEC, TYPE_CODEC);
    }

    /**
     * 지정한 엔티티 타입에 해당하는 습격자 효과를 반환한다.
     *
     * @param <T> {@link RaiderEffect}를 상속받는 습격자 효과
     * @return 습격자 효과
     * @throws IllegalStateException 해당하는 엔티티 수정자가 존재하지 않으면 발생
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends RaiderEffect> T fromEntityType(@NonNull EntityType<?> entityType) {
        return (T) VPRegistry.RAIDER_EFFECT.getValueOrThrow(BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath());
    }

    /**
     * 효과 정보 클래스.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public abstract static class EffectInfo {
        /** 레벨 기반 확률 */
        @NonNull
        private final LevelBasedValue.Clamped levelBasedChance;

        @NonNull
        private static <T extends EffectInfo> Products.P1<RecordCodecBuilder.Mu<T>, LevelBasedValue.Clamped> createBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
            return instance.group(LevelBasedValue.CODEC.fieldOf("chance")
                    .xmap(levelBasedValue -> new LevelBasedValue.Clamped(levelBasedValue, 0, 1),
                            LevelBasedValue.Clamped::value)
                    .forGetter(EffectInfo::getLevelBasedChance));
        }

        /**
         * 지정한 몹이 효과를 적용할 확률을 만족하는지 확인한다.
         *
         * @param mob 대상 몹
         * @return 확률을 만족하면 {@code true} 반환
         */
        private boolean checkChance(@NonNull Mob mob) {
            Raid raid = null;
            if (mob instanceof Vex vex) {
                Mob owner = vex.getOwner();

                if (owner instanceof Evoker evoker)
                    raid = evoker.getCurrentRaid();
            } else if (mob instanceof Raider raider)
                raid = raider.getCurrentRaid();

            return raid != null && getLevelBasedChance().calculate(raid.getRaidOmenLevel()) > mob.getRandom().nextFloat();
        }

        /**
         * 아이템 마법 부여 효과 정보 클래스.
         */
        public static final class EnchantItemInfo extends EffectInfo {
            /** JSON 코덱 */
            private static final Codec<EnchantItemInfo> CODEC = RecordCodecBuilder.create(instance ->
                    createBaseCodec(instance)
                            .and(instance.group(EquipmentSlot.CODEC.fieldOf("equipment")
                                            .forGetter(enchantItemInfo -> enchantItemInfo.equipmentSlot),
                                    Enchantment.CODEC.fieldOf("enchantment")
                                            .forGetter(enchantItemInfo -> enchantItemInfo.enchantmentHolder),
                                    ExtraCodecs.intRange(1, 255).fieldOf("level")
                                            .forGetter(enchantItemInfo -> enchantItemInfo.level)))
                            .apply(instance, EnchantItemInfo::new));

            /** 장착 슬롯 */
            @NonNull
            private final EquipmentSlot equipmentSlot;
            /** 마법 부여 홀더 인스턴스 */
            @NonNull
            private final Holder<Enchantment> enchantmentHolder;
            /** 마법 부여 레벨 */
            private final int level;

            private EnchantItemInfo(@NonNull LevelBasedValue.Clamped levelBasedChance, @NonNull EquipmentSlot equipmentSlot,
                                    @NonNull Holder<Enchantment> enchantmentHolder, int level) {
                super(levelBasedChance);

                this.equipmentSlot = equipmentSlot;
                this.enchantmentHolder = enchantmentHolder;
                this.level = level;
            }

            /**
             * 지정한 몹의 아이템에 마법 부여를 적용한다.
             *
             * @param mob 대상 몹
             */
            public void applyEnchantment(@NonNull Mob mob) {
                if (!super.checkChance(mob))
                    return;

                ItemStack itemStack = mob.getItemBySlot(equipmentSlot);

                EnchantmentHelper.updateEnchantments(itemStack, mutable -> {
                    for (Holder<Enchantment> currentEnchantmentHolder : mutable.keySet())
                        if (enchantmentHolder.equals(currentEnchantmentHolder)) {
                            mutable.set(currentEnchantmentHolder, level);
                            return;
                        } else if (!Enchantment.areCompatible(currentEnchantmentHolder, enchantmentHolder))
                            return;

                    mutable.upgrade(enchantmentHolder, level);
                });
            }
        }

        /**
         * 물약이 묻은 화살 효과 정보 클래스.
         */
        public static final class TippedArrowInfo extends EffectInfo {
            /** JSON 코덱 */
            private static final Codec<TippedArrowInfo> CODEC = RecordCodecBuilder.create(instance ->
                    createBaseCodec(instance)
                            .and(Potion.CODEC.listOf().fieldOf("potions")
                                    .forGetter(tippedArrowInfo -> tippedArrowInfo.potionHolders))
                            .apply(instance, TippedArrowInfo::new));

            /** 물약 홀더 인스턴스 목록 */
            @NonNull
            private final List<Holder<Potion>> potionHolders;

            private TippedArrowInfo(@NonNull LevelBasedValue.Clamped levelBasedChance, @NonNull List<Holder<Potion>> potionHolders) {
                super(levelBasedChance);
                this.potionHolders = potionHolders;
            }

            /**
             * 지정한 몹의 화살을 물약이 묻은 화살로 대체한다.
             *
             * @param mob       대상 몹
             * @param itemStack 화살 아이템
             * @return 화살 아이템
             */
            @NonNull
            public ItemStack applyArrowPotionEffect(@NonNull Mob mob, @NonNull ItemStack itemStack) {
                if (super.checkChance(mob) && itemStack.is(Items.ARROW)) {
                    Holder<Potion> potionHolder = potionHolders.get(mob.getRandom().nextInt(potionHolders.size()));
                    return PotionContents.createItemStack(Items.TIPPED_ARROW, potionHolder);
                }

                return itemStack;
            }
        }

        /**
         * 아이템 장착 효과 정보 클래스.
         */
        public static final class EquipItemInfo extends EffectInfo {
            /** JSON 코덱 */
            private static final Codec<EquipItemInfo> CODEC = RecordCodecBuilder.create(instance ->
                    createBaseCodec(instance)
                            .and(instance.group(EquipmentSlot.CODEC.fieldOf("equipment")
                                            .forGetter(equipItemInfo -> equipItemInfo.equipmentSlot),
                                    ItemStack.CODEC.fieldOf("item").forGetter(equipItemInfo -> equipItemInfo.itemStack)))
                            .apply(instance, EquipItemInfo::new));

            /** 장착 슬롯 */
            @NonNull
            private final EquipmentSlot equipmentSlot;
            /** 아이템 */
            @NonNull
            private final ItemStack itemStack;

            private EquipItemInfo(@NonNull LevelBasedValue.Clamped levelBasedChance, @NonNull EquipmentSlot equipmentSlot,
                                  @NonNull ItemStack itemStack) {
                super(levelBasedChance);

                this.equipmentSlot = equipmentSlot;
                this.itemStack = itemStack;
            }

            /**
             * 지정한 몹에게 아이템을 장착시킨다.
             *
             * @param mob 대상 몹
             */
            public void equipItem(@NonNull Mob mob) {
                if (super.checkChance(mob))
                    mob.setItemSlot(equipmentSlot, itemStack.copy());
            }
        }

        /**
         * 몹 상태 효과 정보 클래스.
         */
        public static final class MobEffectInfo extends EffectInfo {
            /** JSON 코덱 */
            private static final Codec<MobEffectInfo> CODEC = RecordCodecBuilder.create(instance ->
                    createBaseCodec(instance)
                            .and(instance.group(MobEffect.CODEC.fieldOf("effect")
                                            .forGetter(mobEffectInfo -> mobEffectInfo.mobEffectHolder),
                                    ExtraCodecs.UNSIGNED_BYTE.optionalFieldOf("amplifier", 0)
                                            .forGetter(mobEffectInfo -> mobEffectInfo.amplifier)))
                            .apply(instance, MobEffectInfo::new));

            /** 상태 효과 홀더 인스턴스 */
            @NonNull
            private final Holder<MobEffect> mobEffectHolder;
            /** 효과 세기 */
            private final int amplifier;

            private MobEffectInfo(@NonNull LevelBasedValue.Clamped levelBasedChance, @NonNull Holder<MobEffect> mobEffectHolder, int amplifier) {
                super(levelBasedChance);

                this.mobEffectHolder = mobEffectHolder;
                this.amplifier = amplifier;
            }

            /**
             * 지정한 몹에게 상태 효과를 적용한다.
             *
             * @param mob 대상 몹
             */
            public void applyMobEffect(@NonNull Mob mob) {
                if (super.checkChance(mob))
                    mob.addEffect(new MobEffectInstance(mobEffectHolder, -1, amplifier));
            }
        }

        /**
         * 물약 강화 효과 정보 클래스.
         */
        public static final class UpgradePotionInfo extends EffectInfo {
            /** JSON 코덱 */
            private static final Codec<UpgradePotionInfo> CODEC = RecordCodecBuilder.create(instance ->
                    createBaseCodec(instance).apply(instance, UpgradePotionInfo::new));

            private UpgradePotionInfo(@NonNull LevelBasedValue.Clamped levelBasedChance) {
                super(levelBasedChance);
            }

            /**
             * 지정한 몹의 물약을 연장형 또는 강화형 물약으로 강화한다.
             *
             * @param mob       대상 몹
             * @param itemStack 물약 아이템
             * @return 물약 아이템
             */
            @NonNull
            public ItemStack upgradePotion(@NonNull Mob mob, @NonNull ItemStack itemStack) {
                if (super.checkChance(mob)) {
                    List<ItemStack> ingredients = new ArrayList<>();
                    ingredients.add(new ItemStack(Items.GLOWSTONE_DUST));
                    ingredients.add(new ItemStack(Items.REDSTONE));

                    PotionBrewing potionBrewing = mob.level().potionBrewing();

                    while (!ingredients.isEmpty()) {
                        ItemStack ingredient = ingredients.remove(mob.level().random.nextInt(ingredients.size()));

                        if (potionBrewing.hasMix(itemStack, ingredient))
                            return potionBrewing.mix(ingredient, itemStack);
                    }
                }

                return itemStack;
            }
        }

        /**
         * 물약 대체 효과 정보 클래스.
         */
        public static final class ReplacePotionInfo extends EffectInfo {
            /** JSON 코덱 */
            private static final Codec<ReplacePotionInfo> CODEC = RecordCodecBuilder.create(instance ->
                    createBaseCodec(instance)
                            .and(instance.group(LootItemCondition.DIRECT_CODEC.fieldOf("predicate")
                                            .forGetter(replacePotionInfo -> replacePotionInfo.condition),
                                    Potion.CODEC.fieldOf("potion")
                                            .forGetter(replacePotionInfo -> replacePotionInfo.potionHolder)))
                            .apply(instance, ReplacePotionInfo::new));

            /** 물약을 대체할 조건 */
            @NonNull
            private final LootItemCondition condition;
            /** 물약 홀더 인스턴스 */
            @NonNull
            private final Holder<Potion> potionHolder;

            private ReplacePotionInfo(@NonNull LevelBasedValue.Clamped levelBasedChance, @NonNull LootItemCondition condition,
                                      @NonNull Holder<Potion> potionHolder) {
                super(levelBasedChance);

                this.condition = condition;
                this.potionHolder = potionHolder;
            }

            /**
             * 지정한 몹의 물약을 특정 물약으로 대체한다.
             *
             * @param mob       대상 몹
             * @param itemStack 물약 아이템
             * @return 물약 아이템
             */
            @NonNull
            public ItemStack replacePotion(@NonNull Mob mob, @NonNull ItemStack itemStack) {
                if (super.checkChance(mob) && itemStack.is(Items.POTION)) {
                    LootParams lootParams = new LootParams.Builder((ServerLevel) mob.level())
                            .withParameter(LootContextParams.THIS_ENTITY, mob)
                            .withParameter(LootContextParams.ORIGIN, mob.position()).create();
                    LootContext lootContext = new LootContext.Builder(lootParams).create(Optional.empty());

                    if (condition.test(lootContext))
                        return PotionContents.createItemStack(Items.POTION, potionHolder);
                }

                return itemStack;
            }
        }

        /**
         * 수치 수정 효과 정보 클래스.
         */
        public static final class ModifyValueInfo extends EffectInfo {
            /** JSON 코덱 */
            private static final Codec<ModifyValueInfo> CODEC = RecordCodecBuilder.create(instance ->
                    createBaseCodec(instance)
                            .and(Codec.FLOAT.fieldOf("multiplier").forGetter(modifyValueInfo -> modifyValueInfo.multiplier))
                            .apply(instance, ModifyValueInfo::new));

            /** 값 수정 배수 */
            private final float multiplier;

            private ModifyValueInfo(@NonNull LevelBasedValue.Clamped levelBasedChance, float multiplier) {
                super(levelBasedChance);
                this.multiplier = multiplier;
            }

            /**
             * 지정한 수치에 수정자를 적용한 최종 값을 반환한다.
             *
             * @param mob   대상 몹
             * @param value 원본 수치
             * @return 최종 값
             */
            public int modifyValue(@NonNull Mob mob, int value) {
                return super.checkChance(mob) ? (int) (value * multiplier) : value;
            }

            /**
             * 지정한 수치에 수정자를 적용한 최종 값을 반환한다.
             *
             * @param mob   대상 몹
             * @param value 원본 수치
             * @return 최종 값
             */
            public double modifyValue(@NonNull Mob mob, double value) {
                return super.checkChance(mob) ? value * multiplier : value;
            }
        }
    }

    /**
     * 약탈자 습격 효과 클래스.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static final class PillagerEffect extends RaiderEffect {
        private static final MapCodec<PillagerEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(EffectInfo.EnchantItemInfo.CODEC.listOf().fieldOf("enchant_items").forGetter(PillagerEffect::getEnchantItemInfos),
                        EffectInfo.TippedArrowInfo.CODEC.fieldOf("tipped_arrow").forGetter(PillagerEffect::getTippedArrowInfo))
                .apply(instance, PillagerEffect::new));

        /** 아이템 마법 부여 효과 목록 */
        @NonNull
        private final List<EffectInfo.EnchantItemInfo> enchantItemInfos;
        /** 물약이 묻은 화살 효과 */
        @NonNull
        private final EffectInfo.TippedArrowInfo tippedArrowInfo;

        @Override
        @NonNull
        public MapCodec<? extends RaiderEffect> getCodec() {
            return CODEC;
        }
    }

    /**
     * 변명자 습격 효과 클래스.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static final class VindicatorEffect extends RaiderEffect {
        private static final MapCodec<VindicatorEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(EffectInfo.EnchantItemInfo.CODEC.listOf().fieldOf("enchant_items").forGetter(VindicatorEffect::getEnchantItemInfos),
                        EffectInfo.MobEffectInfo.CODEC.listOf().fieldOf("mob_effects").forGetter(VindicatorEffect::getMobEffectInfos))
                .apply(instance, VindicatorEffect::new));

        /** 아이템 마법 부여 효과 목록 */
        @NonNull
        private final List<EffectInfo.EnchantItemInfo> enchantItemInfos;
        /** 몹 상태 효과 목록 */
        @NonNull
        private final List<EffectInfo.MobEffectInfo> mobEffectInfos;

        @Override
        @NonNull
        public MapCodec<? extends RaiderEffect> getCodec() {
            return CODEC;
        }
    }

    /**
     * 마녀 습격 효과 클래스.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static final class WitchEffect extends RaiderEffect {
        private static final MapCodec<WitchEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(EffectInfo.UpgradePotionInfo.CODEC.fieldOf("upgrade_potion_for_self")
                                .forGetter(WitchEffect::getUpgradePotionForSelfInfo),
                        EffectInfo.UpgradePotionInfo.CODEC.fieldOf("upgrade_potion_for_support")
                                .forGetter(WitchEffect::getUpgradePotionForSupportInfo),
                        EffectInfo.UpgradePotionInfo.CODEC.fieldOf("upgrade_potion_for_attack")
                                .forGetter(WitchEffect::getUpgradePotionForAttackInfo),
                        EffectInfo.ReplacePotionInfo.CODEC.fieldOf("replace_potion_for_self")
                                .forGetter(WitchEffect::getReplacePotionForSelfInfo),
                        EffectInfo.ModifyValueInfo.CODEC.listOf().fieldOf("modify_potion_cooldowns")
                                .forGetter(WitchEffect::getModifyPotionCooldownInfos))
                .apply(instance, WitchEffect::new));

        /** 물약 강화 효과 (자신) */
        @NonNull
        private final EffectInfo.UpgradePotionInfo upgradePotionForSelfInfo;
        /** 물약 강화 효과 (지원) */
        @NonNull
        private final EffectInfo.UpgradePotionInfo upgradePotionForSupportInfo;
        /** 물약 강화 효과 (공격) */
        @NonNull
        private final EffectInfo.UpgradePotionInfo upgradePotionForAttackInfo;
        /** 물약 대체 효과 */
        @NonNull
        private final EffectInfo.ReplacePotionInfo replacePotionForSelfInfo;
        /** 물약 투척 쿨타임 수정 효과 목록 */
        @NonNull
        private final List<EffectInfo.ModifyValueInfo> modifyPotionCooldownInfos;

        @Override
        @NonNull
        public MapCodec<? extends RaiderEffect> getCodec() {
            return CODEC;
        }
    }

    /**
     * 파괴수 습격 효과 클래스.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static final class RavagerEffect extends RaiderEffect {
        private static final MapCodec<RavagerEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(EffectInfo.MobEffectInfo.CODEC.listOf().fieldOf("mob_effects").forGetter(RavagerEffect::getMobEffectInfos))
                .apply(instance, RavagerEffect::new));

        /** 몹 상태 효과 목록 */
        @NonNull
        private final List<EffectInfo.MobEffectInfo> mobEffectInfos;

        @Override
        @NonNull
        public MapCodec<? extends RaiderEffect> getCodec() {
            return CODEC;
        }
    }

    /**
     * 소환사 습격 효과 클래스.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static final class EvokerEffect extends RaiderEffect {
        private static final MapCodec<EvokerEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(EffectInfo.MobEffectInfo.CODEC.listOf().fieldOf("vex_mob_effects").forGetter(EvokerEffect::getVexMobEffectInfos),
                        EffectInfo.EquipItemInfo.CODEC.listOf().fieldOf("equip_items").forGetter(EvokerEffect::getEquipItemInfos))
                .apply(instance, EvokerEffect::new));

        /** 벡스 대상 몹 상태 효과 목록 */
        @NonNull
        private final List<EffectInfo.MobEffectInfo> vexMobEffectInfos;
        /** 아이템 장착 효과 목록 */
        @NonNull
        private final List<EffectInfo.EquipItemInfo> equipItemInfos;

        @Override
        @NonNull
        public MapCodec<? extends RaiderEffect> getCodec() {
            return CODEC;
        }
    }

    /**
     * 환술사 습격 효과 클래스.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static final class IllusionerEffect extends RaiderEffect {
        private static final MapCodec<IllusionerEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(EffectInfo.EnchantItemInfo.CODEC.listOf().fieldOf("enchant_items").forGetter(IllusionerEffect::getEnchantItemInfos),
                        EffectInfo.TippedArrowInfo.CODEC.fieldOf("tipped_arrow").forGetter(IllusionerEffect::getTippedArrowInfo))
                .apply(instance, IllusionerEffect::new));

        /** 아이템 마법 부여 효과 목록 */
        @NonNull
        private final List<EffectInfo.EnchantItemInfo> enchantItemInfos;
        /** 물약이 묻은 화살 효과 */
        @NonNull
        private final EffectInfo.TippedArrowInfo tippedArrowInfo;

        @Override
        @NonNull
        public MapCodec<? extends RaiderEffect> getCodec() {
            return CODEC;
        }
    }
}
