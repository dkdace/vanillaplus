package com.dace.vanillaplus;

import com.dace.vanillaplus.util.ReflectionUtil;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * 모드 메인 클래스.
 */
@Mod(VanillaPlus.MODID)
public final class VanillaPlus {
    public static final String MODID = "vanillaplus";
    private static final Logger LOGGER = LogUtils.getLogger();

    public VanillaPlus(FMLJavaModLoadingContext context) {
        LOGGER.debug("Hello, World!");

        ReflectionUtil.loadClass(Tag.class);
    }
}
