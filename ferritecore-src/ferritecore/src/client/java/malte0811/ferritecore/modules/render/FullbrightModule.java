package malte0811.ferritecore.modules.render;

import malte0811.ferritecore.modules.ToggleModule;

// Fullbright — no tick. FullbrightMixin (on GameRenderer.getNightVisionStrength)
// reads the static mirror and forces max brightness. caves r gone.
public class FullbrightModule extends ToggleModule {
    @Override public String getId() { return "fullbright"; }
    @Override public String getName() { return "Fullbright"; }

    public static volatile boolean active = false;

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        active = enabled;
    }
}
