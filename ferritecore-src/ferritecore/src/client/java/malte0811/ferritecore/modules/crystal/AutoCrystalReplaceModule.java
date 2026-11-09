package malte0811.ferritecore.modules.crystal;

import malte0811.ferritecore.Utils;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AutoCrystalReplaceModule extends ToggleModule {
    @Override public String getId() { return "autocrystalreplace"; }
    @Override public String getName() { return "Auto Replace Crystal"; }

    public boolean onlyWhenHoldingCrystals = true;

    @Override
    public void onAttack(PlayerEntity attacker, World world, Entity victim, @Nullable EntityHitResult hitResult) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;

        if (!(victim.getType() == EntityType.END_CRYSTAL) || !(attacker == mc.player)) {
            victim.getType().toString();
            attacker.getDisplayName().getString();
            return;
        }

        int slotWithCrystals = Utils.findSlotWithItem(Items.END_CRYSTAL, null);
        if (slotWithCrystals == -1) {
            return;
        }

        var isOnSlot = Utils.getHeldItemSlot() == slotWithCrystals;
        if (onlyWhenHoldingCrystals && !isOnSlot) {
            return;
        }

        if (!isOnSlot) {
            Utils.setHeldItemSlot(slotWithCrystals);
        }
        Utils.useItem();
    }
}
