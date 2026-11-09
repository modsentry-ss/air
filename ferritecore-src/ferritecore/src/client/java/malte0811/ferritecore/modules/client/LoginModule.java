package malte0811.ferritecore.modules.client;

import malte0811.ferritecore.FerriteCore;
import malte0811.ferritecore.auth.AuthClient;
import malte0811.ferritecore.gui.controls.options.Option;
import malte0811.ferritecore.gui.controls.options.TextboxOption;
import malte0811.ferritecore.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LoginModule implements Module {
    private final TextboxOption usernameOption;
    private final TextboxOption passwordOption;
    
    private AuthClient authClient;
    private String statusMessage = "";
    private boolean isAuthenticating = false;

    public LoginModule() {
        usernameOption = new TextboxOption("username", "Username:", "", false, null);
        passwordOption = new TextboxOption("password", "Password:", "", true, null);
    }

    @Override public String getId() { return "login"; }
    @Override public String getName() { return "Login"; }

    @Override public boolean isEnabled() { return false; }
    @Override public void setEnabled(boolean enabled) {
        // Try to activate when user clicks
        tryActivate();
    }

    @Override public int getBind() { return GLFW.GLFW_KEY_UNKNOWN; }
    @Override public void setBind(int bind) { }

    @Override public void onTick() { }
    @Override public void onRender(DrawContext ctx) { }
    @Override public void onAttack(PlayerEntity attacker, World world, Entity victim, @Nullable EntityHitResult hitResult) { }

    @Override
    public List<Option> getOptions() {
        return List.of(usernameOption, passwordOption);
    }

    public boolean tryActivate() {
        if (!AuthClient.getAuthEnabled()) {
            FerriteCore.activated = true;
            return true;
        }

        String username = usernameOption.getValue();
        String password = passwordOption.getValue();

        // Check if both fields have values
        if (username == null || username.trim().isEmpty()) {
            statusMessage = "Please enter username";
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            statusMessage = "Please enter password";
            return false;
        }
        
        // Don't start another auth if one is in progress
        if (isAuthenticating) {
            statusMessage = "Authentication in progress...";
            return false;
        }

        // Start authentication in background thread
        isAuthenticating = true;
        statusMessage = "Authenticating...";
        
        CompletableFuture.runAsync(() -> {
            try {
                // Create new auth client for each attempt
                authClient = new AuthClient();
                
                // Step 1: Verify server identity
                if (!authClient.verifyServer()) {
                    statusMessage = "Failed to verify server";
                    isAuthenticating = false;
                    return;
                }
                
                // Step 2: Authenticate with credentials
                String hardwareId = authClient.getHardwareId();
                AuthClient.AuthResult result = authClient.authenticate(username.trim(), password, hardwareId);
                
                if (result.ok) {
                    statusMessage = "Authenticated!";
                    FerriteCore.activated = true;
                } else {
                    statusMessage = result.message != null && !result.message.isEmpty() 
                        ? result.message 
                        : "Authentication failed";
                }
                
            } catch (java.net.ConnectException e) {
                statusMessage = "Cannot connect to server";
//                e.printStackTrace();
            } catch (java.net.SocketTimeoutException e) {
                statusMessage = "Cannot connect to server";
//                e.printStackTrace();

            } catch (Exception e) {
                statusMessage = "Cannot connect to server";
//                statusMessage = "Error: " + e.getMessage();
//                e.printStackTrace();
            } finally {
                isAuthenticating = false;
            }
        });

        return false; // Will be set asynchronously
    }

    public TextboxOption getUsernameOption() {
        return usernameOption;
    }

    public TextboxOption getPasswordOption() {
        return passwordOption;
    }
    
    public String getStatusMessage() {
        return statusMessage;
    }
    
    public boolean isAuthenticating() {
        return isAuthenticating;
    }

    public boolean hasTextboxFocused() {
        return usernameOption.isFocused() || passwordOption.isFocused();
    }

    public boolean handleKey(int keyCode, int scanCode, int modifiers) {
        if (usernameOption.isFocused()) {
            return usernameOption.handleKeyPress(keyCode, scanCode, modifiers);
        }
        if (passwordOption.isFocused()) {
            return passwordOption.handleKeyPress(keyCode, scanCode, modifiers);
        }
        return false;
    }

    public boolean handleChar(char character) {
        if (usernameOption.isFocused()) {
            return usernameOption.handleCharTyped(character, 0);
        }
        if (passwordOption.isFocused()) {
            return passwordOption.handleCharTyped(character, 0);
        }
        return false;
    }
}

