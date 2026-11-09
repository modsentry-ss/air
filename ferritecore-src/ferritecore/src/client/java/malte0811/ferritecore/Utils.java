package malte0811.ferritecore;

import malte0811.ferritecore.mixin.client.MinecraftClientIMixin;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static malte0811.ferritecore.FerriteCore.TICKS;
import static malte0811.ferritecore.FerriteCore.mc;

public class Utils {
    public static boolean isInside(double boxx1, double boxy1, double boxx2, double boxy2, double clickx, double clicky) {
        double minX = Math.min(boxx1, boxx2);
        double maxX = Math.max(boxx1, boxx2);
        double minY = Math.min(boxy1, boxy2);
        double maxY = Math.max(boxy1, boxy2);

        return clickx >= minX && clickx <= maxX && clicky >= minY && clicky <= maxY;
    }


    public static void runLater(Runnable task, long delayTicks) {
        AtomicBoolean isCancelled = new AtomicBoolean(false);
        long targetTick = TICKS + delayTicks;

        FerriteCore.tickEndCallbacks.put(targetTick, task);
//        Flashback.tickEndCallbacks.put(targetTick,() -> {
//            if (isCancelled.get()) return;
//            if (TICKS >= targetTick) {
//                task.run();
//                isCancelled.set(true);
//            }
//        });
    }


    public static boolean isWearingElytra() {
//        return mc.player != null && mc.player.getInventory().equipment.get(EquipmentSlot.CHEST).getItem() == Items.MACE;
        return mc.player != null && mc.player.isGliding();
    }

    public static boolean canCrit() {
        boolean isFalling = mc.player.fallDistance > 0.2f;
        if (!isFalling) {
            return false;
        }
//
//        float weaponCooldown = mc.player.getAttackCooldownProgress(0.5f);
//        boolean canCritWithWeapon = weaponCooldown == 1.0f;
//
//        if (!canCritWithWeapon) {
//            msg("crit: weapon cooldown: " + weaponCooldown, true);
//            return false;
//        }

        return true;
    }


    public static double getDistanceToGround() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return -1;

        Vec3d eyePos = mc.player.getEyePos();
        Vec3d end = eyePos.subtract(0, 100, 0);

