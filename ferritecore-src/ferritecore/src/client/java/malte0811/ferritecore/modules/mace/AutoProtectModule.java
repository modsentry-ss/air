package malte0811.ferritecore.modules.mace;

import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;

public class AutoProtectModule extends ToggleModule {
    @Override public String getId() { return "autoprotect"; }
    @Override public String getName() { return "Auto Protect"; }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled()) return;


    }
}
