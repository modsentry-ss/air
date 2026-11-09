package malte0811.ferritecore.modules.render;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.modules.ToggleModule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

// GlowESP — ported from MacePvP: client-side glowing flag on nearby players.
// outline shows thru walls, zero custom rendering so it survives version churn.
// marks get cleaned the moment the module turns off.
public class GlowESPModule extends ToggleModule {
    @Override public String getId() { return "glowesp"; }
    @Override public String getName() { return "GlowESP"; }

    public float dist = 32.0f;
    private final Set<UUID> marked = new HashSet<>();

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        var mc = FerriteCore.mc;
        if (mc.player == null || mc.world == null) return;

        double r2 = dist * dist;

        // clean up out-of-range / gone players
        marked.removeIf(id -> {
            var e = mc.world.getPlayerByUuid(id);
            if (e == null) return true;
            if (mc.player.squaredDistanceTo(e) > r2) { e.setGlowing(false); return true; }
            return false;
        });

        for (var p : mc.world.getPlayers()) {
            if (p == mc.player || p.isSpectator()) continue;
            if (mc.player.squaredDistanceTo(p) > r2) continue;
            if (!p.isGlowing()) {
                p.setGlowing(true);
                marked.add(p.getUuid());
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) return;
        var mc = FerriteCore.mc;
        if (mc.world != null) {
            for (UUID id : marked) {
                var e = mc.world.getPlayerByUuid(id);
                if (e != null) e.setGlowing(false);
            }
        }
        marked.clear();
    }

    @Override
    public List<Option> getOptions() {
        return List.of(new RangeOption("Range", 8.0f, 64.0f, 4.0f, () -> dist, v -> dist = v));
    }
}
