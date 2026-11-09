package malte0811.ferritecore.modules.player;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import java.util.List;

// AutoPot — ported from MacePvP: splashes a healing potion at ur feet when u
// drop below the threshold. select -> look down -> throw -> swap back.
public class AutoPotModule extends ToggleModule {
    @Override public String getId() { return "autopot"; }
    @Override public String getName() { return "AutoPot"; }

    public float healthPct = 50.0f;
    private int phase = 0, savedSlot = -1;

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        var mc = FerriteCore.mc;
        if (mc.player == null || mc.interactionManager == null) return;

        double trigger = mc.player.getMaxHealth() * healthPct / 100.0;
        boolean low = mc.player.getHealth() <= trigger;

        if (phase == 0) {
            if (!low) return;
            if (mc.currentScreen != null) return; // dont throw mid-inventory
            int slot = find(mc);
            if (slot == -1) return;
            savedSlot = mc.player.getInventory().getSelectedSlot();
            mc.player.getInventory().setSelectedSlot(slot);
            mc.player.setPitch(85.0f); // straight down at the floor
            phase = 1;
        } else if (phase == 1) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            phase = 2;
        } else {
            mc.player.getInventory().setSelectedSlot(savedSlot);
            savedSlot = -1;
            phase = 0;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) return;
        var mc = FerriteCore.mc;
        if (mc.player != null && phase != 0) {
            mc.player.getInventory().setSelectedSlot(Math.max(savedSlot, 0));
        }
        phase = 0; savedSlot = -1;
    }

    private static int find(net.minecraft.client.MinecraftClient mc) {
        var inv = mc.player.getInventory();
        for (int i = 0; i < 9; i++) {
            ItemStack s = inv.getStack(i);
            if (s.getItem() == Items.SPLASH_POTION) return i;
        }
        return -1;
    }

    @Override
    public List<Option> getOptions() {
        return List.of(new RangeOption("Health%", 15.0f, 90.0f, 5.0f, () -> healthPct, v -> healthPct = v));
    }
}
