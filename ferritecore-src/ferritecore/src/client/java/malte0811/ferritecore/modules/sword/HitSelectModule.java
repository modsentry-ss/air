package malte0811.ferritecore.modules.sword;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.entity.player.PlayerEntity;

// HitSelect — no tick of its own. it's a gate read from Utils.attack(): skip the
// hit when the target PLAYER's swing is already charging, only throw when the
// trade math favors u. wins trades off pure discipline.
public class HitSelectModule extends ToggleModule {
    @Override public String getId() { return "hitselect"; }
    @Override public String getName() { return "HitSelect"; }

    private static volatile boolean gateActive = false;

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        gateActive = enabled;
    }

    // called from Utils.attack() before every real swing
    public static boolean shouldBlockAttack() {
        if (!gateActive) return false;
        return FerriteCore.mc.targetedEntity instanceof PlayerEntity pe
                && pe.getAttackCooldownProgress(0.0f) < 0.5f;
    }
}
