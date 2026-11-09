package malte0811.ferritecore.mixin.client;

import malte0811.ferritecore.modules.render.FullbrightModule;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Fullbright — ported from MacePvP: forces max night-vision strength while the
// module is on.
@Mixin(GameRenderer.class)
public class FullbrightMixin {
    @Inject(method = "getNightVisionStrength", at = @At("HEAD"), cancellable = true)
    private static void ferritecore$fullbright(LivingEntity entity, float f, CallbackInfoReturnable<Float> cir) {
        if (FullbrightModule.active) cir.setReturnValue(1.0f);
    }
}
