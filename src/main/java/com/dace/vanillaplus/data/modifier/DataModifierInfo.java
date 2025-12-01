package com.dace.vanillaplus.data.modifier;

import com.dace.vanillaplus.VPRegistry;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

/**
 * 데이터 수정자의 레지스트리 정보 클래스.
 *
 * @param <T> {@link DataModifier}를 상속받는 데이터 수정자
 * @param <U> 수정 대상 데이터 타입
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataModifierInfo<T extends DataModifier<U>, U> {
    /** 아이템 수정자 */
    public static final DataModifierInfo<ItemModifier, Item> ITEM_MODIFIER = new DataModifierInfo<>(VPRegistry.ITEM_MODIFIER, BuiltInRegistries.ITEM);
    /** 블록 수정자 */
    public static final DataModifierInfo<BlockModifier, Block> BLOCK_MODIFIER = new DataModifierInfo<>(VPRegistry.BLOCK_MODIFIER, BuiltInRegistries.BLOCK);
    /** 엔티티 수정자 */
    public static final DataModifierInfo<EntityModifier, EntityType<?>> ENTITY_MODIFIER = new DataModifierInfo<>(VPRegistry.ENTITY_MODIFIER, BuiltInRegistries.ENTITY_TYPE);
    /** 물약 수정자 */
    public static final DataModifierInfo<PotionModifier, Potion> POTION_MODIFIER = new DataModifierInfo<>(VPRegistry.POTION_MODIFIER, BuiltInRegistries.POTION);

    /** 데이터 수정자 레지스트리 */
    @NonNull
    @Getter
    private final VPRegistry<T> vpRegistry;
    /** 기존 요소의 내장 레지스트리 */
    @NonNull
    private final Registry<U> builtInRegistry;

    /**
     * 지정한 요소에 해당하는 데이터 수정자를 반환한다.
     *
     * @param element 대상 요소
     * @param <V>     데이터 수정자 타입
     * @return 데이터 수정자. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <V extends T> V get(@NonNull U element) {
        ResourceLocation resourceLocation = builtInRegistry.getKey(element);
        return resourceLocation == null ? null : (V) vpRegistry.getValue(resourceLocation.getPath());
    }

    /**
     * 지정한 요소에 해당하는 데이터 수정자를 반환한다.
     *
     * @param element 대상 요소
     * @param <V>     데이터 수정자 타입
     * @return 데이터 수정자
     * @throws IllegalStateException 해당하는 데이터 수정자가 존재하지 않으면 발생
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public <V extends T> V getOrThrow(@NonNull U element) {
        ResourceLocation resourceLocation = builtInRegistry.getKey(element);
        Validate.validState(resourceLocation != null);

        return (V) vpRegistry.getValueOrThrow(resourceLocation.getPath());
    }
}
