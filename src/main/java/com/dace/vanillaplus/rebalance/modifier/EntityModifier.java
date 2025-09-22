package com.dace.vanillaplus.rebalance.modifier;

import com.dace.vanillaplus.VPRegistries;
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
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

import java.util.Collections;
import java.util.List;

/**
 * 엔티티의 요소를 수정하는 엔티티 수정자 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityModifier implements DataModifier<EntityType<?>>, CodecUtil.CodecComponent<EntityModifier, EntityModifier.Types> {
    /** 유형별 코덱 */
    private static final Codec<EntityModifier> TYPE_CODEC = CodecUtil.fromCodecComponent(Types.class);
    /** JSON 코덱 */
    private static final MapCodec<EntityModifier> CODEC = MapCodec.unit(new EntityModifier());

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistries.ENTITY_MODIFIER.getRegistryKey(), TYPE_CODEC, TYPE_CODEC);
    }

    @Override
    @NonNull
    public Types getType() {
        return Types.ENTITY;
    }

    @AllArgsConstructor
    @Getter
    public enum Types implements CodecUtil.CodecComponentType<EntityModifier, Types> {
        ENTITY(CODEC),
        LIVING_ENTITY(LivingEntityModifier.CODEC),
        CROSSBOW_ATTACK_MOB(CrossbowAttackMobModifier.CODEC);

        private final MapCodec<? extends EntityModifier> codec;
    }

    /**
     * {@link LivingEntity}의 엔티티 수정자 클래스.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class LivingEntityModifier extends EntityModifier {
        private static final MapCodec<LivingEntityModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                createBaseCodec(instance).apply(instance, LivingEntityModifier::new));

        /** 엔티티 속성 목록 */
        @NonNull
        private final List<AttributeInstance.Packed> packedAttributes;

        @NonNull
        private static <T extends LivingEntityModifier> Products.P1<RecordCodecBuilder.Mu<T>, List<AttributeInstance.Packed>> createBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
            return instance.group(AttributeInstance.Packed.LIST_CODEC.optionalFieldOf("attributes", Collections.emptyList())
                    .forGetter(LivingEntityModifier::getPackedAttributes));
        }

        @Override
        @NonNull
        public Types getType() {
            return Types.LIVING_ENTITY;
        }
    }

    /**
     * {@link CrossbowAttackMob}의 엔티티 수정자 클래스.
     */
    @Getter
    public static final class CrossbowAttackMobModifier extends LivingEntityModifier {
        private static final MapCodec<CrossbowAttackMobModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                LivingEntityModifier.createBaseCodec(instance)
                        .and(instance.group(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("shooting_power", 1.6F)
                                        .forGetter(CrossbowAttackMobModifier::getShootingPower),
                                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("shooting_range", 8)
                                        .forGetter(CrossbowAttackMobModifier::getShootingRange)))
                        .apply(instance, CrossbowAttackMobModifier::new));

        /** 화살 발사 속력 */
        private final float shootingPower;
        /** 공격 거리 */
        private final int shootingRange;

        private CrossbowAttackMobModifier(@NonNull List<AttributeInstance.Packed> packedAttributes, float shootingPower, int shootingRange) {
            super(packedAttributes);

            this.shootingPower = shootingPower;
            this.shootingRange = shootingRange;
        }

        @Override
        @NonNull
        public Types getType() {
            return Types.CROSSBOW_ATTACK_MOB;
        }
    }
}
