package malte0811.ferritecore.modules.mace;

import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.gui.controls.options.ToggleOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;

import java.util.List;

import static malte0811.ferritecore.FerriteCore.target;
import static malte0811.ferritecore.Utils.*;
import static malte0811.ferritecore.Utils.attack;
import static malte0811.ferritecore.Utils.findBreachMace;
import static malte0811.ferritecore.Utils.findSlotWithAxe;
import static malte0811.ferritecore.Utils.findSlotWithSword;
import static malte0811.ferritecore.Utils.getDistanceToGround;

public class BreachSwapModule extends ToggleModule {
    @Override public String getId() { return "autobreachswap"; }
    @Override public String getName() { return "Auto BreachSwap"; }

    // Configurable options
    private boolean preferSword = true;
    private float chance = 100f; // Attack chance percentage
    private float reach = 3.0f; // Attack reach distance
    private float maxGroundDistance = 6.0f; // Max distance to ground
    private float cooldownTicks = 3.0f; // Cooldown in ticks

    private int breachCooldown = 0;

    @Override
    public List<Option> getOptions() {
        return List.of(
            new ToggleOption("prefer_sword", this, () -> preferSword, v -> preferSword = v, "Prefer Sword"),
            new RangeOption("chance", "Chance", 0, 100, 5,
                () -> chance, v -> chance = v),
            new RangeOption("reach", "Reach", 1.0f, 6.0f, 0.5f,
                () -> reach, v -> reach = v),
            new RangeOption("max_ground_distance", "Max Ground Dist", 1.0f, 20.0f, 1.0f,
                () -> maxGroundDistance, v -> maxGroundDistance = v),
            new RangeOption("cooldown", "Cooldown", 0, 10, 1,
                () -> cooldownTicks, v -> cooldownTicks = v)
        );
    }

    @Override
    public void onTick() {
        if (breachCooldown > 0) {
            breachCooldown--;
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;
        if (isWearingElytra() || target == null || target.hurtTime != 0) {
            return;
        }

        // Check reach distance
        if (mc.player.distanceTo(target) > reach) {
            return;
        }

        if (!canCrit()) {
            return;
        }

        float weaponCooldown = mc.player.getAttackCooldownProgress(0.5f);
        boolean canCritWithWeapon = weaponCooldown == 1.0f;

        if (!canCritWithWeapon) {
            return;
        }

        if (getDistanceToGround() > maxGroundDistance) {
            getDistanceToGround();
            return;
        }

        // Apply chance
        if (chance < 100f && Math.random() * 100 > chance) {
            return;
        }

        int breachSlot = findBreachMace();
        if (breachSlot == -1) {
            return;
        }

        int weaponSlot = findSlotWithSword();
        if (weaponSlot == -1 || !preferSword) {
            weaponSlot = findSlotWithAxe();
            if (weaponSlot == -1) {
                return;
            }
        }

        System.currentTimeMillis();
        Utils.setHeldItemSlot(breachSlot);

        System.currentTimeMillis();
        attack();

        System.currentTimeMillis();
        Utils.setHeldItemSlot(weaponSlot);

        breachCooldown = (int) cooldownTicks;
    }
}
