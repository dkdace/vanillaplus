package com.dace.vanillaplus.mixin;

import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Blocks.class)
public final class BlocksMixin {
    @ModifyArgs(method = "lambda$static$224", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/valueproviders/UniformInt;of(II)Lnet/minecraft/util/valueproviders/UniformInt;"))
    private static void modifyQuartzOreDropXP(Args args) {
        args.set(0, 1);
        args.set(1, 3);
    }
}
