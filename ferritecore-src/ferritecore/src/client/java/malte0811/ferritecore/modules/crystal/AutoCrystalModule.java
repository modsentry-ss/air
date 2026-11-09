package malte0811.ferritecore.modules.crystal;

import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.gui.controls.options.ToggleOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;

import java.util.List;

public class AutoCrystalModule extends ToggleModule {
    @Override public String getId() { return "autocrystal"; }
    @Override public String getName() { return "Auto Crystal"; }

    boolean switchToNeeded = true;
    boolean toggleSelfAfter = true;
    float reach = 5.0f;

    @Override
    public List<Option> getOptions() {
        return List.of(
            new ToggleOption("auto_switch", this, () -> switchToNeeded, v -> switchToNeeded = v, "Auto Switch"),
            new ToggleOption("disable_after", this, () -> toggleSelfAfter, v -> toggleSelfAfter = v, "Disable After"),
            new RangeOption("reach", "Reach", 1.0f, 6.0f, 0.5f,
                () -> reach, v -> reach = v)
        );
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;

        if (mc.crosshairTarget == null) return;
        if (mc.crosshairTarget.getType() != HitResult.Type.BLOCK) {
            if (mc.crosshairTarget.getType() != HitResult.Type.ENTITY || !(((EntityHitResult) mc.crosshairTarget).getEntity() instanceof EndCrystalEntity)) {
                return;
            }

            // Check reach for crystals
            var entity = ((EntityHitResult) mc.crosshairTarget).getEntity();
            if (mc.player.distanceTo(entity) > reach) {
                return;
            }

            Utils.attack();
            return;
        }
        var pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
        var targetedBlock = mc.player.getEntityWorld().getBlockState(pos);
        if (targetedBlock.getBlock().asItem() != Items.BEDROCK && targetedBlock.getBlock().asItem() != Items.OBSIDIAN) return;
        if (!Utils.canPlaceCrystal(pos)) return;

        if (((BlockHitResult) mc.crosshairTarget).getSide() == Direction.DOWN) return;

        var curSlot = Utils.getHeldItemSlot();
        if (mc.player.getInventory().getStack(curSlot).getItem() != Items.END_CRYSTAL) {
            if (!switchToNeeded) return;

            var slotWithObby = Utils.findSlotWithItem(Items.END_CRYSTAL, null);
            if (slotWithObby == -1) return;

            Utils.setHeldItemSlot(slotWithObby);
        }

        Utils.attack();
        Utils.placeBlock();
        Utils.attack();

        if (toggleSelfAfter) setEnabled(isEnabled());
    }
}
