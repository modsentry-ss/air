package malte0811.ferritecore.modules.mace;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;

import java.util.List;

import static malte0811.ferritecore.FerriteCore.target;
import static malte0811.ferritecore.Utils.*;

public class ShieldBreakerModule extends ToggleModule {
    @Override public String getId() { return "shieldbreaker"; }
    @Override public String getName() { return "Shield Breaker"; }

    // Configurable options
    private float reach = 3.0f;
    private float groundDelay = 3f;
    private float cooldownMs = 100f;

    private long shieldBreakerCooldownUntil = 0;
    private long shieldOnGroundDelay = -1;

    @Override
    public List<Option> getOptions() {
        return List.of(
            new RangeOption("reach", "Reach", 1.0f, 6.0f, 0.5f,
                () -> reach, v -> reach = v),
            new RangeOption("ground_delay", "Ground Delay", 0, 10, 1,
                () -> groundDelay, v -> groundDelay = v),
            new RangeOption("cooldown_ms", "Cooldown (ms)", 0, 500, 50,
                () -> cooldownMs, v -> cooldownMs = v)
        );
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;
        if (isWearingElytra() || target == null || target.hurtTime != 0) {
            return;
        }
        if (!FerriteCore.target.isBlocking()) return;

        // Check reach distance
        if (mc.player.distanceTo(target) > reach) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now < shieldBreakerCooldownUntil) return;

        int oldSlot = Utils.getHeldItemSlot();
        int slotWithAxe = -1;
        for (int i = 0; i < 9; i++) {
            if (isAxe(mc.player.getInventory().getStack(i).getItem())) {
                slotWithAxe = i;
                break;
            }
        }
        if (slotWithAxe == -1) {
            return;
        }

        if (Utils.getHeldItemSlot() != slotWithAxe) {
            Utils.setHeldItemSlot(slotWithAxe);
        }

        if (shieldOnGroundDelay == -1 && mc.player.isOnGround()) {
            shieldOnGroundDelay = (long) groundDelay;
        }
        if (shieldOnGroundDelay > 0) {
            shieldOnGroundDelay--;
            return;
        }
        shieldOnGroundDelay = -1;
        attack();

        shieldBreakerCooldownUntil = now + (long) cooldownMs;

//		if (oldSlot != slotWithAxe) {
//			mc.player.getInventory().selectedSlot= oldSlot;
//			mc.player.getInventory().markDirty();
//		}

        int swordSlot = findSlotWithSword();
        Utils.setHeldItemSlot(swordSlot);
    }
}
