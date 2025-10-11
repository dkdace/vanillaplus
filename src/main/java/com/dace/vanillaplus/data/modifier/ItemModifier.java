package com.dace.vanillaplus.data.modifier;

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
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

/**
 * 아이템의 요소를 수정하는 아이템 수정자 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemModifier implements DataModifier<Item>, CodecUtil.CodecComponent<ItemModifier, ItemModifier.Types> {
    /** 유형별 코덱 */
    private static final Codec<ItemModifier> TYPE_CODEC = CodecUtil.fromCodecComponent(Types.class);
    /** JSON 코덱 */
    private static final MapCodec<ItemModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> createBaseCodec(instance)
            .apply(instance, ItemModifier::new));

    /** 아이템 데이터 요소 */
    @NonNull
    private final DataComponentMap dataComponentMap;

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistries.ITEM_MODIFIER.getRegistryKey(), TYPE_CODEC, TYPE_CODEC);
    }

    @NonNull
    private static <T extends ItemModifier> Products.P1<RecordCodecBuilder.Mu<T>, DataComponentMap> createBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
        return instance.group(DataComponentMap.CODEC.optionalFieldOf("components", DataComponentMap.EMPTY)
                .forGetter(ItemModifier::getDataComponentMap));
    }

    @Override
    @NonNull
    public Types getType() {
        return Types.ITEM;
    }

    /**
     * 아이템 수정자의 유형 목록.
     */
    @AllArgsConstructor
    @Getter
    public enum Types implements CodecUtil.CodecComponentType<ItemModifier, Types> {
        ITEM(CODEC),
        ELYTRA(ElytraModifier.CODEC),
        CROSSBOW(CrossbowModifier.CODEC);

        /** JSON 코덱 */
        private final MapCodec<? extends ItemModifier> codec;
    }

    /**
     * {@link Items#ELYTRA}의 아이템 수정자 클래스.
     */
    @Getter
    public static final class ElytraModifier extends ItemModifier {
        private static final MapCodec<ElytraModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> createBaseCodec(instance)
                .and(instance.group(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("firework_add_speed_multiplier", 1.5F)
                                .forGetter(ElytraModifier::getFireworkAddSpeedMultiplier),
                        ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("firework_final_speed_multiplier", 0.5F)
                                .forGetter(ElytraModifier::getFireworkFinalSpeedModifier)))
                .apply(instance, ElytraModifier::new));

        /** 폭죽의 추가 속도 배수 */
        private final float fireworkAddSpeedMultiplier;
        /** 폭죽의 최종 속도 배수 */
        private final float fireworkFinalSpeedModifier;

        private ElytraModifier(@NonNull DataComponentMap dataComponentMap, float fireworkAddSpeedMultiplier, float fireworkFinalSpeedModifier) {
            super(dataComponentMap);

            this.fireworkAddSpeedMultiplier = fireworkAddSpeedMultiplier;
            this.fireworkFinalSpeedModifier = fireworkFinalSpeedModifier;
        }

        @Override
        @NonNull
        public Types getType() {
            return Types.ELYTRA;
        }
    }

    /**
     * {@link Items#CROSSBOW}의 아이템 수정자 클래스.
     */
    @Getter
    public static final class CrossbowModifier extends ItemModifier {
        private static final MapCodec<CrossbowModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                createBaseCodec(instance)
                        .and(instance.group(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("shooting_power_arrow", 3.15F)
                                        .forGetter(CrossbowModifier::getShootingPowerArrow),
                                ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("shooting_power_firework_rocket", 1.6F)
                                        .forGetter(CrossbowModifier::getShootingPowerFireworkRocket)))
                        .apply(instance, CrossbowModifier::new));

        /** 화살 발사 속력 */
        private final float shootingPowerArrow;
        /** 폭죽 발사 속력 */
        private final float shootingPowerFireworkRocket;

        private CrossbowModifier(@NonNull DataComponentMap dataComponentMap, float shootingPowerArrow, float shootingPowerFireworkRocket) {
            super(dataComponentMap);

            this.shootingPowerArrow = shootingPowerArrow;
            this.shootingPowerFireworkRocket = shootingPowerFireworkRocket;
        }

        @Override
        @NonNull
        public Types getType() {
            return Types.CROSSBOW;
        }
    }
}
