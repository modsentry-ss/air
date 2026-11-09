package malte0811.ferritecore.gui.controls;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.Utils;
import malte0811.ferritecore.gui.CatUI;
import malte0811.ferritecore.gui.GUIColors;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.TextboxOption;
import malte0811.ferritecore.modules.client.LoginModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Supplier;

public class ActivationCategory {
    public String name;
    public int originX, originY;
    public final Supplier<ItemStack> icon;
    private final LoginModule loginModule;
    private final List<Option> options;

    private static final int WIDTH = 155;
    private static final int HEADER_HEIGHT = 20;

    public ActivationCategory(String name, Supplier<ItemStack> icon) {
        this.name = name;
        this.icon = icon;
        this.originX = 5;
        this.originY = 0;
        this.loginModule = new LoginModule();
        this.options = loginModule.getOptions();
    }

    public void render(DrawContext ctx) {
        // Draw header background
        ctx.fill(originX, originY, originX + WIDTH, originY + HEADER_HEIGHT, GUIColors.headerBackground);

        // Draw header text and icon
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer,
            Text.literal(name).withColor(GUIColors.text), originX + 32, originY + 7, GUIColors.text);
        ctx.drawItem(icon.get(), originX + 5, originY + 2);

        // Calculate total height for options
        int totalHeight = 0;
        for (Option option : options) {
            totalHeight += option.getHeight();
        }

        // Draw module area (always expanded)
        int moduleY = originY + HEADER_HEIGHT;
        int moduleHeight = 18 + totalHeight + 45; // module button + options + activate button + status message
        ctx.fill(originX, moduleY, originX + WIDTH, moduleY + moduleHeight, GUIColors.moduleBackground);

        // Draw module name "Login"
        int color = GUIColors.text;
        Text labelText = Text.literal(loginModule.getName()).withColor(color);
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, labelText, originX + 10, moduleY + 5, GUIColors.text);

        // Draw separator line
        CatUI.rect(originX + 13, moduleY + 15, originX + WIDTH - 11, moduleY + 16, GUIColors.separator);

        // Draw options (username, password)
        int optionY = moduleY + 18;
        for (Option option : options) {
            option.render(ctx, originX + 5, optionY);
            optionY += option.getHeight();
        }

        // Draw activate button
        int buttonY = optionY + 5;
        int buttonHeight = 18;
        boolean canActivate = canActivate();
        boolean isAuthenticating = loginModule.isAuthenticating();
        int buttonColor = isAuthenticating ? GUIColors.buttonProgress : (canActivate ? GUIColors.buttonActive : GUIColors.buttonInactive);
        int textColor = GUIColors.text;

        ctx.fill(originX + 10, buttonY, originX + WIDTH - 10, buttonY + buttonHeight, buttonColor);
        ctx.drawStrokedRectangle(originX + 10, buttonY, WIDTH - 20, buttonHeight, GUIColors.border);

        String buttonText = isAuthenticating ? "Authenticating..." : (canActivate ? "Login" : "Enter credentials");
        int textWidth = FerriteCore.mc.textRenderer.getWidth(buttonText);
        int textX = originX + (WIDTH - textWidth) / 2;
        ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, Text.literal(buttonText).withColor(textColor),
            textX, buttonY + 5, GUIColors.text);

        // Draw status message
        String statusMessage = loginModule.getStatusMessage();
        if (statusMessage != null && !statusMessage.isEmpty()) {
            int statusY = buttonY + buttonHeight + 5;
            int statusColor = statusMessage.contains("Error") || statusMessage.contains("failed") || statusMessage.contains("Failed")
                ? GUIColors.statusError
                : (statusMessage.contains("Authenticated") ? GUIColors.statusSuccess : GUIColors.text);
            int statusWidth = FerriteCore.mc.textRenderer.getWidth(statusMessage);
            int statusX = originX + (WIDTH - statusWidth) / 2;
            ctx.drawTextWithShadow(FerriteCore.mc.textRenderer, Text.literal(statusMessage).withColor(statusColor),
                statusX, statusY, GUIColors.text);
        }

        // Draw border around entire module
        ctx.drawStrokedRectangle(originX + 5, moduleY, WIDTH - 10, moduleHeight, GUIColors.border);
    }

    private boolean canActivate() {
        String username = loginModule.getUsernameOption().getValue();
        String password = loginModule.getPasswordOption().getValue();
        return username != null && !username.trim().isEmpty()
            && password != null && !password.trim().isEmpty();
    }

    private boolean movingCategory = false;
    private double movingOffsetX = 0;
    private double movingOffsetY = 0;

    public void handleClick(double x, double y, int mouseBtn, int clickType, boolean leftHeld) {
        boolean insideHeader = Utils.isInside(originX, originY, originX + WIDTH, originY + HEADER_HEIGHT, x, y);

        if (insideHeader && clickType == GLFW.GLFW_PRESS && mouseBtn == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            movingCategory = true;
            movingOffsetX = x - originX;
            movingOffsetY = y - originY;
        }

        if (!leftHeld) {
            movingCategory = false;
        }

        // Handle option clicks - always forward to all options so they can unfocus when clicked outside
        int moduleY = originY + HEADER_HEIGHT;
        int optionY = moduleY + 18;

        for (Option option : options) {
            option.handleClick(x, y, mouseBtn, clickType);
            optionY += option.getHeight();
        }

        // Handle activate button click
        if (clickType == GLFW.GLFW_PRESS && mouseBtn == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            int buttonY = optionY + 5;
            int buttonHeight = 18;
            if (Utils.isInside(originX + 10, buttonY, originX + WIDTH - 10, buttonY + buttonHeight, x, y)) {
                if (canActivate()) {
                    loginModule.tryActivate();
                }
            }
        }
    }

    public void handleMove(double x, double y) {
        if (movingCategory) {
            originX = (int) (x - movingOffsetX);
            originY = (int) (y - movingOffsetY);
        }
    }

    public boolean handleKey(int keyCode, int scanCode, int modifiers) {
        return loginModule.handleKey(keyCode, scanCode, modifiers);
    }

    public boolean handleChar(char character) {
        return loginModule.handleChar(character);
    }

    public LoginModule getLoginModule() {
        return loginModule;
    }
}

