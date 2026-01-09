package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.modifier.ItemModifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FireChargeItem.class)
public abstract class FireChargeItemMixin extends ItemMixin<FireChargeItem, ItemModifier> {
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);

        if (level instanceof ServerLevel serverlevel)
            Projectile.spawnProjectileFromRotation((serverLevel, livingEntity, itemStack) -> {
                SmallFireball smallFireball = new SmallFireball(serverLevel, player, Vec3.ZERO);
                smallFireball.setPos(player.getEyePosition());

                return smallFireball;
            }, serverlevel, itemstack, player, 0, 1.5F, 1);

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 1,
                (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1);
        player.awardStat(Stats.ITEM_USED.get(getThis()));
        itemstack.consume(1, player);

        return InteractionResult.SUCCESS;
    }

    @Overwrite
    public InteractionResult useOn(UseOnContext useOnContext) {
        return InteractionResult.PASS;
    }
}