        var hit = mc.world.raycast(new RaycastContext(eyePos, end,
                RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player
        ));

        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            return eyePos.y - hit.getPos().y;
        }

        return -1;
    }


    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static float rand(float target, float missallowed) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return rand.nextFloat(target - missallowed, target + missallowed);
    }


    public static boolean hasStrengthTwo() {
        final boolean[] hasStrengthTwo = {false};
        mc.player.getStatusEffects().forEach(effect -> {
            if (effect.getEffectType().equals(StatusEffects.STRENGTH) && effect.getAmplifier() >= 2) {
                hasStrengthTwo[0] = true;
            }
        });
        return hasStrengthTwo[0];
    }


    public static int findDensityMace() {
        int lastMace = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof MaceItem) {
                lastMace = i;
                if (_isItemEnchanted(stack, Enchantments.DENSITY, 1)) {
                    return i;
                }
            }
        }

        return lastMace;
    }

    public static int findBreachMace() {
        int lastMace = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof MaceItem) {
                lastMace = i;
                if (_isItemEnchanted(stack, Enchantments.BREACH, 1)) {
                    return i;
                }
            }
        }
        return lastMace;
    }

    public static int findSlotWithItem(Item item, @Nullable RegistryKey<Enchantment> wantedEnchant) {
        int lastInstance = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == item) {
                lastInstance = i;
                if (wantedEnchant == null) {
                    return i;
                } else if (_isItemEnchanted(stack, wantedEnchant, 1)) {
                    return i;
                }
            }
        }
        return lastInstance;
    }

    public static int findSlotWithSword() {
        int slot = -1;
        Item[] types = new Item[]{Items.NETHERITE_SWORD, Items.DIAMOND_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.COPPER_SWORD, Items.STONE_SWORD, Items.WOODEN_SWORD};
        for (var type : types) {
            slot = findSlotWithItem(type, null);
            if (slot != -1) {
                break;
            }
        }
        return slot;
    }

    public static int findSlotWithAxe() {
        int slot = -1;
        Item[] types = new Item[]{Items.NETHERITE_AXE, Items.DIAMOND_AXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.COPPER_AXE, Items.STONE_AXE, Items.WOODEN_AXE};
        for (var type : types) {
            slot = findSlotWithItem(type, null);
            if (slot != -1) {
                break;
            }
        }
        return slot;
    }

    public static int findSlotWithSpear(boolean favorSpeed) {
        int slot = -1;
        Item[] types = new Item[]{};
        if (favorSpeed) {
            types = new Item[]{Items.WOODEN_SPEAR, Items.STONE_SPEAR, Items.COPPER_SPEAR,
                    Items.IRON_SPEAR, Items.GOLDEN_SPEAR, Items.DIAMOND_SPEAR, Items.NETHERITE_SPEAR};
        } else {
            types = new Item[]{Items.NETHERITE_SPEAR, Items.DIAMOND_SPEAR, Items.IRON_SPEAR,
                    Items.GOLDEN_SPEAR, Items.COPPER_SPEAR, Items.STONE_SPEAR, Items.WOODEN_SPEAR};
        }
        for (var type : types) {
            slot = findSlotWithItem(type, null);
            if (slot != -1) {
                break;
            }
        }
        return slot;
    }

    public static boolean _isItemEnchanted(@NotNull ItemStack item, @NotNull RegistryKey<Enchantment> enchantment, int minLevel) {
        ItemEnchantmentsComponent enchantments = item.getComponents().get(DataComponentTypes.ENCHANTMENTS);
        if (enchantments != null) {
            for (var entry : enchantments.getEnchantmentEntries()) {
                if (entry.getKey().matchesKey(enchantment))
                    if (entry.getIntValue() > minLevel) {
                        return true;
                    }
            }
        }
        return false;
    }

    public static boolean canAttack(boolean inAir) {
        Item item = mc.player.getActiveItem().getItem();
        if (item instanceof MaceItem maceItem) {
            return true;
        }
        if (inAir) {
            boolean forSword = mc.player.getAttackCooldownProgress(0.5f) < 0.45f;
            return forSword;
        } else {
            boolean forSword = mc.player.getAttackCooldownProgress(0.5f) < 0.92f;
            return forSword;
        }
    }

    public static boolean isAxe(Item item) {
        return item == Items.WOODEN_AXE || item == Items.COPPER_AXE || item == Items.STONE_AXE || item == Items.IRON_AXE || item == Items.GOLDEN_AXE || item == Items.DIAMOND_AXE || item == Items.NETHERITE_AXE;
    }

    public static boolean isSword(Item item) {
        return item == Items.WOODEN_SWORD || item == Items.COPPER_SWORD || item == Items.STONE_SWORD || item == Items.IRON_SWORD || item == Items.GOLDEN_SWORD || item == Items.DIAMOND_SWORD || item == Items.NETHERITE_SWORD;
    }

    public static void smoothPitchTransition(float start, float end, int totalTicks) {
        for (int i = 0; i <= totalTicks; i++) {
            int delay = i;
            float t = (float) i / totalTicks;
            float interpolated = lerp(start, end, t);
            Utils.runLater(() -> {
                mc.player.setPitch(interpolated);
                mc.player.tickMovement();
            }, delay);
        }
    }

    public static void attack() {
        // shared attack gate (ported from MacePvP): HitSelect only throws when
        // the trade math favors u — target player mid-swing -> hold the hit
        if (malte0811.ferritecore.modules.sword.HitSelectModule.shouldBlockAttack()) return;
        ((MinecraftClientIMixin) mc).invokeDoAttack();
//        var wasPressed = mc.options.attackKey.isPressed();
//        mc.options.attackKey.setPressed(false);
//        mc.options.attackKey.setPressed(true);
//        Utils.runLater(() -> {
//            mc.options.attackKey.setPressed(false);
//        }, 1);
//        if (wasPressed) {
//            Utils.runLater(() -> {
//                mc.options.attackKey.setPressed(true);
//            }, 2);
//        }
//        if (mc.isPaused()) mc.openGameMenu(false);
//        if (mc.player != null && mc.interactionManager != null) {
//            HitResult hit = mc.crosshairTarget;
//
//            if (hit != null) {
//                if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
//                    EntityHitResult entityHit = (EntityHitResult) mc.crosshairTarget;
//                    Entity target = entityHit.getEntity();
//
//                    mc.interactionManager.attackEntity(mc.player, target);
//                    mc.player.swingHand(Hand.MAIN_HAND);
//                }
//            } else {
//                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
//                mc.player.swingHand(Hand.MAIN_HAND);
//            }
//        }
    }

    public static void useItem() {
        return; // UNSTABLE
//        MinecraftClient client = MinecraftClient.getInstance();
//        if (client.isPaused()) client.openGameMenu(false);
//
//        if (client.player != null && client.interactionManager != null) {
//            msg("Using " + client.player.getMainHandStack().getItem(), true);
//            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
//            client.player.swingHand(Hand.MAIN_HAND);
//        }
    }

    public static void useBlock(BlockPos pos) {

    }


    public static void placeBlock() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isPaused()) client.openGameMenu(false);

        if (client.player != null && client.interactionManager != null && client.crosshairTarget != null) {
            if (client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                BlockHitResult bhr = (BlockHitResult) client.crosshairTarget;

                client.player.getMainHandStack();
                client.interactionManager.interactBlock(
                        client.player,
                        Hand.MAIN_HAND,
                        bhr
                );
                client.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }



    //    public static LivingEntity getTarget(double distanceFromPlayer, boolean exactDistance) {
//        if (distanceFromPlayer == -1) {
//            var ent = MinecraftClient.getInstance().targetedEntity;
//            return ent instanceof LivingEntity ? (LivingEntity) ent : null;
//        }
//        Vec3d eyePos = mc.player.getEyePos();
//        Vec3d lookVec = mc.player.getRotationVec(1.0f);
//        Vec3d endPos = eyePos.add(lookVec.multiply(distanceFromPlayer));
//        Entity closestEntity = null;
//        double closestDistance = Double.MAX_VALUE;
//
//
//        for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
//            if (player instanceof LivingEntity && player != mc.player) {
//                Vec3d entityPos = player.getBoundingBox().getCenter();
//                Vec3d toEntity = entityPos.subtract(eyePos).normalize();
//                double dot = lookVec.dotProduct(toEntity);
//                if (dot > Math.cos(30)) {
//                    double distance = mc.world.raycast(new RaycastContext(eyePos, entityPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getPos().distanceTo(eyePos);
//                    if (distance <= distanceFromPlayer) {
//                        if (exactDistance) {
//                            if (isInLine(eyePos, endPos, entityPos, 0.5)) {
//                                if (distance < closestDistance) {
//                                    closestEntity = player;
//                                    closestDistance = distance;
//                                }
//                            }
//                        } else {
//                            if (distance < closestDistance) {
//                                closestEntity = player;
//                                closestDistance = distance;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        if (closestEntity != null) {
//            var eid = closestEntity.getUuid();
//            if (mc.getNetworkHandler().getPlayerListEntry(eid) != null) {
//                return (LivingEntity) closestEntity;
//            }aa
//        }
//        return null;
//    }
    public static LivingEntity getTarget(double distanceFromPlayer, boolean exactDistance) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        ClientWorld world = mc.world;

        if (player == null || world == null) return null;

        Vec3d cameraPos = player.getCameraPosVec(1.0f);
        Vec3d rotationVec = player.getRotationVec(1.0f);
        Vec3d reachVec = cameraPos.add(rotationVec.multiply(distanceFromPlayer));

        List<LivingEntity> entities = world.getEntitiesByClass(
                LivingEntity.class,
                player.getBoundingBox().expand(distanceFromPlayer),
                entity -> entity != player && entity.isAlive()
        );

        LivingEntity closestEntity = null;
        double closestDistanceSq = Double.MAX_VALUE;

        for (Entity entity : entities) {
            Optional<Vec3d> hitResult = entity.getBoundingBox().raycast(cameraPos, reachVec);
            if (hitResult.isPresent()) {
                double hitDistanceSq = cameraPos.squaredDistanceTo(hitResult.get());
                if (exactDistance) {
                    double dist = cameraPos.distanceTo(hitResult.get());
                    if (Math.abs(dist - distanceFromPlayer) > 0.5) continue;
                }
                if (hitDistanceSq < closestDistanceSq) {
                    closestEntity = (LivingEntity) entity;
                    closestDistanceSq = hitDistanceSq;
                }
            }
        }

        return closestEntity;
    }

    private static boolean isInLine(Vec3d start, Vec3d end, Vec3d point, double threshold) {
        Vec3d lineVec = end.subtract(start);
        Vec3d pointVec = point.subtract(start);
        double t = pointVec.dotProduct(lineVec) / lineVec.dotProduct(lineVec);
        if (t < 0 || t > 1) return false;
        Vec3d projection = start.add(lineVec.multiply(t));
        return point.distanceTo(projection) <= threshold;
    }


    public static double calculateTotalDamage(ClientPlayerEntity player) {
        double baseDamage = player.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
        double weaponDamage = getWeaponDamage(player.getMainHandStack());

        StatusEffectInstance strength = player.getStatusEffect(StatusEffects.STRENGTH);
        double strengthBonus = strength != null ? 3.0D * (strength.getAmplifier() + 1) : 0.0D;

        StatusEffectInstance weakness = player.getStatusEffect(StatusEffects.WEAKNESS);
        double weaknessPenalty = weakness != null ? 4.0D * (weakness.getAmplifier() + 1) : 0.0D;

        // Total damage, ensuring it doesn't go negative
        return Math.max(0.0D, baseDamage + weaponDamage + strengthBonus - weaknessPenalty);
    }

    public static double getWeaponDamage(ItemStack item) {
        if (item.isEmpty()) {
            return 0.0D;
        }

        final double[] totalDamage = {0.0D};

        // Iterate through modifiers and sum the damage values
        item.applyAttributeModifiers(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (EntityAttributes.ATTACK_DAMAGE.equals(attribute)) {
                totalDamage[0] += modifier.value();
            }
        });

        return totalDamage[0];
    }

    public static void destroyCrystal(Entity crystal) {
        crystal.remove(Entity.RemovalReason.KILLED);
        crystal.emitGameEvent(GameEvent.ENTITY_DIE);
    }

    public static BlockState getBlockState(BlockPos pos) {
        return mc.player.getEntityWorld().getBlockState(pos);
    }

    public static boolean canPlaceCrystal(BlockPos pos) {
        var mc = FerriteCore.mc;
        if (mc.player == null || mc.player.getEntityWorld() == null) return false;
        var world = mc.player.getEntityWorld();

        if (getBlockState(pos).getBlock().asItem() != Items.BEDROCK && getBlockState(pos).getBlock().asItem() != Items.OBSIDIAN) return false;

        if (getBlockState(pos.add(0, 1, 0)).getBlock().asItem() != Items.AIR) return false;

        Box box = new Box(
                pos.getX(), pos.getY() + 1, pos.getZ(),
                pos.getX() + 1, pos.getY() + 1 + 2.0f, pos.getZ() + 1
        );
        var list = mc.world.getEntitiesByClass(Entity.class, box, e -> true);

        if (!list.isEmpty()) return false;

        return true;
    }

    public static List<Entity> getEntitiesNearPos(BlockPos center, double radius) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return List.of();

        // Build an axis-aligned bounding box around the center
        Box box = new Box(
                center.getX() - radius, center.getY() - 1.0f, center.getZ() - radius,
                center.getX() + radius, center.getY() + 1.0f, center.getZ() + radius
        );

        // Query entities
        var list = client.world.getEntitiesByClass(Entity.class, box, e -> true);
        list = list.stream().filter(e -> e.getBlockPos().getY() >= center.getY()).toList();
        return list;
    }

    public static void setHeldItemSlot(int slot) {
        if (mc.player == null || getHeldItemSlot() == slot) return;
        mc.player.getInventory().setSelectedSlot(slot);
        mc.player.getInventory().markDirty();
    }

    public static int getHeldItemSlot() {
        if (mc.player == null) return -1;
        return mc.player.getInventory().getSelectedSlot();
    }
}
