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
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 아이템의 요소를 수정하는 아이템 수정자 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemModifier implements DataModifier<Item>, CodecUtil.CodecComponent<ItemModifier> {
    /** 코덱 레지스트리 */
    private static final VPRegistry<MapCodec<? extends ItemModifier>> CODEC_REGISTRY = VPRegistry.ITEM_MODIFIER.createRegistry("type");
    /** 유형별 코덱 */
    private static final Codec<ItemModifier> TYPE_CODEC = CodecUtil.fromCodecRegistry(CODEC_REGISTRY);
    /** JSON 코덱 */
    private static final MapCodec<ItemModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> createBaseCodec(instance)
            .apply(instance, ItemModifier::new));

    static {
        CODEC_REGISTRY.register("item", () -> CODEC);
        CODEC_REGISTRY.register("elytra", () -> ElytraModifier.CODEC);
        CODEC_REGISTRY.register("projectile_weapon", () -> ProjectileWeaponModifier.CODEC);
        CODEC_REGISTRY.register("crossbow", () -> CrossbowModifier.CODEC);
    }

    /** 아이템 데이터 요소 */
    @NonNull
    private final DataComponentMap dataComponentMap;

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.ITEM_MODIFIER.getRegistryKey(), TYPE_CODEC, TYPE_CODEC);
    }

    /**
     * 지정한 아이템에 해당하는 아이템 수정자를 반환한다.
     *
     * @param item 아이템
     * @param <T>  {@link ItemModifier}를 상속받는 아이템 수정자
     * @return 아이템 수정자. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends ItemModifier> T fromItem(@NonNull Item item) {
        return (T) VPRegistry.ITEM_MODIFIER.getValue(BuiltInRegistries.ITEM.getKey(item).getPath());
    }

    /**
     * 지정한 아이템에 해당하는 아이템 수정자를 반환한다.
     *
     * @param item 아이템
     * @return 아이템 수정자
     * @throws IllegalStateException 해당하는 아이템 수정자가 존재하지 않으면 발생
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends ItemModifier> T fromItemOrThrow(@NonNull Item item) {
        return (T) VPRegistry.ITEM_MODIFIER.getValueOrThrow(BuiltInRegistries.ITEM.getKey(item).getPath());
    }

    @NonNull
    private static <T extends ItemModifier> Products.P1<RecordCodecBuilder.Mu<T>, DataComponentMap> createBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
        return instance.group(DataComponentMap.CODEC.optionalFieldOf("components", DataComponentMap.EMPTY)
                .forGetter(ItemModifier::getDataComponentMap));
    }

    @Override
    @NonNull
    public MapCodec<? extends ItemModifier> getCodec() {
        return CODEC;
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
        public MapCodec<? extends ItemModifier> getCodec() {
            return CODEC;
        }
    }

    /**
     * {@link ProjectileWeaponItem}의 아이템 수정자 클래스.
     */
    @Getter
    public static class ProjectileWeaponModifier extends ItemModifier {
        private static final MapCodec<ProjectileWeaponModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                createBaseCodec(instance).apply(instance, ProjectileWeaponModifier::new));

        /** 화살 발사 속력 */
        private final float shootingPower;

        private ProjectileWeaponModifier(@NonNull DataComponentMap dataComponentMap, float shootingPower) {
            super(dataComponentMap);
            this.shootingPower = shootingPower;
        }

        @NonNull
        private static <T extends ProjectileWeaponModifier> Products.P2<RecordCodecBuilder.Mu<T>, DataComponentMap, Float> createBaseCodec(@NonNull RecordCodecBuilder.Instance<T> instance) {
            return ItemModifier.createBaseCodec(instance)
                    .and(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("shooting_power", 3.0F)
                            .forGetter(ProjectileWeaponModifier::getShootingPower));
        }

        @Override
        @NonNull
        public MapCodec<? extends ItemModifier> getCodec() {
            return CODEC;
        }
    }

    /**
     * {@link Items#CROSSBOW}의 아이템 수정자 클래스.
     */
    @Getter
    public static final class CrossbowModifier extends ProjectileWeaponModifier {
        private static final MapCodec<CrossbowModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
                ProjectileWeaponModifier.createBaseCodec(instance)
                        .and(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("shooting_power_firework_rocket", 1.6F)
                                .forGetter(CrossbowModifier::getShootingPowerFireworkRocket))
                        .apply(instance, CrossbowModifier::new));

        /** 폭죽 발사 속력 */
        private final float shootingPowerFireworkRocket;

        private CrossbowModifier(@NonNull DataComponentMap dataComponentMap, float shootingPower, float shootingPowerFireworkRocket) {
            super(dataComponentMap, shootingPower);
            this.shootingPowerFireworkRocket = shootingPowerFireworkRocket;
        }

        @Override
        @NonNull
        public MapCodec<? extends ItemModifier> getCodec() {
            return CODEC;
        }
    }
}
