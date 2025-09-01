package com.dace.vanillaplus.input.key;

import com.dace.vanillaplus.custom.CustomPlayer;
import com.dace.vanillaplus.network.NetworkManager;
import com.dace.vanillaplus.network.packet.PronePacketHandler;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/**
 * 엎드리기 키 클래스.
 */
@OnlyIn(Dist.CLIENT)
public final class ProneKey extends ToggleKeyMapping {
    @Getter
    public static final ProneKey instance = new ProneKey();

    private ProneKey() {
        super("key.prone", GLFW.GLFW_KEY_LEFT_ALT, "key.categories.movement", () -> false);
    }

    @Override
    @NonNull
    public IKeyConflictContext getKeyConflictContext() {
        return KeyConflictContext.IN_GAME;
    }

    @Override
    public void setDown(boolean isDown) {
        super.setDown(isDown);

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || minecraft.getConnection() == null)
            return;

        ((CustomPlayer) player).setProneKeyDown(isDown);
        NetworkManager.sendToServer(new PronePacketHandler(isDown));
    }
}
