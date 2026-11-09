package malte0811.ferritecore.gui;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.gui.controls.ActivationCategory;
import malte0811.ferritecore.gui.controls.Category;
import malte0811.ferritecore.modules.client.LoginModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class CatUI {
    public final List<Category> categories = new ArrayList<>();
    public final ActivationCategory activation;

    private static DrawContext ctx;

    public static void rect(int x, int y, int x2, int y2, int alphaRGBHex) {
        ctx.fill(x, y, x2, y2, alphaRGBHex);
    }
    public static void text(int x, int y, Text text) {
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, text, x, y, GUIColors.text);
    }

    public CatUI() {
        FerriteCore.activated = false;
        activation = new ActivationCategory("Activation",
                () -> Items.NETHER_STAR.getDefaultStack()
        );
        activation.originX = 100;
        activation.originY = 100;
        var mace = new Category(
                "Mace",
                () -> Items.MACE.getDefaultStack(),
                FerriteCore.moduleManager.maceModules
        );
        mace.originY = 10;
        categories.add(mace);

        var sword = new Category(
                "Sword",
                () -> Items.DIAMOND_SWORD.getDefaultStack(),
                FerriteCore.moduleManager.swordModules
        );
        sword.originY = 35;
        categories.add(sword);

        var spear = new Category(
                "Spear",
                () -> Items.COPPER_HOE.getDefaultStack(),
                FerriteCore.moduleManager.spearModules
        );
        spear.originY = 60;
        categories.add(spear);

        var crystal = new Category(
                "Crystal",
                () -> Items.END_CRYSTAL.getDefaultStack(),
                FerriteCore.moduleManager.crystalModules
        );
        crystal.originY = 85;
        categories.add(crystal);

        var movement = new Category(
                "Movement",
                () -> Items.CHAINMAIL_BOOTS.getDefaultStack(),
                FerriteCore.moduleManager.movementModules
        );
        movement.originY = 110;
        categories.add(movement);

        var render = new Category(
                "Render",
                () -> Items.ITEM_FRAME.getDefaultStack(),
                FerriteCore.moduleManager.renderModules
        );
        render.originY = 135;
        categories.add(render);

        var player = new Category(
                "Player",
                () -> Items.LEATHER_CHESTPLATE.getDefaultStack(),
                FerriteCore.moduleManager.playerModules
        );
        player.originY = 185;
        categories.add(player);

        var client = new Category(
                "Client",
                () -> Items.COMMAND_BLOCK.getDefaultStack(),
                FerriteCore.moduleManager.clientModules
        );
        client.originY = 210;
        categories.add(client);
    }

    public void draw(DrawContext ctx) {
        CatUI.ctx = ctx;
        if (!FerriteCore.showUI) return;

        if (FerriteCore.activated) {
            for (var cat : categories) {
                cat.render(ctx);
            }
        } else {
            activation.render(ctx);
        }

        CatUI.ctx = null;
    }

    private boolean left = false;
    private boolean right = false;

    public void handleClick(double x, double y, int mouseBtn, int action) {
        if (mouseBtn == InputUtil.GLFW_MOUSE_BUTTON_LEFT) {
            left = action == GLFW.GLFW_PRESS;
        } else if (mouseBtn == InputUtil.GLFW_MOUSE_BUTTON_RIGHT) {
            right = action == GLFW.GLFW_PRESS;
        }

        if (FerriteCore.activated) {
            for (var cat : categories) {
                cat.handleClick(x, y, mouseBtn, action, left);
            }
        } else {
            activation.handleClick(x, y, mouseBtn, action, left);
        }
    }

    public void handleMove(double x, double y) {
        if (FerriteCore.activated) {
            for (var cat : categories) {
                cat.handleMove(x, y);
            }
        } else {
            activation.handleMove(x, y);
        }
    }

    public void handleButton(int key) {
        if (FerriteCore.activated) {
            for (var cat : categories) {
                cat.handleBind(key);
            }
        }
    }

    public boolean handleKey(int keyCode, int scanCode, int modifiers) {
        if (!FerriteCore.activated) {
            return activation.handleKey(keyCode, scanCode, modifiers);
        }
        // Forward key events to categories for option textbox handling
        for (var cat : categories) {
            if (cat.handleKeyPress(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    public boolean handleChar(char character) {
        if (!FerriteCore.activated) {
            return activation.handleChar(character);
        }
        // Forward char events to categories for option textbox handling
        for (var cat : categories) {
            if (cat.handleCharTyped(character, 0)) {
                return true;
            }
        }
        return false;
    }
}

