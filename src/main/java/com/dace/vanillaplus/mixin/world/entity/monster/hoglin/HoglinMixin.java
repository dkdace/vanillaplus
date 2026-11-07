package com.dace.vanillaplus.mixin.world.entity.monster.hoglin;

import com.dace.vanillaplus.data.VPTags;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.item.Item;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Hoglin.class)
public abstract class HoglinMixin extends MobMixin<Hoglin> {
    @ModifyExpressionValue(method = "isFood", at = @At(value = "FIELD",
            target = "Lnet/minecraft/tags/ItemTags;HOGLIN_FOOD:Lnet/minecraft/tags/TagKey;", opcode = Opcodes.GETSTATIC))
    private TagKey<Item> modifyFood(TagKey<Item> itemTagKey) {
        return isBaby() ? VPTags.Items.BABY_HOGLIN_FOOD : itemTagKey;
    }
}
