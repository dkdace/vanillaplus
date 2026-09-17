package com.dace.vanillaplus.event;

import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.dace.vanillaplus.data.registryobject.VPParticleTypes;
import com.dace.vanillaplus.extension.client.VPOptions;
import com.dace.vanillaplus.extension.client.particle.VPNoxiousGasCloudParticle;
import com.dace.vanillaplus.extension.world.entity.player.VPPlayer;
import com.dace.vanillaplus.network.NetworkManager;
import com.dace.vanillaplus.network.server.PronePacket;
import com.dace.vanillaplus.world.block.entity.WaterCauldronBlockEntity;
import com.mojang.datafixers.util.Either;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.particle.NoxiousGasCloudParticle;
import net.minecraft.client.particle.NoxiousGasParticle;
import net.minecraft.client.particle.WhiteAshParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
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
    private static void onRenderTooltipGatherComponents(@NonNull RenderTooltipEvent.GatherComponents event) {
        Options options = Minecraft.getInstance().options;
        VPOptions vpOptions = VPOptions.cast(options);
        event.setMaxWidth((int) (event.getScreenWidth() * vpOptions.getMaxItemTooltipWidth().get()));

        if (!vpOptions.getItemDescription().get())
            return;

        String key = event.getItemStack().getItem().getDescriptionId() + ".description";
        String value = Language.getInstance().getLanguageData().get(key);
        if (value == null)
            return;

        List<Either<FormattedText, TooltipComponent>> tooltipElements = event.getTooltipElements();
        if (tooltipElements.size() > (options.advancedItemTooltips ? 3 : 1)
                && tooltipElements.get(1).map(targetLine -> !targetLine.equals(Component.empty()), _ -> true))
            tooltipElements.add(1, Either.left(Component.empty()));

        String[] lines = value.split("\n");
        for (int i = 0; i < lines.length; i++)
            tooltipElements.add(i + 1, Either.left(Component.literal(lines[i]).withStyle(ChatFormatting.GRAY)));
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
                        ? waterCauldronBlockEntity.getWaterColor(averageWaterColor)
                        : averageWaterColor;
            }
        }), Blocks.WATER_CAULDRON);
    }

    @SubscribeEvent
    private static void onRegisterParticleProviders(@NonNull RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(VPParticleTypes.SULFUR_ASH.get(), spriteSet ->
                (_, level, x, y, z, _, _, _, randomSource) -> {
                    double xAux = randomSource.nextDouble() * -1.9 * randomSource.nextDouble() * 0.1;
                    double yAux = randomSource.nextDouble() * -0.5 * randomSource.nextDouble() * 0.1 * 5;
                    double zAux = randomSource.nextDouble() * -1.9 * randomSource.nextDouble() * 0.1;

                    WhiteAshParticle particle = new WhiteAshParticle(level, x, y, z, xAux, yAux, zAux, 1, spriteSet);
                    particle.setColor(0.54F, 0.54F, 0.44F);

                    return particle;
                });
        event.registerSpriteSet(VPParticleTypes.NOXIOUS_GAS_BURNING.get(), spriteSet ->
                (_, level, x, y, z, xAux, yAux, zAux, randomSource) -> {
                    NoxiousGasParticle particle = new NoxiousGasParticle(level, x, y, z, xAux, yAux, zAux, 4.5F, spriteSet);
                    particle.setLifetime(randomSource.nextInt(20) + 30);
                    particle.setParticleSpeed(0, 0.08 + randomSource.nextDouble() * 0.04, 0);
                    particle.fadeOutStartingPoint = 0;

                    return particle;
                });
        event.registerSpecial(VPParticleTypes.NOXIOUS_GAS_CLOUD_BURNING.get(),
                (_, level, x, y, z, _, _, _, _) -> {
                    NoxiousGasCloudParticle particle = new NoxiousGasCloudParticle(level, x, y, z);
                    VPNoxiousGasCloudParticle.cast(particle).setBurning(true);

                    return particle;
                });
    }
}
