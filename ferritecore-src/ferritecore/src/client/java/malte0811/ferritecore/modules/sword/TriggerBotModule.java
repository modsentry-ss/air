package malte0811.ferritecore.modules.sword;

import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.gui.controls.options.ToggleOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;

import java.util.List;

public class TriggerBotModule extends ToggleModule {
    @Override public String getId() { return "triggerbot"; }
    @Override public String getName() { return "Trigger Bot"; }

    // Configurable options
    public boolean permanentTriggerMode = false;
    private float reach = 3.0f; // Attack reach distance

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;

        if (!permanentTriggerMode && !mc.options.attackKey.isPressed()) {
            return;
        }

        if (mc.player.isBlocking()) {
            return;
        }
        if (mc.player.isUsingItem()) {
            return;
        }
        if (mc.player.getHealth() <= 0.0f) {
            return;
        }
        if (mc.currentScreen instanceof HandledScreen<?>) {
            return;
        }

        boolean onGround = mc.player.isOnGround();

        if (!onGround && !Utils.canCrit()) {
            return;
        }

        float swordCooldown = onGround ? 0.95f : 0.92f;
        float axeCooldown = onGround ? 1.25f : 1.02f;
        float maceCooldown = onGround ? 2.33f : 2.92f;

        float wantedCooldown = 1.00f;
        var item = mc.player.getMainHandStack().getItem();
        if (item == Items.WOODEN_SWORD || item == Items.STONE_SWORD || item == Items.IRON_SWORD || item == Items.GOLDEN_SWORD || item == Items.DIAMOND_SWORD || item == Items.NETHERITE_SWORD)
            wantedCooldown = swordCooldown;
        else if (item instanceof AxeItem)
            wantedCooldown = axeCooldown;
        else if (item instanceof MaceItem)
            wantedCooldown = maceCooldown;

        if (mc.player.getAttackCooldownProgress(0.5f) < wantedCooldown) {
            return;
        }

        if (!(mc.targetedEntity instanceof LivingEntity target)) {
            return;
        }
        if (target.getHealth() <= 0.0f) {
            return;
        }

        // Check reach distance
        if (mc.player.distanceTo(target) > reach) {
            return;
        }

        Utils.attack();
    }

    @Override
    public List<Option> getOptions() {
        return List.of(
            new ToggleOption("permanent_mode", this, () -> permanentTriggerMode, v -> permanentTriggerMode = v, "Permanent Mode"),
            new RangeOption("reach", "Reach", 1.0f, 6.0f, 0.5f,
                () -> reach, v -> reach = v)
        );
    }
}
