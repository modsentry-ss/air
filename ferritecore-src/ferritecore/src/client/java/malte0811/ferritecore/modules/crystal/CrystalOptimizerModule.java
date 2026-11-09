package malte0811.ferritecore.modules.crystal;

import malte0811.ferritecore.modules.ToggleModule;

public class CrystalOptimizerModule extends ToggleModule {
    @Override public String getId() { return "crystaloptimizer"; }
    @Override public String getName() { return "Crystal Optimizer"; }

    public static boolean IS_ACTIVE = false;

    @Override
    public void onTick() {
        if (IS_ACTIVE != isEnabled()) {
            IS_ACTIVE = isEnabled();
        }
    }
}
