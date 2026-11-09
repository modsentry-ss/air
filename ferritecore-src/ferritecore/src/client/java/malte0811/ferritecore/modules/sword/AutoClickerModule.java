package malte0811.ferritecore.modules.sword;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.gui.controls.options.ToggleOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;

import java.util.List;

// AutoClicker — ported from MacePvP: humanized jitter (sum of 3 randoms),
// fatigue after 5s of holding, respects the vanilla attack cooldown unless disabled.
public class AutoClickerModule extends ToggleModule {
    @Override public String getId() { return "autoclicker"; }
    @Override public String getName() { return "AutoClicker"; }

    public float cps = 12.0f;
    public boolean respectCd = true;
    public boolean fatigue = true;
    private long nextClick = 0, heldSince = 0;

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        MinecraftClient mc = FerriteCore.mc;
        if (mc.player == null) return;

        if (!mc.options.attackKey.isPressed()) { heldSince = 0; return; }
        if (!(mc.crosshairTarget instanceof EntityHitResult hit)) return;
        if (!(hit.getEntity() instanceof LivingEntity t)) return;

        long now = System.currentTimeMillis();
        if (heldSince == 0) heldSince = now;
        if (now < nextClick) return;

        double c = cps;
        if (fatigue && now - heldSince > 5000) c *= 0.85; // get tired like a real hand
        double jitter = ((Math.random() + Math.random() + Math.random()) / 3.0) * 0.3 + 0.85;
        nextClick = now + (long) (1000.0 / c * jitter);

        if (respectCd && mc.player.getAttackCooldownProgress(0.5f) < 1.0f) return;
        Utils.attack();
    }

    @Override
    public List<Option> getOptions() {
        return List.of(
            new RangeOption("CPS", 6.0f, 20.0f, 1.0f, () -> cps, v -> cps = v),
            new ToggleOption(this, () -> respectCd, v -> respectCd = v, "Respect Cooldown"),
            new ToggleOption(this, () -> fatigue, v -> fatigue = v, "Fatigue")
        );
    }
}
