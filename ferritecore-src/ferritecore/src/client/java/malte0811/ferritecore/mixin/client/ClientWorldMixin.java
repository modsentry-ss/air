package malte0811.ferritecore.mixin.client;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.modules.client.HUDModule;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(at = @At("HEAD"), method = "tick")
    public void onTick(CallbackInfo ci) {
        if (FerriteCore.destructed) return;
        FerriteCore.TICKS++;

        // Track TPS for HUD display
        HUDModule.onGameTick();

        if (FerriteCore.mc.targetedEntity instanceof LivingEntity crosshair)
            FerriteCore.target = crosshair;
        else
            FerriteCore.target = null;

        if (FerriteCore.mc.player == null
                || FerriteCore.mc.player.isBlocking() || FerriteCore.mc.player.isUsingItem()
                || FerriteCore.mc.currentScreen instanceof HandledScreen<?>) {
            return;
        }

        FerriteCore.moduleManager.onTick();
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void onTickEnd(CallbackInfo ci) {
        if (FerriteCore.destructed) return;

        FerriteCore.tickEndCallbacks.forEach((neededTick, runnable) -> {
//            System.out.println("Current tick:" + Flashback.TICKS + " Needed tick:" + neededTick);
            if (neededTick <= FerriteCore.TICKS) {
//                System.out.println("Running Sodium debugging runnable at:" + neededTick);
                runnable.run();
                FerriteCore.tickEndCallbacks.remove(neededTick);
            }
        });
    }
}
