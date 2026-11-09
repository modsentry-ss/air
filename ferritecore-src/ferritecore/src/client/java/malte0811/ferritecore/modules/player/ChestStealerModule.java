package malte0811.ferritecore.modules.player;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.slot.SlotActionType;

import java.util.List;

// ChestStealer — ported from MacePvP: shift-loots every container slot with a
// human-ish delay between grabs.
public class ChestStealerModule extends ToggleModule {
    @Override public String getId() { return "cheststealer"; }
    @Override public String getName() { return "ChestStealer"; }

    public float delay = 2.0f;
    private int timer = 0;

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        var mc = FerriteCore.mc;
        if (mc.player == null || mc.interactionManager == null) return;
        if (!(mc.currentScreen instanceof GenericContainerScreen screen)) return;
        if (timer-- > 0) return;

        var h = screen.getScreenHandler();
        int syncId = h.syncId;
        for (int i = 0; i < h.getInventory().size(); i++) {
            if (!h.getSlot(i).getStack().isEmpty()) {
                mc.interactionManager.clickSlot(syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                timer = (int) delay;
                return; // one grab per delay tick
            }
        }
    }

    @Override
    public List<Option> getOptions() {
        return List.of(new RangeOption("Delay", 0.0f, 10.0f, 1.0f, () -> delay, v -> delay = v));
    }
}
