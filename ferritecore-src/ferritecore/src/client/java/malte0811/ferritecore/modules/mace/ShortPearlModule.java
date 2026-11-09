package malte0811.ferritecore.modules.mace;

import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.gui.controls.options.ToggleOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Items;
import net.minecraft.item.WindChargeItem;
import net.minecraft.util.Hand;

import java.util.List;

public class ShortPearlModule extends ToggleModule {
    @Override public String getId() { return "shortpearl"; }
    @Override public String getName() { return "Short Pearl"; }

    private boolean disableAfterUse = true;
    private float windChargeDelay = 1f;
    private float minPitch = -89f;

    @Override
    public List<Option> getOptions() {
        return List.of(
            new ToggleOption("disable_after_use", this, () -> disableAfterUse, v -> disableAfterUse = v, "Disable After Use"),
            new RangeOption("wind_delay", "Wind Delay", 0, 10, 1,
                () -> windChargeDelay, v -> windChargeDelay = v),
            new RangeOption("min_pitch", "Min Pitch", -90f, -60f, 1f,
                () -> minPitch, v -> minPitch = v)
        );
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;

        if (mc.player.getPitch() > minPitch) {
            return;
        }

        int pearlSlot = -1;
        int windSlot = -1;
        for (int i = 0; i < 9; i++) {
            var item = mc.player.getInventory().getStack(i).getItem();
            if (item instanceof EnderPearlItem) {
                pearlSlot = i;
            } else if (item instanceof WindChargeItem) {
                windSlot = i;
            }
            if (pearlSlot != -1 && windSlot != -1) {
                break;
            }
        }
        if (pearlSlot == -1 || windSlot == -1) {
            return;
        }

        if (mc.player.getItemCooldownManager().isCoolingDown(Items.MACE.getDefaultStack())
                || mc.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL.getDefaultStack())) {
            return;
        }

        int selectedSlot = Utils.getHeldItemSlot();
        if (selectedSlot != pearlSlot) {
            Utils.setHeldItemSlot(pearlSlot);
        }
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);

        int finalWindSlot = windSlot;
        Utils.runLater(() -> {
            Utils.setHeldItemSlot(finalWindSlot);
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        }, (int) windChargeDelay);

        if (disableAfterUse) {
            setEnabled(false);
        }
    }
}
