package malte0811.ferritecore.modules.crystal;

import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.ToggleOption;
import malte0811.ferritecore.modules.ToggleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Set;

public class AutoTotemModule extends ToggleModule {
    @Override public String getId() { return "autototem"; }
    @Override public String getName() { return "Auto Totem"; }

    private boolean onlyWhenInventoryOpen = true;
    private boolean onlyWhenHoveringItem = false;

    @Override
    public List<Option> getOptions() {
        return List.of(
            new ToggleOption("only_in_inventory", this, () -> onlyWhenInventoryOpen, v -> onlyWhenInventoryOpen = v, "Only In Inventory"),
            new ToggleOption("only_when_hovering", this, () -> onlyWhenHoveringItem, v -> onlyWhenHoveringItem = v, "Only When Hovering")
        );
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!isEnabled() || mc.player == null) return;

        if (!(mc.currentScreen instanceof InventoryScreen) && onlyWhenInventoryOpen) return;
        var screen = (InventoryScreen) mc.currentScreen;
        var hovered = screen.hoveredElement(mc.mouse.getX(), mc.mouse.getY());
        if (hovered.isEmpty() && onlyWhenHoveringItem) return;

        var elem = hovered.get();

        var hasTotem = mc.player.getInventory().containsAny(Set.of(Items.TOTEM_OF_UNDYING));
        if (!hasTotem) return;

        elem.getFocusedPath().toString();
    }
}
