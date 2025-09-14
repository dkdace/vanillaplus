package com.dace.vanillaplus;

import com.dace.vanillaplus.rebalance.trade.StructureMap;
import com.dace.vanillaplus.rebalance.trade.Trade;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;

/**
 * 모드에서 사용하는 레지스트리를 관리하는 클래스.
 */
@UtilityClass
public final class VPRegistries {
    /** 효과음 이벤트 */
    public static final VPRegistry<SoundEvent> SOUND_EVENT = new VPRegistry<>(Registries.SOUND_EVENT);
    /** 주민 거래 정보 */
    public static final VPRegistry<Trade> TRADE = new VPRegistry<>("trade");
    /** 구조물 지도 */
    public static final VPRegistry<StructureMap> STRUCTURE_MAP = new VPRegistry<>("structure_map");

    /**
     * 레지스트리 정보를 관리하는 클래스.
     *
     * @param <T> 레지스트리 데이터 타입
     */
    @Getter
    public static final class VPRegistry<T> {
        /** 레지스트리 리소스 키 */
        @NonNull
        private final ResourceKey<Registry<T>> registryKey;
        /** DeferredRegister 인스턴스 */
        @NonNull
        private final DeferredRegister<T> deferredRegister;

        private VPRegistry(@NonNull ResourceKey<Registry<T>> registryKey) {
            this.registryKey = registryKey;
            this.deferredRegister = DeferredRegister.create(registryKey, VanillaPlus.MODID);

            VanillaPlus.getInstance().registerBusGroup(deferredRegister);
        }

        private VPRegistry(@NonNull String name) {
            this(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(VanillaPlus.MODID, name)));
            deferredRegister.makeRegistry(RegistryBuilder::of);
        }

        /**
         * 레지스트리의 하위 요소 리소스 키를 생성한다.
         *
         * @param name 이름
         * @return 리소스 키
         */
        @NonNull
        public ResourceKey<T> createResourceKey(@NonNull String name) {
            return ResourceKey.create(registryKey, ResourceLocation.fromNamespaceAndPath(VanillaPlus.MODID, name));
        }
    }
}
