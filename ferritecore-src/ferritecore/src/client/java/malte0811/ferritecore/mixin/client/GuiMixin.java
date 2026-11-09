package malte0811.ferritecore.mixin.client;

import net.minecraft.client.gui.DrawContext;
import malte0811.ferritecore.FerriteCore;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class GuiMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void render(DrawContext ctx, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (FerriteCore.destructed) return;

        FerriteCore.moduleManager.getAllModules().forEach(mod -> {
            mod.onRender(ctx);
        });
    }
}
