package malte0811.ferritecore.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftClientIMixin {
    @Accessor("itemUseCooldown")
    void setItemUseCooldown(int cooldown);

    @Accessor("mouse")
    Mouse getMouse();

    @Invoker("doItemUse")
    void invokeDoItemUse();

    @Invoker("doAttack")
    boolean invokeDoAttack();
}
