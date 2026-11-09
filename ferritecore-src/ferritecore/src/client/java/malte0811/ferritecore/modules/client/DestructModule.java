package malte0811.ferritecore.modules.client;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;

public class DestructModule extends ToggleModule {
    @Override public String getId() { return "destruct"; }
    @Override public String getName() { return "Destruct"; }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;

        FerriteCore.destruct();
        setEnabled(false);
    }
}
