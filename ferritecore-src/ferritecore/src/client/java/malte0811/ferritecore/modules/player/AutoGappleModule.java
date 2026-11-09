package malte0811.ferritecore.modules.player;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

// AutoGapple — ported from MacePvP: eats a gapple when u drop below the health
// threshold. hotbar slot machine: select -> hold use -> restore.
public class AutoGappleModule extends ToggleModule {
    @Override public String getId() { return "autogapple"; }
    @Override public String getName() { return "AutoGapple"; }

    public float healthPct = 60.0f;
    private int phase = 0, savedSlot = -1, useTicks = 0;

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        var mc = FerriteCore.mc;
        if (mc.player == null || mc.interactionManager == null) return;

        double trigger = mc.player.getMaxHealth() * healthPct / 100.0;
        boolean low = mc.player.getHealth() <= trigger;

        if (phase == 0) {
            if (!low) return;
            int slot = find(mc);
            if (slot == -1) return;
            savedSlot = mc.player.getInventory().getSelectedSlot();
            mc.player.getInventory().setSelectedSlot(slot);
            useTicks = 0;
            phase = 1;
        } else {
            // hold use until it's gone
            mc.options.useKey.setPressed(true);
            useTicks++;
            ItemStack hand = mc.player.getMainHandStack();
            boolean done = hand.getItem() != Items.GOLDEN_APPLE && hand.getItem() != Items.ENCHANTED_GOLDEN_APPLE;
            if (done || useTicks > 80 || (!low && useTicks > 8)) { // healed enough mid-eat -> bail clean
                mc.options.useKey.setPressed(false);
                mc.player.getInventory().setSelectedSlot(savedSlot);
                phase = 0;
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) return;
        var mc = FerriteCore.mc;
        if (mc.player != null && phase != 0) {
            mc.options.useKey.setPressed(false);
            mc.player.getInventory().setSelectedSlot(Math.max(savedSlot, 0));
        }
        phase = 0; savedSlot = -1;
    }

    private static int find(net.minecraft.client.MinecraftClient mc) {
        var inv = mc.player.getInventory();
        for (int i = 0; i < 9; i++) {
            ItemStack s = inv.getStack(i);
            if (s.getItem() == Items.GOLDEN_APPLE || s.getItem() == Items.ENCHANTED_GOLDEN_APPLE) return i;
        }
        return -1;
    }

    @Override
    public List<Option> getOptions() {
        return List.of(new RangeOption("Health%", 20.0f, 95.0f, 5.0f, () -> healthPct, v -> healthPct = v));
    }
}
