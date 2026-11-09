package malte0811.ferritecore.modules.crystal;

import malte0811.ferritecore.Utils;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;

public class AutoObsidianModule extends ToggleModule {
    @Override public String getId() { return "autoobsidian"; }
    @Override public String getName() { return "Auto Obsidian"; }

    boolean switchToNeeded = true;
    boolean toggleSelfAfter = true;

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;

        if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.BLOCK) return;
        var targetPos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
        var targetedBlock = mc.player.getEntityWorld().getBlockState(targetPos);
        if (targetedBlock.getBlock().asItem() == Items.BEDROCK || targetedBlock.getBlock().asItem() == Items.OBSIDIAN) return;

        if (!targetedBlock.isReplaceable() && mc.player.getEntityWorld().getBlockState(targetPos.add(0, 1, 0)).getBlock().asItem() != Items.AIR) return;

        if (((BlockHitResult) mc.crosshairTarget).getSide() != Direction.UP) return;

        var curSlot = Utils.getHeldItemSlot();
        if (mc.player.getInventory().getStack(curSlot).getItem() != Items.OBSIDIAN) {
            if (!switchToNeeded) return;

            var slotWithObby = Utils.findSlotWithItem(Items.OBSIDIAN, null);
            if (slotWithObby == -1) return;

            Utils.setHeldItemSlot(slotWithObby);
        }

        Utils.placeBlock();
        if (toggleSelfAfter) setEnabled(isEnabled());
    }
}
