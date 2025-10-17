package com.dace.vanillaplus.data.modifier;

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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * 엔티티의 요소를 수정하는 엔티티 수정자 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityModifier implements DataModifier<EntityType<?>>, CodecUtil.CodecComponent<EntityModifier> {
    /** 코덱 레지스트리 */
    private static final VPRegistry<MapCodec<? extends EntityModifier>> CODEC_REGISTRY = VPRegistry.ENTITY_MODIFIER.createRegistry("type");
    /** 유형별 코덱 */
    private static final Codec<EntityModifier> TYPE_CODEC = CodecUtil.fromCodecRegistry(CODEC_REGISTRY);
    /** JSON 코덱 */
    private static final MapCodec<EntityModifier> CODEC = MapCodec.unit(new EntityModifier());

    static {
        CODEC_REGISTRY.register("entity", () -> CODEC);
        CODEC_REGISTRY.register("living_entity", () -> LivingEntityModifier.CODEC);
        CODEC_REGISTRY.register("crossbow_attack_mob", () -> CrossbowAttackMobModifier.CODEC);
    }

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.ENTITY_MODIFIER.getRegistryKey(), TYPE_CODEC, TYPE_CODEC);
    }

    /**
     * 지정한 엔티티 타입에 해당하는 엔티티 수정자를 반환한다.
     *
     * @param entityType 엔티티 타입
     * @return 엔티티 수정자. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends EntityModifier> T fromEntityType(@NonNull EntityType<?> entityType) {
        return (T) VPRegistry.ENTITY_MODIFIER.getValue(BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath());
    }

    /**
     * 지정한 엔티티 타입에 해당하는 엔티티 수정자를 반환한다.
     *
     * @param entityType 엔티티 타입
     * @return 엔티티 수정자
     * @throws IllegalStateException 해당하는 엔티티 수정자가 존재하지 않으면 발생
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends EntityModifier> T fromEntityTypeOrThrow(@NonNull EntityType<?> entityType) {
        return (T) VPRegistry.ENTITY_MODIFIER.getValueOrThrow(BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath());
    }

    @Override
    @NonNull
    public MapCodec<? extends EntityModifier> getCodec() {
        return CODEC;
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
        public MapCodec<? extends EntityModifier> getCodec() {
            return CODEC;
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
        public MapCodec<? extends EntityModifier> getCodec() {
            return CODEC;
        }
    }
}
