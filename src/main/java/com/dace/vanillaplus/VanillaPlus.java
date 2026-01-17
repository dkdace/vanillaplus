package com.dace.vanillaplus;

import com.mojang.logging.LogUtils;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.resources.Identifier;
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
    }

    /**
     * 지정한 클래스를 {@link Class#forName(String)}을 이용하여 불러온다.
     *
     * <p>static initializer를 실행하기 위해 사용한다.</p>
     *
     * @param clazz 클래스
     */
    public static void loadClass(@NonNull Class<?> clazz) {
        try {
            Class.forName(clazz.getName());
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 모드의 식별자를 생성한다.
     *
     * @param path 리소스 경로
     * @return 식별자
     */
    @NonNull
    public static Identifier createIdentifier(@NonNull String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
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
