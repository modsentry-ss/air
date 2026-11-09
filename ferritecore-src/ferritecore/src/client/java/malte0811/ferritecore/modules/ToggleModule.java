package malte0811.ferritecore.modules;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public abstract class ToggleModule implements Module {
    private boolean enabled = false;
    private int bind = GLFW.GLFW_KEY_UNKNOWN;

    @Override public int getBind() { return bind; }
    @Override public void setBind(int bind) { this.bind = bind; }

    @Override public boolean isEnabled() { return enabled; }
    @Override public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @Override public void onRender(DrawContext ctx) { }
    @Override public void onTick() { }
    @Override public void onAttack(PlayerEntity attacker, World world, Entity victim, @Nullable EntityHitResult hitResult) { }
}
