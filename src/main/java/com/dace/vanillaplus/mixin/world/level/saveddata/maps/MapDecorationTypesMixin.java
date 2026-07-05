package com.dace.vanillaplus.mixin.world.level.saveddata.maps;

import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.Holder;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(MapDecorationTypes.class)
public abstract class MapDecorationTypesMixin implements VPMixin<MapDecorationTypes> {
    @Shadow
    private static Holder<MapDecorationType> register(String name, String assetName, boolean showOnItemFrame, int mapColor, boolean trackCount,
                                                      boolean explorationMapElement) {
        throw new UnsupportedOperationException();
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/saveddata/maps/MapDecorationTypes;register(Ljava/lang/String;Ljava/lang/String;ZZ)Lnet/minecraft/core/Holder;",
            ordinal = 0), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=red_x")))
    private static Holder<MapDecorationType> registerRedX(String name, String assetName, boolean showOnItemFrame, boolean trackCount) {
        return register(name, assetName, showOnItemFrame, ARGB.color(219, 41, 41), trackCount, false);
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/material/MapColor;col:I", ordinal = 0,
            opcode = Opcodes.GETFIELD))
    private static int modifyDesertVillageColor(int color) {
        return ARGB.color(190, 173, 65);
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/material/MapColor;col:I", ordinal = 1,
            opcode = Opcodes.GETFIELD))
    private static int modifyPlainsVillageColor(int color) {
        return ARGB.color(55, 172, 47);
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/material/MapColor;col:I", ordinal = 2,
            opcode = Opcodes.GETFIELD))
    private static int modifySavannaVillageColor(int color) {
        return ARGB.color(188, 127, 41);
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/material/MapColor;col:I", ordinal = 3,
            opcode = Opcodes.GETFIELD))
    private static int modifySnowyVillageColor(int color) {
        return ARGB.color(156, 205, 206);
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/material/MapColor;col:I", ordinal = 4,
            opcode = Opcodes.GETFIELD))
    private static int modifyTaigaVillageColor(int color) {
        return ARGB.color(71, 169, 144);
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/material/MapColor;col:I", ordinal = 5,
            opcode = Opcodes.GETFIELD))
    private static int modifyJungleTempleColor(int color) {
        return ARGB.color(87, 122, 72);
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/material/MapColor;col:I", ordinal = 6,
            opcode = Opcodes.GETFIELD))
    private static int modifySwampHutColor(int color) {
        return ARGB.color(44, 94, 71);
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "CONSTANT", args = "intValue=5393476"))
    private static int modifyWoodlandMansionColor(int color) {
        return ARGB.color(101, 71, 37);
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "CONSTANT", args = "intValue=3830373"))
    private static int modifyOceanMonumentColor(int color) {
        return ARGB.color(10, 70, 148);
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "CONSTANT", args = "intValue=12741452"))
    private static int modifyTrialChambersColor(int color) {
        return ARGB.color(221, 107, 14);
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "CONSTANT", args = "intValue=12741452"))
    private static int modifyRedXColor(int color) {
        return ARGB.color(221, 107, 14);
    }
}
