package malte0811.ferritecore.modules;

import malte0811.ferritecore.gui.controls.options.Option;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public interface Module {
    String getId();
    String getName();

    boolean isEnabled();
    void setEnabled(boolean enabled);

    int getBind();
    void setBind(int bind);

    void onTick();
    void onRender(DrawContext ctx);
    void onAttack(PlayerEntity attacker, World world, Entity victim, @Nullable EntityHitResult hitResult);

    default List<Option> getOptions() {
        return Collections.emptyList();
    }
}
