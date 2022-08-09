package net.fabricmc.quietfishing.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMix {
    @Shadow
    @Final
    private MinecraftClient client;

    @Overwrite
    public void onPlaySound(PlaySoundS2CPacket packet) {
        ClientPlayNetworkHandler this1 = (ClientPlayNetworkHandler) (Object) this;
        NetworkThreadUtils.forceMainThread(packet, this1, client);
        if (client.world == null) return;

        if (packet.getSound() == SoundEvents.ENTITY_FISHING_BOBBER_SPLASH) {
            List<FishingBobberEntity> list = client.world.getEntitiesByType(EntityType.FISHING_BOBBER, new Box(new BlockPos(packet.getX(), packet.getY(), packet.getZ())).expand(0.25f), Entity::isAlive);

            if (list.isEmpty()) return;

            for (FishingBobberEntity bobberEntity : list) {
                if (!(bobberEntity.getPlayerOwner() instanceof OtherClientPlayerEntity)) {
                    client.world.playSound(this.client.player, packet.getX(), packet.getY(), packet.getZ(), packet.getSound(), packet.getCategory(), packet.getVolume(), packet.getPitch());
                    return;
                }
            }
            return;
        }

        client.world.playSound(this.client.player, packet.getX(), packet.getY(), packet.getZ(), packet.getSound(), packet.getCategory(), packet.getVolume(), packet.getPitch());
    }


}
