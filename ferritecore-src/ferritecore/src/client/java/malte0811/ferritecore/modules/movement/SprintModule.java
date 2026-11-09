    package malte0811.ferritecore.modules.movement;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;

public class SprintModule extends ToggleModule {
    @Override public String getId() { return "sprint"; }
    @Override public String getName() { return "Sprint"; }

    @Override
    public void onTick() {
        if (!isEnabled()) return;

        MinecraftClient mc = FerriteCore.mc;
        if (mc.player == null) return;

        if (mc.player.forwardSpeed > 0) {
            mc.options.sprintKey.setPressed(true);
        }
    }
}

