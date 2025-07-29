package com.dace.vanillaplus;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(VanillaPlus.MODID)
public final class VanillaPlus {
    public static final String MODID = "vanillaplus";
    private static final Logger LOGGER = LogUtils.getLogger();

    public VanillaPlus(FMLJavaModLoadingContext context) {
        LOGGER.debug("Hello, World!");
    }
}
