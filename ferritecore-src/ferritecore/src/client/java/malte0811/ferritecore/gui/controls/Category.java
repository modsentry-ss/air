package malte0811.ferritecore.gui.controls;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.GUIColors;
import malte0811.ferritecore.modules.Module;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Supplier;

public class Category {
    public String name;
    public int originX, originY;
    public final Supplier<ItemStack> icon;
    public boolean isCollapsed = true;
    private final List<Module> modules;
    private final ObjectArrayList<ModuleButton> buttons = new ObjectArrayList<>();

    public Category(String name, Supplier<ItemStack> icon, List<Module> modules) {
        this.name = name;
        this.icon = icon;
        this.modules = modules;
        this.originX = 5;
        this.originY = 0;

        int y = 5;
        for (var mod : modules) {
            y += 20;
            var btn = new ModuleButton(mod);
            btn.getX = () -> originX + 5;
            int finalY = y;
            btn.getY = () -> originY + finalY;
            buttons.add(btn);
        }
    }

    public void render(DrawContext ctx) {
        ctx.fill(originX, originY, originX + 155, originY +(20), GUIColors.headerBackground); // draw header background

        int totalHeight = 0;
        for (var btn : buttons) totalHeight += btn.getTotalHeight() + 3;
        if (!isCollapsed)
            ctx.fill(originX , originY+20 , originX + 155, originY +25+(totalHeight)+10, GUIColors.moduleBackground); // draw background behind moddules
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, Text.literal(name).withColor(GUIColors.text), originX +32, originY +7, GUIColors.text); // header
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, isCollapsed ? Text.literal("v") : Text.literal("^"), originX+140, originY +7, GUIColors.text); // header expanded or collapsed indicator
        ctx.drawItem(icon.get(), originX+5, originY +2); // header icon

        int currentY = originY+25; // compensates for header
        if (!isCollapsed) {
            for (var btn : buttons) {
                int finalCurrentY = currentY;
                btn.getY = () -> finalCurrentY;
                btn.render(ctx);
                currentY += btn.getTotalHeight();
                ctx.fill(originX, originY + 20, originX + 155, originY + 25 + (totalHeight) + 10, GUIColors.categoryAccent); // draw background behind moddules
            }
        }
    }

    private boolean movingCategory = false;
    private double movingOffsetX = 0;
    private double movingOffsetY = 0;

    public void handleClick(double x, double y, int mouseBtn, int clickType, boolean leftHeld) {
        boolean insideHeader = Utils.isInside(originX, originY, originX + (20 + 140), originY + (20), x, y);
        if (insideHeader && clickType == GLFW.GLFW_PRESS) {
            if (mouseBtn == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                movingCategory = true;
                movingOffsetX = x - originX;
                movingOffsetY = y - originY;
            } else if (mouseBtn == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                isCollapsed = !isCollapsed;
            }
        }
        if (!leftHeld) {
            movingCategory = false;
        }

        if (isCollapsed) return;

        for (var btn : buttons) {
            if (btn.handleClick(x, y, mouseBtn, clickType)) break;
        }
    }

    public void handleMove(double x, double y) {
        if (movingCategory) {
            originX = (int) (x - movingOffsetX);
            originY = (int) (y - movingOffsetY);
        }

        // Forward move events to buttons for slider dragging
        if (!isCollapsed) {
            for (var btn : buttons) {
                btn.handleMove(x, y);
            }
        }
    }

    public void handleBind(int key) {
        for (var btn : buttons) {
            if (btn.waitingForBind) {
                if (key == GLFW.GLFW_KEY_ESCAPE) {
                    btn.setBind.accept(GLFW.GLFW_KEY_UNKNOWN);
                }
                btn.setBind.accept(key);
                btn.waitingForBind = false;
            }
        }
    }

    public boolean handleKeyPress(int keyCode, int scanCode, int modifiers) {
        if (isCollapsed) return false;
        for (var btn : buttons) {
            if (btn.handleKeyPress(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    public boolean handleCharTyped(char chr, int modifiers) {
        if (isCollapsed) return false;
        for (var btn : buttons) {
            if (btn.handleCharTyped(chr, modifiers)) {
                return true;
            }
        }
        return false;
    }
}