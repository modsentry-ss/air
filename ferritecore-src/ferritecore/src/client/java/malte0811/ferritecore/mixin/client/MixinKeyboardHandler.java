package malte0811.ferritecore.mixin.client;

import malte0811.ferritecore.FerriteCore;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Keyboard.class)
public abstract class MixinKeyboardHandler {
	@Inject(at = @At("HEAD"), method = "onKey", cancellable = true)
	public void onKeyPressed(long window, int action, KeyInput input, CallbackInfo ci) {
		if (FerriteCore.destructed) return;
		if (window != FerriteCore.mc.getWindow().getHandle()) {
			return;
		}
		if (FerriteCore.mc.player == null) return;

		if (FerriteCore.mc.inGameHud.getChatHud().isChatFocused()) {
			return;
		}

        var key = input.key();
		if (key == GLFW.GLFW_KEY_UNKNOWN) return;

		// Handle text input for textboxes when UI is shown
		if (FerriteCore.showUI && action == GLFW.GLFW_PRESS) {
			if (FerriteCore.catUI.handleKey(key, input.scancode(), input.modifiers())) {
				ci.cancel();
				return;
			}
		}

		if (action == GLFW.GLFW_RELEASE) {
			return;
		}

		for (var mod : FerriteCore.moduleManager.getAllModules()) {
			var bind = mod.getBind();
			if (bind == key) {
				mod.setEnabled(!mod.isEnabled());
				if (!Objects.equals(mod.getName(), "ClickGUI") && FerriteCore.moduleManager.getById("notifications").get().isEnabled()) {
                    Text.literal(mod.getName() + " is now " + (mod.isEnabled() ? "Enabled" : "Disabled")).setStyle(Style.EMPTY.withColor(mod.isEnabled() ? 0x00ff00 : 0xff0000));

                }
			}
		}

		FerriteCore.catUI.handleButton(key);
		if (FerriteCore.showUI) {
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "onChar", cancellable = true)
	public void onCharTyped(long window, CharInput input, CallbackInfo ci) {
		if (FerriteCore.destructed) return;
		if (window != FerriteCore.mc.getWindow().getHandle()) {
			return;
		}
		if (FerriteCore.mc.player == null) return;

		if (FerriteCore.mc.inGameHud.getChatHud().isChatFocused()) {
			return;
		}

		// Handle character input for textboxes when UI is shown
		if (FerriteCore.showUI) {
			if (FerriteCore.catUI.handleChar((char) input.codepoint())) {
				ci.cancel();
			}
		}
	}
}