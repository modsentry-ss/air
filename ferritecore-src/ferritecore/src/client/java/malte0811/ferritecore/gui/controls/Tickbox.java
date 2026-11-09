package malte0811.ferritecore.gui.controls;

import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.CatUI;
import malte0811.ferritecore.gui.GUIColors;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Tickbox {
    public boolean status;
    public Consumer<Boolean> onChange;
    public Supplier<Integer> getX, getY;
    private final int width = 15, height = 15;
    private final int innerMargin = 3;

    public Tickbox(boolean status, Supplier<Integer> x, Supplier<Integer> y, @Nullable Consumer<Boolean> onChange) {
        this.status = status;
        this.getX = x;
        this.getY = y;
        if (onChange != null) {
            this.onChange = onChange;
        } else {
            this.onChange = (val) -> {};
        }
    }

    public void render(DrawContext ctx) {
        ctx.drawStrokedRectangle(getX.get(), getY.get(), width, height, GUIColors.border);
        if (status)
            CatUI.rect(getX.get()+ innerMargin, getY.get()+ innerMargin,
                    (getX.get() + width)- innerMargin, (getY.get() + height)- innerMargin, GUIColors.accent);
    }

    public void handleClick(double clickX, double clickY, int mouseBtn) {
        if (!Utils.isInside(getX.get(), getY.get(), (getX.get() + width), (getY.get() + height), clickX, clickY)) {
            return;
        }

        status = !status;
        onChange.accept(status);
    }
}
