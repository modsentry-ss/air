package malte0811.ferritecore.modules.movement;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Random;

public class JumpResetModule extends ToggleModule {
    @Override public String getId() { return "autojumpreset"; }
    @Override public String getName() { return "Auto Jump Reset"; }

    private float chance = 50f;
    private final Random random = new Random();

    @Override
    public List<Option> getOptions() {
        return List.of(
            new RangeOption("chance", "Chance", 0, 100, 5,
                () -> chance, v -> chance = v)
        );
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;

        MinecraftClient mc = FerriteCore.mc;
        if (mc.player == null) return;

        // Don't trigger if in a GUI screen
        if (mc.currentScreen instanceof HandledScreen) return;

        // Don't trigger if blocking, using item, or in water
        if (mc.player.isBlocking()) return;
        if (mc.player.isUsingItem()) return;
        if (mc.player.isTouchingWater()) return;

        // Check if attacker is a player
        if (!(mc.player.getAttacker() instanceof PlayerEntity attacker)) return;

        // Check attacker conditions
        if (!attacker.isOnGround()) return;
        if (attacker.hurtTime <= 0 || attacker.hurtTime >= 10) return;
        if (attacker.hurtTime != attacker.maxHurtTime - 1) return;
        if (attacker.isOnFire()) return;

        // Apply chance
        if (random.nextFloat() > chance / 100f) return;

        // Jump to reduce knockback
        mc.player.jump();
    }
}
