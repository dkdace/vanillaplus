package com.dace.vanillaplus;

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
    /** 모드 ID */
    public static final String MODID = "vanillaplus";
    /** 로거 인스턴스 */
    public static final Logger LOGGER = LogUtils.getLogger();

    @Getter
    private static VanillaPlus instance;
    private final BusGroup busGroup;

    public VanillaPlus(@NonNull FMLJavaModLoadingContext context) {
        LOGGER.debug("VanillaPlus Loaded");

        instance = this;
        busGroup = context.getModBusGroup();

        ReflectionUtil.loadClass(VPGameRules.class);
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
