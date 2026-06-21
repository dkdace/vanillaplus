package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.world.item.FireChargeConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireChargeItem.class)
public abstract class FireChargeItemMixin extends ItemMixin<FireChargeItem> {
    @Unique
    private static final float PROJECTILE_SHOOT_POWER = 1.5F;

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (!FireChargeConfig.get().enableThrowing())
            return super.use(level, player, interactionHand);

        ItemStack itemstack = player.getItemInHand(interactionHand);

        if (level instanceof ServerLevel serverlevel)
            Projectile.spawnProjectileFromRotation((serverLevel, _, _) -> {
                SmallFireball smallFireball = new SmallFireball(serverLevel, player, Vec3.ZERO);
                smallFireball.setPos(player.getEyePosition());

                return smallFireball;
            }, serverlevel, itemstack, player, 0, PROJECTILE_SHOOT_POWER, 1);

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 1,
                (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1);
        player.awardStat(Stats.ITEM_USED.get(getThis()));
        itemstack.consume(1, player);

        return InteractionResult.SUCCESS;
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    public void cancelDefaultUse(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (FireChargeConfig.get().enableThrowing())
            cir.setReturnValue(InteractionResult.PASS);
    }
}
