package malte0811.ferritecore.modules.player;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import java.util.HashMap;
import java.util.Map;

// AutoArmor — ported from MacePvP: equips ur best piece per slot from the
// inventory. scores hardcoded per item (stable across yarn churn), swap via
// shift-click. one swap per tick, keeps the traffic looking hand-made.
public class AutoArmorModule extends ToggleModule {
    @Override public String getId() { return "autoarmor"; }
    @Override public String getName() { return "AutoArmor"; }

    // item -> {armorSlot(0..3), score}
    private static final Map<Item, int[]> PIECES = new HashMap<>();
    static {
        PIECES.put(Items.LEATHER_HELMET, new int[]{0, 1});   PIECES.put(Items.TURTLE_HELMET, new int[]{0, 2});
        PIECES.put(Items.IRON_HELMET, new int[]{0, 3});      PIECES.put(Items.DIAMOND_HELMET, new int[]{0, 4});
        PIECES.put(Items.NETHERITE_HELMET, new int[]{0, 5});
        PIECES.put(Items.LEATHER_CHESTPLATE, new int[]{1, 1});PIECES.put(Items.IRON_CHESTPLATE, new int[]{1, 3});
        PIECES.put(Items.DIAMOND_CHESTPLATE, new int[]{1, 4});PIECES.put(Items.NETHERITE_CHESTPLATE, new int[]{1, 5});
        PIECES.put(Items.LEATHER_LEGGINGS, new int[]{2, 1});  PIECES.put(Items.IRON_LEGGINGS, new int[]{2, 3});
        PIECES.put(Items.DIAMOND_LEGGINGS, new int[]{2, 4});  PIECES.put(Items.NETHERITE_LEGGINGS, new int[]{2, 5});
        PIECES.put(Items.LEATHER_BOOTS, new int[]{3, 1});     PIECES.put(Items.IRON_BOOTS, new int[]{3, 3});
        PIECES.put(Items.DIAMOND_BOOTS, new int[]{3, 4});     PIECES.put(Items.NETHERITE_BOOTS, new int[]{3, 5});
        PIECES.put(Items.CHAINMAIL_HELMET, new int[]{0, 2});  PIECES.put(Items.CHAINMAIL_CHESTPLATE, new int[]{1, 2});
        PIECES.put(Items.CHAINMAIL_LEGGINGS, new int[]{2, 2});PIECES.put(Items.CHAINMAIL_BOOTS, new int[]{3, 2});
        PIECES.put(Items.GOLDEN_HELMET, new int[]{0, 2});     PIECES.put(Items.GOLDEN_CHESTPLATE, new int[]{1, 2});
        PIECES.put(Items.GOLDEN_LEGGINGS, new int[]{2, 2});   PIECES.put(Items.GOLDEN_BOOTS, new int[]{3, 2});
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        var mc = FerriteCore.mc;
        if (mc.player == null || mc.interactionManager == null) return;
        var h = mc.player.playerScreenHandler;

        for (int armorSlot = 5; armorSlot <= 8; armorSlot++) {
            var cur = h.getSlot(armorSlot).getStack();
            int curScore = PIECES.containsKey(cur.getItem()) ? PIECES.get(cur.getItem())[1] : 0;

            int bestSlot = -1, bestScore = curScore;
            for (int s = 9; s <= 44; s++) {
                var st = h.getSlot(s).getStack();
                int[] info = PIECES.get(st.getItem());
                if (info == null) continue;
                if (info[0] != armorSlot - 5) continue; // wrong body part
                if (info[1] > bestScore) { bestScore = info[1]; bestSlot = s; }
            }
            if (bestSlot == -1) continue;

            int syncId = h.syncId;
            if (!cur.isEmpty()) {
                // make room: shift the worn piece out first
                mc.interactionManager.clickSlot(syncId, armorSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
            }
            mc.interactionManager.clickSlot(syncId, bestSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
            return; // one swap per tick
        }
    }
}
