package com.dace.vanillaplus.rebalance.modifier;

import com.dace.vanillaplus.VPRegistries;
import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.custom.CustomModifiableData;
import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 레지스트리 별 데이터 수정자({@link DataModifier}) 목록을 관리하는 클래스.
 *
 * @see DataModifier
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DataModifiers {
    /** 서버 시작 시 실행할 작업 목록 */
    private static final List<Consumer<RegistryAccess>> TASKS = new ArrayList<>();

    /** 아이템별 아이템 수정자 목록 (아이템 : 아이템 수정자) */
    public static final ImmutableMap<Item, ResourceKey<ItemModifier>> ITEM_MODIFIER_MAP = create(BuiltInRegistries.ITEM, VPRegistries.ITEM_MODIFIER);
    /** 블록별 아이템 수정자 목록 (블록 : 아이템 수정자) */
    public static final ImmutableMap<Block, ResourceKey<BlockModifier>> BLOCK_MODIFIER_MAP = create(BuiltInRegistries.BLOCK, VPRegistries.BLOCK_MODIFIER);
    /** 엔티티 타입별 엔티티 수정자 목록 (엔티티 타입 : 엔티티 수정자) */
    public static final ImmutableMap<EntityType<?>, ResourceKey<EntityModifier>> ENTITY_MODIFIER_MAP = create(BuiltInRegistries.ENTITY_TYPE, VPRegistries.ENTITY_MODIFIER);

    @NonNull
    @SuppressWarnings("unchecked")
    private static <T, U extends DataModifier<T>> ImmutableMap<T, ResourceKey<U>> create(@NonNull DefaultedRegistry<T> registry,
                                                                                         @NonNull VPRegistries.VPRegistry<U> vpRegistry) {
        ImmutableMap.Builder<T, ResourceKey<U>> builder = ImmutableMap.builder();

        registry.forEach(element -> {
            ResourceKey<U> resourceKey = vpRegistry.createResourceKey(registry.getKey(element).getPath());
            builder.put(element, resourceKey);
        });

        ImmutableMap<T, ResourceKey<U>> map = builder.build();

        TASKS.add(registryAccess -> map.forEach((element, resourceKey) ->
                registryAccess.get(resourceKey).ifPresent(reference -> ((CustomModifiableData<T, U>) element).apply(reference.value()))));

        return map;
    }

    /**
     * 지정한 요소에 해당하는 데이터 수정자를 반환한다.
     *
     * @param registryAccess RegistryAccess 인스턴스
     * @param resourceKeyMap 요소별 데이터 수정자 목록
     * @param element        대상 요소
     * @param <T>            대상 요소의 데이터 타입
     * @param <U>            {@link DataModifier}를 상속받는 데이터 수정자
     * @return 데이터 수정자
     */
    @NonNull
    public static <T, U extends DataModifier<T>> U get(@NonNull RegistryAccess registryAccess, @NonNull ImmutableMap<T, ResourceKey<U>> resourceKeyMap,
                                                       @NonNull T element) {
        return registryAccess.getOrThrow(Objects.requireNonNull(resourceKeyMap.get(element))).value();
    }

    @SubscribeEvent
    private static void onServerAboutToStart(@NonNull ServerAboutToStartEvent event) {
        RegistryAccess registryAccess = event.getServer().registryAccess();
        TASKS.forEach(v -> v.accept(registryAccess));
    }
}
