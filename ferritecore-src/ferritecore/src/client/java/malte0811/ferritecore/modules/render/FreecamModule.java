package malte0811.ferritecore.modules.render;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.gui.controls.options.ToggleOption;
import malte0811.ferritecore.mixin.client.KeyBindingAccessor;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class FreecamModule extends ToggleModule {
    @Override public String getId() { return "freecam"; }
    @Override public String getName() { return "Freecam"; }

    // Settings
    private float speed = 1.0f;
    private boolean renderLine = true;

    // State
    private Vec3d prevPos = Vec3d.ZERO;
    private Vec3d currentPos = Vec3d.ZERO;

    @Override
    public List<Option> getOptions() {
        return List.of(
            new RangeOption("speed", "Speed", 0.1f, 10.0f, 0.1f,
                () -> speed, v -> speed = v),
            new ToggleOption("render_line", this, () -> renderLine, v -> renderLine = v, "Render Line")
        );
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            onEnable();
        }
    }

    private void onEnable() {
        MinecraftClient mc = FerriteCore.mc;
        if (mc.player != null) {
            this.prevPos = this.currentPos = mc.player.getEyePos();
        }
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;

        MinecraftClient mc = FerriteCore.mc;
        if (mc.player == null || mc.world == null) return;

        // Disable player movement by setting keys to false
        mc.options.forwardKey.setPressed(false);
        mc.options.backKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
        mc.options.sneakKey.setPressed(false);

        // Calculate movement vectors based on player yaw
        float f = (float) Math.PI / 180;
        float f2 = (float) Math.PI;

        Vec3d forward = new Vec3d(
            -MathHelper.sin(-mc.player.getYaw() * f - f2),
            0.0,
            -MathHelper.cos(-mc.player.getYaw() * f - f2)
        );
        Vec3d up = new Vec3d(0.0, 1.0, 0.0);
        Vec3d left = up.crossProduct(forward);
        Vec3d right = forward.crossProduct(up);

        Vec3d movement = Vec3d.ZERO;

        // Check movement keys
        if (isKeyPressed(mc.options.forwardKey)) {
            movement = movement.add(forward);
        }
        if (isKeyPressed(mc.options.backKey)) {
            movement = movement.subtract(forward);
        }
        if (isKeyPressed(mc.options.leftKey)) {
            movement = movement.add(left);
        }
        if (isKeyPressed(mc.options.rightKey)) {
            movement = movement.add(right);
        }
        if (isKeyPressed(mc.options.jumpKey)) {
            movement = movement.add(0.0, speed, 0.0);
        }
        if (isKeyPressed(mc.options.sneakKey)) {
            movement = movement.add(0.0, -speed, 0.0);
        }

        // Apply sprint multiplier
        float finalSpeed = speed;
        if (isKeyPressed(mc.options.sprintKey)) {
            finalSpeed *= 2;
        }

        movement = movement.normalize().multiply(finalSpeed);

        // Update positions
        prevPos = currentPos;
        currentPos = currentPos.add(movement);
    }

    private boolean isKeyPressed(KeyBinding keyBinding) {
        MinecraftClient mc = FerriteCore.mc;
        int keyCode = ((KeyBindingAccessor) keyBinding).getBoundKey().getCode();
        return GLFW.glfwGetKey(mc.getWindow().getHandle(), keyCode) == GLFW.GLFW_PRESS;
    }

    public Vec3d getPrevPos() {
        return prevPos;
    }

    public Vec3d getCurrentPos() {
        return currentPos;
    }

    public boolean shouldRenderLine() {
        return renderLine;
    }
}

