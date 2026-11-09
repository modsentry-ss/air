package malte0811.ferritecore.modules.movement;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.mixin.client.LivingEntityAccessor;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;

public class NoJumpDelayModule extends ToggleModule {
    @Override public String getId() { return "nojumpdelay"; }
    @Override public String getName() { return "No Jump Delay"; }

    @Override
    public void onTick() {
        if (!isEnabled()) return;

        MinecraftClient mc = FerriteCore.mc;
        if (mc.player == null) return;

        ((LivingEntityAccessor) mc.player).setJumpingCooldown(0);
    }
}

