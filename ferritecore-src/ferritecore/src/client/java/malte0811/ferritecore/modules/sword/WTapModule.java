package malte0811.ferritecore.modules.sword;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Random;

public class WTapModule extends ToggleModule {
    @Override public String getId() { return "wtap"; }
    @Override public String getName() { return "W-Tap"; }

    // Settings
    private float chance = 100f;
    private float holdLength = 100f; // milliseconds
    private float holdLengthVariance = 100f; // variance in ms
    private float reaction = 10f; // milliseconds
    private float reactionVariance = 20f; // variance in ms

    // State
    private long attackTime = 0;
    private long actualReaction = 0;
    private long actualHoldLength = 0;
    private boolean shouldTap = false;
    private boolean isHolding = false;
    private final Random random = new Random();

    @Override
    public List<Option> getOptions() {
        return List.of(
            new RangeOption("chance", "Chance", 0, 100, 5,
                () -> chance, v -> chance = v),
            new RangeOption("hold_length", "Hold Length", 0, 400, 10,
                () -> holdLength, v -> holdLength = v),
            new RangeOption("hold_variance", "Hold Variance", 0, 200, 10,
                () -> holdLengthVariance, v -> holdLengthVariance = v),
            new RangeOption("reaction", "Reaction", 0, 50, 5,
                () -> reaction, v -> reaction = v),
            new RangeOption("reaction_variance", "Reaction Var", 0, 30, 5,
                () -> reactionVariance, v -> reactionVariance = v)
        );
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;

        MinecraftClient mc = FerriteCore.mc;
        if (mc.player == null) return;

        // Check if W key is pressed using GLFW (key code 87 = W)
        boolean wPressed = GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS;

        if (!wPressed) {
            shouldTap = false;
            isHolding = false;
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Check if we should start the W release after reaction time
        if (shouldTap && (currentTime - attackTime) >= actualReaction) {
            mc.options.forwardKey.setPressed(false);
            isHolding = true;
            shouldTap = false;
        }

        // Check if we should re-press W after hold length
        if (isHolding && (currentTime - attackTime) >= (actualReaction + actualHoldLength)) {
            mc.options.forwardKey.setPressed(true);
            isHolding = false;
        }
    }

    @Override
    public void onAttack(PlayerEntity attacker, World world, Entity victim, @Nullable EntityHitResult hitResult) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;
        if (mc.player == null) return;
        if (!mc.options.forwardKey.isPressed()) return;
        if (!mc.player.isSprinting()) return;

        // Apply chance
        if (random.nextFloat() * 100 > chance) return;

        // Calculate randomized timings
        actualReaction = (long) (reaction + (random.nextFloat() * reactionVariance));
        actualHoldLength = (long) (holdLength + (random.nextFloat() * holdLengthVariance));

        attackTime = System.currentTimeMillis();
        shouldTap = true;
    }
}
