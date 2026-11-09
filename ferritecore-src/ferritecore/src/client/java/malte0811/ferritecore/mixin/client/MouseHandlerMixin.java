package malte0811.ferritecore.mixin.client;

import malte0811.ferritecore.FerriteCore;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseHandlerMixin {
    @Inject(at = @At("HEAD"), method = "onMouseButton", cancellable = true)
    public void onPress(long window, MouseInput input, int action, CallbackInfo ci) {
        if (FerriteCore.destructed) return;
        var mc = FerriteCore.mc;
        if (mc.player == null) return;

        double trueX = mc.mouse.getX() * (double)mc.getWindow().getScaledWidth() / (double)mc.getWindow().getWidth();
        double trueY = mc.mouse.getY() * (double)mc.getWindow().getScaledHeight() / (double)mc.getWindow().getHeight();

//        FerriteCoreClient.msg("clicked: " + mcWindow + " xy: " + trueX + " " + trueY + " | mouseBtn " + mouseBtn + " | pressed: " + pressed + " | modifiers: " + modifiers, true);

        if (FerriteCore.showUI) {
            FerriteCore.catUI.handleClick(trueX, trueY, input.button(), action);
            // Don't cancel if a screen is open (like pause menu) so clicks pass through
            if (mc.currentScreen == null) {
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "onCursorPos(JDD)V")
    public void onMove(long mcWindow, double x, double y, CallbackInfo ci) {
        if (FerriteCore.destructed) return;
        var mc = FerriteCore.mc;
        if (mc.player == null) return;

        double trueX = mc.mouse.getX() * (double)mc.getWindow().getScaledWidth() / (double)mc.getWindow().getWidth();
        double trueY = mc.mouse.getY() * (double)mc.getWindow().getScaledHeight() / (double)mc.getWindow().getHeight();

        if (FerriteCore.showUI) {
            FerriteCore.catUI.handleMove(trueX, trueY);
        }
    }

    @Inject(at = @At("HEAD"), method = "onMouseScroll", cancellable = true)
    public void onScroll(long mcWindow, double horizontal, double vertical, CallbackInfo ci) {
        if (FerriteCore.destructed) return;
        var mc = FerriteCore.mc;
        if (mc.player == null) return;

        if (FerriteCore.showUI) {
            ci.cancel();
            FerriteCore.catUI.categories.forEach(cat -> {
                cat.originY += ((int) vertical)*5;
                cat.originX += ((int) horizontal)*5;
            });
        }
    }
}
