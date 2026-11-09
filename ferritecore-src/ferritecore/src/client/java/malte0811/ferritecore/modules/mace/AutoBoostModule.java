package malte0811.ferritecore.modules.mace;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.util.hit.HitResult;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

public class AutoBoostModule extends ToggleModule {
    @Override public String getId() { return "autowindchargepunch"; }
    @Override public String getName() { return "Wind Charge Booster"; }

    // Configurable options
    private float minFallDistance = 0.5f;
    private float minPitch = 80f;
    private float maxCooldownVariation = 2f;

    private static int cooldownTicks = 0;

    @Override
    public List<Option> getOptions() {
        return List.of(
            new RangeOption("min_fall", "Min Fall", 0f, 5f, 0.5f,
                () -> minFallDistance, v -> minFallDistance = v),
            new RangeOption("min_pitch", "Min Pitch", 60f, 90f, 5f,
                () -> minPitch, v -> minPitch = v),
            new RangeOption("cd_variation", "CD Variation", 0, 5, 1,
                () -> maxCooldownVariation, v -> maxCooldownVariation = v)
        );
    }

    @Override
    public void onTick() {
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return;
        }

        if (!isEnabled()) return;

        if (FerriteCore.mc.crosshairTarget != null && (FerriteCore.mc.crosshairTarget.getType() != HitResult.Type.ENTITY || !(FerriteCore.mc.targetedEntity instanceof WindChargeEntity))) {
            return;
        }

        if (FerriteCore.mc.player == null || FerriteCore.mc.player.getPitch(1.0f) < minPitch) {
            return;
        }

        if (FerriteCore.mc.player.fallDistance < minFallDistance) {
            return;
        }

        cooldownTicks = RandomUtils.secure().randomInt(0, (int) maxCooldownVariation + 1);
        Utils.attack();
    }
}
