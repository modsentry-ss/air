package malte0811.ferritecore.modules.sword;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.RangeOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.List;

// Reach — ported from MacePvP: bumps the vanilla ENTITY_INTERACTION_RANGE
// attribute. servers check reach since 1.20.5, so keep the slider honest.
// puts the attribute back the moment the module turns off.
public class ReachModule extends ToggleModule {
    @Override public String getId() { return "reach"; }
    @Override public String getName() { return "Reach"; }

    private static final float VANILLA = 3.0f;
    public float range = 3.5f;

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (FerriteCore.mc.player == null) return;
        EntityAttributeInstance inst =
                FerriteCore.mc.player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
        if (inst == null) return;
        if (inst.getBaseValue() != range) inst.setBaseValue(range);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) return;
        var p = FerriteCore.mc.player;
        if (p == null) return;
        EntityAttributeInstance inst = p.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
        if (inst != null) inst.setBaseValue(VANILLA);
    }

    @Override
    public List<Option> getOptions() {
        return List.of(new RangeOption("Range", 3.0f, 4.5f, 0.05f, () -> range, v -> range = v));
    }
}
