package com.dace.vanillaplus;

import com.mojang.logging.LogUtils;
import lombok.NonNull;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import org.slf4j.Logger;

/**
 * 모드 메인 클래스.
 */
@Mod(VanillaPlus.MODID)
public final class VanillaPlus {
    /** 모드 ID */
    public static final String MODID = "vanillaplus";
    /** 로거 인스턴스 */
    private static final Logger LOGGER = LogUtils.getLogger();

    public VanillaPlus(@NonNull FMLJavaModLoadingContext context) {
        IModInfo modInfo = context.getContainer().getModInfo();
        LOGGER.info("Starting {} {}", modInfo.getModId(), modInfo.getVersion());

        StaticRegistry.bootstrap(context.getModBusGroup());
        DataPackRegistries.bootstrap();
        ReloadableDataManager.bootstrap();
    }
}
