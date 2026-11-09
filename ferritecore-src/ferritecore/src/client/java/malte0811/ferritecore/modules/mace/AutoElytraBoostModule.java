package malte0811.ferritecore.modules.mace;

import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import java.util.List;

import static malte0811.ferritecore.Utils.*;

public class AutoElytraBoostModule extends ToggleModule {
    @Override public String getId() { return "elytraboost"; }
    @Override public String getName() { return "Auto ElytraBoost"; }

    // Configurable options
    private float minPitch = 90f;
    private float maxGroundDistance = 0.8f;
    private float useDelay = 1f;

    @Override
    public List<Option> getOptions() {
        return List.of(
            new RangeOption("min_pitch", "Min Pitch", 70f, 90f, 5f,
                () -> minPitch, v -> minPitch = v),
            new RangeOption("ground_dist", "Ground Dist", 0.2f, 3.0f, 0.2f,
                () -> maxGroundDistance, v -> maxGroundDistance = v),
            new RangeOption("use_delay", "Use Delay", 0, 5, 1,
                () -> useDelay, v -> useDelay = v)
        );
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;

        int windSlot = findSlotWithItem(Items.WIND_CHARGE, null);
        if (windSlot == -1) {
            return;
        }

        if (!isWearingElytra() || !mc.player.isInPose(EntityPose.GLIDING)) {
            return;
        }

        boolean lookAngle = mc.player.getPitch() >= minPitch;
        if (!lookAngle) {
            return;
        }

        boolean nextToGround = getDistanceToGround() < maxGroundDistance;
        getDistanceToGround();
        if (!nextToGround) {
            return;
        }

        if (Utils.getHeldItemSlot() != windSlot) {
            Utils.setHeldItemSlot(windSlot);
        }

        Utils.runLater(() -> {
            mc.player.swingHand(Hand.MAIN_HAND);
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        }, (int) useDelay);
//        mc.player.checkFallFlying();
//        Utils.runLater(() -> {
//            float finalPitch = -45f + rand(-0.2f, 0.2f);
//            smoothPitchTransition(89f, finalPitch, 12);
//        }, -1);
    }
}
