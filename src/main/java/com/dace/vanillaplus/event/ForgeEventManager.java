package com.dace.vanillaplus.event;

import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.dace.vanillaplus.data.registryobject.VPGameRules;
import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.mutable.MutableFloat;

/**
 * 클라이언트 및 서버의 Forge 이벤트를 처리하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeEventManager {
    @SubscribeEvent
    private static void onLivingHeal(@NonNull LivingHealEvent event) {
        event.setAmount((float) (event.getAmount() * event.getEntity().getAttributeValue(VPAttributes.HEAL_MULTIPLIER.getHolder().orElseThrow())));
    }

    @SubscribeEvent
    private static void onLivingVisibility(@NonNull LivingEvent.LivingVisibilityEvent event) {
        Entity targetEntity = event.getLookingEntity();
        if (targetEntity == null)
            return;

        LivingEntity entity = event.getEntity();
        MutableFloat value = new MutableFloat(1);

        EnchantmentHelper.runIterationOnEquipment(entity, (enchantmentHolder, level, _) ->
                VPEnchantment.cast(enchantmentHolder.value()).modifyMobVisibilityMultiplier((ServerLevel) entity.level(), level,
                        entity, targetEntity, value));

        event.modifyVisibility(value.floatValue());
    }

    @SubscribeEvent
    private static void onLivingDamage(@NonNull LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        EnchantmentHelper.runIterationOnEquipment(entity, (enchantmentHolder, level, enchantedItemInUse) ->
                VPEnchantment.cast(enchantmentHolder.value()).runPostDamageEffects((ServerLevel) entity.level(), level, enchantedItemInUse, entity,
                        event.getSource()));
    }

    @SubscribeEvent
    private static void onMobSpawnAllowDespawn(@NonNull MobSpawnEvent.AllowDespawn event) {
        ServerLevel level = event.getLevel().getLevel();
        Mob entity = event.getEntity();

        if (!(entity instanceof Endermite) && !level.getGameRules().get(VPGameRules.SPAWN_MOBS_IN_ENDER_DRAGON_FIGHT.get())
                && level.getBiome(entity.blockPosition()).is(Biomes.THE_END)
                && !level.getEntities(EntityType.ENDER_DRAGON, _ -> true).isEmpty())
            event.setResult(Result.ALLOW);
    }
}
