package malte0811.ferritecore.modules.sword;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

// AimAssist — ported from MacePvP: smooth rotation toward the target while u hold
// attack. rate-limited (max degrees/tick + strength lerp) so it reads like a hand.
public class AimAssistModule extends ToggleModule {
    @Override public String getId() { return "aimassist"; }
    @Override public String getName() { return "AimAssist"; }

    public float strength = 0.25f;
    public float maxTurn = 20.0f;
    public float range = 4.0f;

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        MinecraftClient mc = FerriteCore.mc;
        if (mc.player == null) return;
        if (!mc.options.attackKey.isPressed()) return;
        if (!(mc.crosshairTarget instanceof EntityHitResult hit)) return;
        if (!(hit.getEntity() instanceof LivingEntity t) || !t.isAlive()) return;
        if (mc.player.distanceTo(t) > range) return;

        Vec3d eye = mc.player.getEyePos();
        Vec3d aim = t.getEyePos();
        double dx = aim.x - eye.x, dy = aim.y - eye.y, dz = aim.z - eye.z;

        double wantYaw = MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dz, dx)) - 90.0 - mc.player.getYaw());
        double flat = Math.sqrt(dx * dx + dz * dz);
        double wantPitch = MathHelper.wrapDegrees(-Math.toDegrees(Math.atan2(dy, flat)) - mc.player.getPitch());

        float max = maxTurn;
        mc.player.setYaw(mc.player.getYaw() + (float) MathHelper.clamp(wantYaw * strength, -max, max));
        mc.player.setPitch(MathHelper.clamp(mc.player.getPitch() + (float) MathHelper.clamp(wantPitch * strength, -max, max), -90.0f, 90.0f));
    }

    @Override
    public List<Option> getOptions() {
        return List.of(
            new RangeOption("Strength", 0.05f, 0.60f, 0.01f, () -> strength, v -> strength = v),
            new RangeOption("MaxTurn/Tick", 5.0f, 45.0f, 1.0f, () -> maxTurn, v -> maxTurn = v),
            new RangeOption("Range", 2.0f, 6.0f, 0.5f, () -> range, v -> range = v)
        );
    }
}
