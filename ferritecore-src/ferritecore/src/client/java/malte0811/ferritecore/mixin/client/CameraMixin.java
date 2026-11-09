package malte0811.ferritecore.mixin.client;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.modules.Module;
import malte0811.ferritecore.modules.render.FreecamModule;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    protected abstract void setPos(Vec3d pos);

    @Inject(method = "update", at = @At("RETURN"))
    private void onCameraUpdate(World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress, CallbackInfo ci) {
        for (Module module : FerriteCore.moduleManager.renderModules) {
            if (module instanceof FreecamModule freecam && freecam.isEnabled()) {
                Vec3d prevPos = freecam.getPrevPos();
                Vec3d currentPos = freecam.getCurrentPos();
                double x = MathHelper.lerp(tickProgress, prevPos.x, currentPos.x);
                double y = MathHelper.lerp(tickProgress, prevPos.y, currentPos.y);
                double z = MathHelper.lerp(tickProgress, prevPos.z, currentPos.z);
                setPos(new Vec3d(x, y, z));
                break;
            }
        }
    }
}

