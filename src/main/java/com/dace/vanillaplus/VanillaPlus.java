package com.dace.vanillaplus;

import com.dace.vanillaplus.rebalance.enchantment.VPEnchantmentLevelBasedValueTypes;
import com.dace.vanillaplus.sound.VPSoundEvents;
import com.dace.vanillaplus.util.ReflectionUtil;
import com.mojang.logging.LogUtils;
import lombok.Getter;
import lombok.NonNull;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import org.slf4j.Logger;

/**
 * 모드 메인 클래스.
 */
@Mod(VanillaPlus.MODID)
public final class VanillaPlus {
    public static final String MODID = "vanillaplus";
    private static final Logger LOGGER = LogUtils.getLogger();

    @Getter
    private static VanillaPlus instance;
    private final BusGroup busGroup;

    public VanillaPlus(FMLJavaModLoadingContext context) {
        LOGGER.debug("Hello, World!");

        instance = this;
        busGroup = context.getModBusGroup();

        ReflectionUtil.loadClass(VPRegistries.class);
        ReflectionUtil.loadClass(VPTags.class);
        ReflectionUtil.loadClass(VPSoundEvents.class);
        ReflectionUtil.loadClass(VPAttributes.class);
        ReflectionUtil.loadClass(VPDataComponentTypes.class);
        ReflectionUtil.loadClass(VPEnchantmentLevelBasedValueTypes.class);
    }

    /**
     * DeferredRegister 인스턴스를 등록한다.
     *
     * @param deferredRegister DeferredRegister 인스턴스
     * @param <T>              레지스트리 데이터 타입
     */
    public <T> void registerBusGroup(@NonNull DeferredRegister<T> deferredRegister) {
        deferredRegister.register(busGroup);
    }
}
