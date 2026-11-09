package malte0811.ferritecore.modules.movement;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.util.Hand;

// AntiAFK — ported from MacePvP: if u sit idle for a while it drifts ur yaw n
// swings so afk kick timers dont eat u while u scroll twitter between fights.
public class AntiAFKModule extends ToggleModule {
    @Override public String getId() { return "antiafk"; }
    @Override public String getName() { return "AntiAFK"; }

    private int idle = 0;

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        var mc = FerriteCore.mc;
        if (mc.player == null) return;

        boolean active = mc.options.forwardKey.isPressed() || mc.options.backKey.isPressed()
                || mc.options.leftKey.isPressed() || mc.options.rightKey.isPressed()
                || mc.options.jumpKey.isPressed() || mc.options.attackKey.isPressed();
        if (active) { idle = 0; return; }

        idle++;
        if (idle > 20 * 20 && idle % 60 == 0) {
            mc.player.setYaw(mc.player.getYaw() + (Math.random() < 0.5 ? -4.5f : 4.5f));
            mc.player.swingHand(Hand.OFF_HAND);
        }
    }
}
