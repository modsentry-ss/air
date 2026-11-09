package malte0811.ferritecore.modules.sword;

import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class STapModule extends ToggleModule {
    @Override public String getId() { return "stap"; }
    @Override public String getName() { return "S-Tap"; }

    private float delay = 5f; // Ticks delay for S-tap

    @Override
    public List<Option> getOptions() {
        return List.of(
            new RangeOption("delay", "Delay", 1, 15, 1,
                () -> delay, v -> delay = v)
        );
    }

    @Override
    public void onAttack(PlayerEntity attacker, World world, Entity victim, @Nullable EntityHitResult hitResult) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;

        var pressed = mc.options.backKey.isPressed();
        var front = mc.options.forwardKey.isPressed();
        mc.options.forwardKey.setPressed(false);
        mc.options.backKey.setPressed(true);
        Utils.runLater(() -> {
            mc.options.backKey.setPressed(false);
        }, (int) delay);
        if (front) {
            Utils.runLater(() -> {
                mc.options.backKey.setPressed(true);
            }, (int) delay);
        }
    }
}
