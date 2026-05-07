package com.dace.vanillaplus.mixin.world.entity.monster.piglin;

import com.dace.vanillaplus.data.registryobject.EntityModifierComponentTypes;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.entity.VPEntity;
import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import com.dace.vanillaplus.world.entity.modifier.component.CrossbowMobInfo;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin implements VPMixin<PiglinAi> {
    @WrapOperation(method = "getBarterResponseItems", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"))
    private static ObjectArrayList<ItemStack> modifyBarteringResult(LootTable instance, LootParams params,
                                                                    Operation<ObjectArrayList<ItemStack>> original,
                                                                    @Local(argsOnly = true) Piglin body) {
        Player player = body.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).orElse(null);
        if (player == null)
            return original.call(instance, params);

        MutableFloat value = new MutableFloat(1);

        EnchantmentHelper.runIterationOnEquipment(player, (enchantmentHolder, level, enchantedItemInUse) ->
                VPEnchantment.cast(enchantmentHolder.value()).modifyBarteringRolls((ServerLevel) player.level(), level, enchantedItemInUse.itemStack(),
                        player, value));

        ObjectArrayList<ItemStack> itemStacks = new ObjectArrayList<>();

        int rolls = value.intValue();
        if (body.getRandom().nextFloat() < value.floatValue() - rolls)
            rolls++;

        for (int i = 0; i < rolls; i++)
            itemStacks.addAll(original.call(instance, params));

        return itemStacks;
    }

    @ModifyArg(method = "initFightActivity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/behavior/BackUpIfTooClose;create(IF)Lnet/minecraft/world/entity/ai/behavior/OneShot;"),
            index = 0)
    private static int modifyBackupDistance(int tooCloseDistance, @Local(argsOnly = true) Piglin body) {
        return VPEntity.cast(body).getDataModifier().getComponents().get(EntityModifierComponentTypes.CROSSBOW_ATTACK_MOB)
                .map(CrossbowMobInfo::backupDistance)
                .orElse(tooCloseDistance);
    }
}
