package com.dace.vanillaplus.rebalance.modifier;

import com.dace.vanillaplus.VPRegistries;
import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.custom.CustomModifiableData;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 레지스트리 별 데이터 수정자({@link DataModifier})를 관리하는 클래스.
 *
 * @see DataModifier
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DataModifiers {
    /** 서버 시작 시 실행할 작업 목록 */
    private static final List<Consumer<RegistryAccess>> TASKS = new ArrayList<>();

    static {
        create(VPRegistries.ITEM_MODIFIER, BuiltInRegistries.ITEM);
        create(VPRegistries.BLOCK_MODIFIER, BuiltInRegistries.BLOCK);
        create(VPRegistries.ENTITY_MODIFIER, BuiltInRegistries.ENTITY_TYPE);
    }

    @SuppressWarnings("unchecked")
    private static <T, U extends DataModifier<T>> void create(@NonNull VPRegistries.VPRegistry<U> vpRegistry, @NonNull DefaultedRegistry<T> registry) {
        TASKS.add(registryAccess -> registry.forEach(element -> {
            ResourceKey<U> dataModifierResourceKey = vpRegistry.createResourceKey(registry.getKey(element).getPath());

            registryAccess.get(dataModifierResourceKey).ifPresent(reference ->
                    ((CustomModifiableData<T, U>) element).setDataModifier(reference.value()));
        }));
    }

    @SubscribeEvent
    private static void onServerAboutToStart(@NonNull ServerAboutToStartEvent event) {
        RegistryAccess registryAccess = event.getServer().registryAccess();
        TASKS.forEach(v -> v.accept(registryAccess));
    }
}
