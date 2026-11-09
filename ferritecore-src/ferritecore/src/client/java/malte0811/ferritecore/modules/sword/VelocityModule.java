package malte0811.ferritecore.modules.sword;

import malte0811.ferritecore.modules.ToggleModule;

// Velocity — no tick. VelocityMixin (on ClientPlayNetworkHandler.onEntityVelocityUpdate)
// reads the static mirror and cancels the server's knockback packet for ur own player.
public class VelocityModule extends ToggleModule {
    @Override public String getId() { return "velocity"; }
    @Override public String getName() { return "Velocity"; }

    public static volatile boolean active = false;

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        active = enabled;
    }
}
