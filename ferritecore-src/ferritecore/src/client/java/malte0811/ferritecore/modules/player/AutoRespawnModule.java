package malte0811.ferritecore.modules.player;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.gui.screen.DeathScreen;

// AutoRespawn — ported from MacePvP: skips the death screen, back in the fight
// instantly.
public class AutoRespawnModule extends ToggleModule {
    @Override public String getId() { return "autorespawn"; }
    @Override public String getName() { return "AutoRespawn"; }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        var mc = FerriteCore.mc;
        if (mc.player == null) return;
        if (mc.player.isDead() && mc.currentScreen instanceof DeathScreen) {
            mc.player.requestRespawn();
            mc.setScreen(null);
        }
    }
}
