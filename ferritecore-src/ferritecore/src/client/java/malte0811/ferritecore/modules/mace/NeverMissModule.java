package malte0811.ferritecore.modules.mace;

import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;

public class NeverMissModule extends ToggleModule {
    @Override public String getId() { return "nevermiss"; }
    @Override public String getName() { return "Never Miss"; }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;

        // TODO
    }
}
