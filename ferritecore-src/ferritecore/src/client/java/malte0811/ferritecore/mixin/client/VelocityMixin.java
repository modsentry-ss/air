package malte0811.ferritecore.mixin.client;

import malte0811.ferritecore.modules.sword.VelocityModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Velocity — ported from MacePvP: cancels the server's knockback packet for ur
// own player. gated on the Velocity module's static mirror.
@Mixin(ClientPlayNetworkHandler.class)
public class VelocityMixin {
    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
    private void ferritecore$noKb(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        if (!VelocityModule.active) return;
        ClientPlayerEntity p = MinecraftClient.getInstance().player;
        if (p != null && packet.getEntityId() == p.getId()) {
            ci.cancel();
        }
    }
}
