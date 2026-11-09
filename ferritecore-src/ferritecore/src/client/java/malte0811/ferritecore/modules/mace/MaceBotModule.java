package malte0811.ferritecore.modules.mace;

import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;

import java.util.List;

import static malte0811.ferritecore.FerriteCore.target;
import static malte0811.ferritecore.Utils.*;
import static malte0811.ferritecore.Utils.attack;

public class MaceBotModule extends ToggleModule {
    @Override public String getId() { return "macebot"; }
    @Override public String getName() { return "Mace Bot"; }

    // Configurable options
    private float chance = 100f; // Attack chance percentage
    private float reach = 3.0f; // Attack reach distance
    private float minFallDistance = 2.0f; // Minimum fall distance to attack
    private float strengthThreshold = 6.0f; // Fall threshold when having strength II
    private float noStrengthThreshold = 8.0f; // Fall threshold without strength II

    @Override
    public List<Option> getOptions() {
        return List.of(
            new RangeOption("chance", "Chance", 0, 100, 5,
                () -> chance, v -> chance = v),
            new RangeOption("reach", "Reach", 1.0f, 6.0f, 0.5f,
                () -> reach, v -> reach = v),
            new RangeOption("min_fall", "Min Fall", 0.5f, 10.0f, 0.5f,
                () -> minFallDistance, v -> minFallDistance = v),
            new RangeOption("str_thresh", "Str Thresh", 2.0f, 20.0f, 1.0f,
                () -> strengthThreshold, v -> strengthThreshold = v),
            new RangeOption("no_str_thresh", "No-Str Thresh", 2.0f, 20.0f, 1.0f,
                () -> noStrengthThreshold, v -> noStrengthThreshold = v)
        );
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;
        if (isWearingElytra() || target == null || target.hurtTime != 0) {
            return;
        }

        // Check reach distance
        if (mc.player.distanceTo(target) > reach) {
            return;
        }

        boolean isFalling = mc.player.fallDistance > minFallDistance;
        int currentSlot = Utils.getHeldItemSlot();
        if (!isFalling) {
//			msg("mace: not falling", true);
            return;
        }

        // Apply chance
        if (chance < 100f && Math.random() * 100 > chance) {
            return;
        }

        int maceSlot;
        if (useBreachInsteadOfDensity(mc)) {
            maceSlot = findBreachMace();
        } else {
            maceSlot = findDensityMace();
        }
        if (maceSlot == -1) {
            return;
        }

        if (currentSlot != maceSlot) {
            Utils.setHeldItemSlot(maceSlot);
        }

        attack();
        attack();
    }

    private boolean useBreachInsteadOfDensity(MinecraftClient mc) {
        boolean strength = hasStrengthTwo();
        double fallDistance = mc.player.fallDistance;
        double velocity = mc.player.getVelocity().y;
        double normalizedVelocity = Math.abs(velocity);
        float estimatedFall = (float) (fallDistance + normalizedVelocity);
        if (strength) {
            if (estimatedFall > strengthThreshold) {
                return false;
            } else {
                return true;
            }
        } else {
            if (estimatedFall > noStrengthThreshold) {
                return false;
            } else {
                return true;
            }
        }
    }
}
