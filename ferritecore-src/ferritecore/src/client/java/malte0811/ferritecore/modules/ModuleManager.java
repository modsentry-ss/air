package malte0811.ferritecore.modules;

import malte0811.ferritecore.modules.client.*;
import malte0811.ferritecore.modules.crystal.*;
import malte0811.ferritecore.modules.mace.*;
import malte0811.ferritecore.modules.movement.AntiAFKModule;
import malte0811.ferritecore.modules.movement.JumpResetModule;
import malte0811.ferritecore.modules.movement.NoJumpDelayModule;
import malte0811.ferritecore.modules.movement.SprintModule;
import malte0811.ferritecore.modules.player.AutoArmorModule;
import malte0811.ferritecore.modules.player.AutoGappleModule;
import malte0811.ferritecore.modules.player.AutoPotModule;
import malte0811.ferritecore.modules.player.AutoRespawnModule;
import malte0811.ferritecore.modules.player.ChestStealerModule;
import malte0811.ferritecore.modules.render.FreecamModule;
import malte0811.ferritecore.modules.render.FullbrightModule;
import malte0811.ferritecore.modules.render.GlowESPModule;
import malte0811.ferritecore.modules.spear.LungeSwapModule;
import malte0811.ferritecore.modules.sword.AimAssistModule;
import malte0811.ferritecore.modules.sword.AutoClickerModule;
import malte0811.ferritecore.modules.sword.HitSelectModule;
import malte0811.ferritecore.modules.sword.ReachModule;
import malte0811.ferritecore.modules.sword.STapModule;
import malte0811.ferritecore.modules.sword.TriggerBotModule;
import malte0811.ferritecore.modules.sword.VelocityModule;
import malte0811.ferritecore.modules.sword.WTapModule;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.List;
import java.util.Optional;
import malte0811.ferritecore.config.Config;
import malte0811.ferritecore.config.ConfigManager;
import malte0811.ferritecore.gui.controls.options.Option;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModuleManager {
    public final ObjectArrayList<Module> maceModules = new ObjectArrayList<>();
    public final ObjectArrayList<Module> swordModules = new ObjectArrayList<>();
    public final ObjectArrayList<Module> spearModules = new ObjectArrayList<>();
    public final ObjectArrayList<Module> crystalModules = new ObjectArrayList<>();
    public final ObjectArrayList<Module> movementModules = new ObjectArrayList<>();
    public final ObjectArrayList<Module> renderModules = new ObjectArrayList<>();
    public final ObjectArrayList<Module> playerModules = new ObjectArrayList<>();
    public final ObjectArrayList<Module> clientModules = new ObjectArrayList<>();

    private final List<List<Module>> allModuleLists = List.of(
            maceModules, swordModules, spearModules, crystalModules, movementModules, renderModules, playerModules, clientModules
    );

    public final List<Module> getAllModules() {
        return new AbstractList<>() {
            @Override
            public Module get(int index) {
                for (List<Module> list : allModuleLists) {
                    if (index < list.size()) {
                        return list.get(index);
                    }
                    index -= list.size();
                }
                throw new IndexOutOfBoundsException();
            }

            @Override
            public int size() {
                int total = 0;
                for (List<Module> list : allModuleLists) {
                    total += list.size();
                }
                return total;
            }
        };
    }

    public ModuleManager() {
        maceModules.add(new BreachSwapModule());
        maceModules.add(new MaceBotModule());
//        maceModules.add(new AutoElytraBoostModule()); // WIP
        maceModules.add(new FarPearlModule());
        maceModules.add(new ShortPearlModule());
        maceModules.add(new ShieldBreakerModule());
        maceModules.add(new AutoProtectModule()); // WIP
        maceModules.add(new AutoBoostModule()); // half finished

        crystalModules.add(new AutoCrystalModule()); // WIP
        crystalModules.add(new AutoCrystalReplaceModule()); // WIP
        crystalModules.add(new AutoObsidianModule()); // WIP
        crystalModules.add(new AutoTotemModule()); // WIP
        crystalModules.add(new CrystalOptimizerModule()); // WIP

        swordModules.add(new TriggerBotModule());
        swordModules.add(new AutoClickerModule());
        swordModules.add(new HitSelectModule());
        swordModules.add(new AimAssistModule());
        swordModules.add(new ReachModule());
        swordModules.add(new VelocityModule());

        spearModules.add(new LungeSwapModule());

        movementModules.add(new WTapModule());
        movementModules.add(new STapModule()); // WIP
        movementModules.add(new JumpResetModule());
        movementModules.add(new NoJumpDelayModule());
        movementModules.add(new SprintModule());
        movementModules.add(new AntiAFKModule());

        renderModules.add(new FreecamModule());
        renderModules.add(new GlowESPModule());
        renderModules.add(new FullbrightModule());

        playerModules.add(new AutoGappleModule());
        playerModules.add(new AutoPotModule());
        playerModules.add(new ChestStealerModule());
        playerModules.add(new AutoArmorModule());
        playerModules.add(new AutoRespawnModule());

        clientModules.add(new ClickGUIModule());
        clientModules.add(new ArrayListModule());
        clientModules.add(new NotificationsModule());
        clientModules.add(new HUDModule());
        clientModules.add(new DestructModule());

//        // TODO: Register module enabled/bind state in config so state persists
//        // TODO: Make this talk to auth server to store (sharable?) user configs
//        try {
//            Config cfg = ConfigManager.getInstance().createConfig("modules");
//            for (Module m : getAllModules()) {
//                String base = "modules." + m.getId();
//                cfg.register(base + ".enabled", Boolean.class, m.isEnabled(), m::isEnabled, m::setEnabled);
//                cfg.register(base + ".bind", Integer.class, m.getBind(), m::getBind, m::setBind);
//
//                // Register each option for the module (if we can extract getter/setter)
//                List<Option> options = m.getOptions();
//                for (int i = 0; i < options.size(); i++) {
//                    Option opt = options.get(i);
//                    String optKey;
//                    try {
//                        String k = opt.getConfigKey();
//                        if (k != null && !k.isBlank()) {
//                            // use label-based key (sanitized) by default; module id prefix keeps it scoped per-module
//                            String sanitized = k.trim().toLowerCase().replaceAll("[^a-z0-9_\\-]", "_");
//                            optKey = base + ".option." + sanitized;
//                        } else {
//                            // fallback to index-based stable key
//                            optKey = base + ".option." + i;
//                        }
//                    } catch (Throwable ignored) {
//                        optKey = base + ".option." + i;
//                    }
//
//                    Supplier<Object> getter = null;
//                    Consumer<Object> setter = null;
//
//                    // Try to find Supplier/Consumer fields via reflection
//                    try {
//                        for (Field f : opt.getClass().getDeclaredFields()) {
//                            Class<?> t = f.getType();
//                            if (Supplier.class.isAssignableFrom(t)) {
//                                f.setAccessible(true);
//                                Object val = f.get(opt);
//                                if (val != null && getter == null) {
//                                    getter = () -> ((Supplier<?>) val).get();
//                                }
//                            }
//                            if (Consumer.class.isAssignableFrom(t)) {
//                                f.setAccessible(true);
//                                Object val = f.get(opt);
//                                if (val != null && setter == null) {
//                                    setter = (v) -> ((Consumer<Object>) val).accept(v);
//                                }
//                            }
//                        }
//                    } catch (Throwable ignored) {}
//
//                    // Fallback to public getValue / setValue methods
//                    if (getter == null) {
//                        try {
//                            Method mget = opt.getClass().getMethod("getValue");
//                            mget.setAccessible(true);
//                            getter = () -> {
//                                try { return mget.invoke(opt); } catch (Exception e) { return null; }
//                            };
//                        } catch (Throwable ignored) {}
//                    }
//                    if (setter == null) {
//                        try {
//                            // try setValue single-arg method
//                            for (Method meth : opt.getClass().getMethods()) {
//                                if (meth.getName().equals("setValue") && meth.getParameterCount() == 1) {
//                                    meth.setAccessible(true);
//                                    setter = (v) -> {
//                                        try { meth.invoke(opt, v); } catch (Exception ignored) {}
//                                    };
//                                    break;
//                                }
//                            }
//                        } catch (Throwable ignored) {}
//                    }
//
//                    // Special-case TextboxOption: use getTextbox().setValue
//                    if (setter == null) {
//                        try {
//                            Method gt = opt.getClass().getMethod("getTextbox");
//                            gt.setAccessible(true);
//                            Object textbox = gt.invoke(opt);
//                            if (textbox != null) {
//                                Method setVal = textbox.getClass().getMethod("setValue", String.class);
//                                setVal.setAccessible(true);
//                                setter = (v) -> {
//                                    try { setVal.invoke(textbox, String.valueOf(v)); } catch (Exception ignored) {}
//                                };
//                                if (getter == null) {
//                                    Method getVal = textbox.getClass().getMethod("getValue");
//                                    getVal.setAccessible(true);
//                                    getter = () -> {
//                                        try { return getVal.invoke(textbox); } catch (Exception e) { return null; }
//                                    };
//                                }
//                            }
//                        } catch (Throwable ignored) {}
//                    }
//
//                    // If we found both getter and setter, register the option
//                    if (getter != null && setter != null) {
//                        try {
//                            Object sample = getter.get();
//                            Class<?> type = sample != null ? sample.getClass() : String.class;
//                            //noinspection unchecked
//                            cfg.register(optKey, (Class) type, sample, getter, setter);
//                        } catch (Throwable ignored) {}
//                    }
//                }
//            }
//            // Load after registration to apply persisted values
//            ConfigManager.getInstance().loadAll();
//        } catch (Exception ignored) {
//        }
    }

    public void onTick() {
        for (Module module : getAllModules()) {
            module.onTick();
        }
    }

    public void onAttack(PlayerEntity playerEntity, World world, Entity entity, @Nullable EntityHitResult entityHitResult) {
        for (Module module : getAllModules()) {
            module.onAttack(playerEntity, world, entity, entityHitResult);
        }
    }

    public Optional<Module> getById(String name) {
        return getAllModules().stream()
                .filter(m -> m.getId().equalsIgnoreCase(name))
                .findFirst();
    }
}
