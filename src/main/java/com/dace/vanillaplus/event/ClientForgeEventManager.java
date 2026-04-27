package com.dace.vanillaplus.event;

import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.dace.vanillaplus.extension.client.VPOptions;
import com.dace.vanillaplus.extension.world.entity.player.VPPlayer;
import com.dace.vanillaplus.network.NetworkManager;
import com.dace.vanillaplus.network.server.PronePacket;
import com.dace.vanillaplus.world.block.entity.WaterCauldronBlockEntity;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * 클라이언트 전용 Forge 이벤트를 처리하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientForgeEventManager {
    @SubscribeEvent
    private static void onRegisterKeyMappings(@NonNull RegisterKeyMappingsEvent event) {
        VPOptions vpOptions = VPOptions.cast(Minecraft.getInstance().options);
        event.register(vpOptions.getKeyProne());
    }

    @SubscribeEvent
    private static void onMovementInputUpdate(@NonNull MovementInputUpdateEvent event) {
        VPOptions vpOptions = VPOptions.cast(Minecraft.getInstance().options);

        VPPlayer.cast(event.getEntity()).setProneKeyDown(vpOptions.getKeyProne().isDown());
        NetworkManager.sendToServer(new PronePacket(vpOptions.getKeyProne().isDown()));
    }

    @SubscribeEvent
    private static void onViewportRenderFog(@NonNull ViewportEvent.RenderFog event) {
        if (!(event.getCamera().entity() instanceof LivingEntity livingEntity))
            return;

        float fogDistance = (float) livingEntity.getAttributeValue(VPAttributes.FOG_DISTANCE.getHolder().orElseThrow());
        event.getData().environmentalStart *= fogDistance;
        event.getData().environmentalEnd *= fogDistance;
    }

    @SubscribeEvent
    private static void onRegisterColorHandlersBlock(@NonNull RegisterColorHandlersEvent.Block event) {
        event.register(List.of(new BlockTintSource() {
            @Override
            public int color(@NonNull BlockState state) {
                return -1;
            }

            @Override
            public int colorInWorld(@NonNull BlockState state, @NonNull BlockAndTintGetter level, @NonNull BlockPos pos) {
                int averageWaterColor = BiomeColors.getAverageWaterColor(level, pos);

                return level.getBlockEntity(pos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity
                        ? WaterCauldronBlockEntity.getMixedColor(averageWaterColor, waterCauldronBlockEntity.getColor(), 0.5F)
                        : averageWaterColor;
            }
        }), Blocks.WATER_CAULDRON);
    }
}
