package com.dace.vanillaplus.mixin.world.level.block.state.properties;

import com.dace.vanillaplus.extension.world.level.block.state.properties.VPPotentSulfurState;
import lombok.NonNull;
import net.minecraft.world.level.block.state.properties.PotentSulfurState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Mixin(PotentSulfurState.class)
public abstract class PotentSulfurStateMixin implements VPPotentSulfurState {
    @Mutable
    @Shadow
    @Final
    private static PotentSulfurState[] $VALUES;

    @Unique
    private static PotentSulfurState add(@NonNull String enumName, @NonNull String name) {
        ArrayList<PotentSulfurState> values = new ArrayList<>(Arrays.asList(Objects.requireNonNull($VALUES)));
        PotentSulfurState potentSulfurState = init(enumName, values.getLast().ordinal() + 1, name);

        values.add(potentSulfurState);
        $VALUES = values.toArray(new PotentSulfurState[0]);

        return potentSulfurState;
    }

    @Invoker("<init>")
    private static PotentSulfurState init(String enumName, int ordinal, String name) {
        throw new UnsupportedOperationException();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void clinit(CallbackInfo ci) {
        BURNING.setValue(add("BURNING", "burning"));
    }
}
