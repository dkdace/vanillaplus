package com.dace.vanillaplus.mixin.world.level.saveddata.maps;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MapDecorationTypes.class)
public abstract class MapDecorationTypesMixin implements VPMixin<MapDecorationTypes> {

}
