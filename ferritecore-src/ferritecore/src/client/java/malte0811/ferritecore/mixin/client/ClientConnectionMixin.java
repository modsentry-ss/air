package malte0811.ferritecore.mixin.client;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.Utils;
import malte0811.ferritecore.modules.crystal.CrystalOptimizerModule;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    private void onPacketSend(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof PlayerInteractEntityC2SPacket interactionPacket) {
            if (!CrystalOptimizerModule.IS_ACTIVE) return;

            if (!(FerriteCore.mc.crosshairTarget instanceof EntityHitResult entityHitResult)) {
                return;
            }

            if (!(entityHitResult.getEntity() instanceof EndCrystalEntity entity)) {
                return;
            }

            if (!(FerriteCore.mc.player instanceof ClientPlayerEntity player)) {
                return;
            }

            if (Utils.calculateTotalDamage(player) > 0.0D) {
                Utils.destroyCrystal(entity);
            }
        }
    }
}
