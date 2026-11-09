package malte0811.ferritecore.modules.spear;


import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;

import static malte0811.ferritecore.Utils.*;

public class LungeSwapModule extends ToggleModule {
    @Override public String getId() { return "lungeswap"; }
    @Override public String getName() { return "Lunge Swap"; }

    private Item wantedItemHeld = Items.WIND_CHARGE;
    private float reach = 3.0f;
    private float chance = 100f;

    @Override
    public List<Option> getOptions() {
        return List.of(
            new RangeOption("reach", "Reach", 1.0f, 6.0f, 0.5f,
                () -> reach, v -> reach = v),
            new RangeOption("chance", "Chance", 0, 100, 5,
                () -> chance, v -> chance = v)
        );
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;

        float weaponCooldown = mc.player.getAttackCooldownProgress(0.5f);
        if (weaponCooldown != 0.0f && weaponCooldown != 1.0f) {
            return;
        }

        if (!mc.options.attackKey.isPressed()) {
            return;
        }

        // Apply chance
        if (chance < 100f && Math.random() * 100 > chance) {
            return;
        }

        int heldSlot = Utils.getHeldItemSlot();
        int spearSlot = findSlotWithSpear(true);
        int wantedSlot = Utils.findSlotWithItem(wantedItemHeld, null);
        if (wantedSlot == -1 || spearSlot == -1) {
            return;
        }

        var holdingWanted = wantedSlot == heldSlot;
        if (!holdingWanted) {
            return;
//            Utils.setHeldItemSlot(wantedSlot);
        }

        System.currentTimeMillis();
        Utils.setHeldItemSlot(spearSlot);

        System.currentTimeMillis();
        attack();

        System.currentTimeMillis();
        Utils.setHeldItemSlot(wantedSlot);
    }
}
