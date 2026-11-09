# FerriteCore (Judgment Client) - Modules Documentation

This document provides detailed information about all modules available in the client, organized by category.

---

## Table of Contents

- [Mace Modules](#mace-modules)
- [Sword Modules](#sword-modules)
- [Spear Modules](#spear-modules)
- [Crystal Modules](#crystal-modules)
- [Movement Modules](#movement-modules)
- [Render Modules](#render-modules)
- [Client Modules](#client-modules)

---

## Mace Modules

Modules designed for mace-based PvP combat.

### Auto BreachSwap

**ID:** `autobreachswap`

Automatically swaps to a Breach-enchanted mace to attack, then swaps back to your preferred weapon (sword/axe). Useful for applying the Breach armor-penetration effect with a lower attack cooldown.

**Logic:**
- Waits for attack cooldown and crit conditions
- Checks if target is within reach and not in hurt animation
- Swaps to Breach mace, attacks, then swaps back to sword/axe
- Respects cooldown between attacks

**Options:**
| Option | Description | Default | Min | Max | Step |
|--------|-------------|---------|-----|-----|------|
| Prefer Sword | Prefer sword over axe for swap-back | `true` | - | - | - |
| Chance | Attack chance percentage | `100%` | 0 | 100 | 5 |
| Reach | Attack reach distance | `3.0` | 1.0 | 6.0 | 0.5 |
| Max Ground Dist | Maximum distance to ground | `6.0` | 1.0 | 20.0 | 1.0 |
| Cooldown | Cooldown in ticks | `3` | 0 | 10 | 1 |

**Default Keybind:** None

---

### Mace Bot

**ID:** `macebot`

Main module and what the client started from. Automatically attacks with a mace when falling, intelligently choosing between Breach and Density enchantments based on fall distance and strength effects.

**Logic:**
- Monitors fall distance and determines optimal mace type
- Uses Breach mace for shorter falls (more armor penetration)
- Uses Density mace for longer falls (more raw damage)
- Adjusts breach/density picking thresholds based on whether player has Strength II effect

**Options:**
| Option | Description | Default | Min | Max | Step |
|--------|-------------|---------|-----|-----|------|
| Chance | Attack chance percentage | `100%` | 0 | 100 | 5 |
| Reach | Attack reach distance | `3.0` | 1.0 | 6.0 | 0.5 |
| Min Fall | Minimum fall distance to attack | `2.0` | 0.5 | 10.0 | 0.5 |
| Str Thresh | Fall threshold with Strength II | `6.0` | 2.0 | 20.0 | 1.0 |
| No-Str Thresh | Fall threshold without Strength II | `8.0` | 2.0 | 20.0 | 1.0 |

**Default Keybind:** None

---

### Far Pearl

**ID:** `farpearl`

Executes a "far pearl" technique - throws an ender pearl followed by a wind charge for maximum distance pearl throws.

**Logic:**
- Activates when looking nearly straight up (configurable pitch)
- Throws ender pearl first
- Throws wind charge after configurable delay for boost
- Optionally disables itself after use

**Options:**
| Option | Description | Default | Min | Max | Step |
|--------|-------------|---------|-----|-----|------|
| Disable After Use | Disable module after executing | `true` | - | - | - |
| Wind Delay | Ticks delay before wind charge | `3` | 0 | 10 | 1 |
| Min Pitch | Minimum pitch (look angle) | `-89°` | -90 | -60 | 1 |

**Default Keybind:** None

---

### Short Pearl

**ID:** `shortpearl`

Similar to Far Pearl but with shorter delay, optimized for close-range tactical pearling.

**Logic:**
- Same as Far Pearl but with shorter default wind charge delay
- Better for quick repositioning in combat

**Options:**
| Option | Description | Default | Min | Max | Step |
|--------|-------------|---------|-----|-----|------|
| Disable After Use | Disable module after executing | `true` | - | - | - |
| Wind Delay | Ticks delay before wind charge | `1` | 0 | 10 | 1 |
| Min Pitch | Minimum pitch (look angle) | `-89°` | -90 | -60 | 1 |

**Default Keybind:** None

---

### Shield Breaker

**ID:** `shieldbreaker`

Automatically swaps to an axe and attacks when an enemy is blocking with a shield, disabling their shield.
If you are continuing/using this codebase, I'd recommend you add a (randomized) delay before attacking to simulate human behavior.

**Logic:**
- Detects when target is blocking
- Swaps to axe in hotbar
- Waits for ground delay (if configured)
- Attacks to disable shield
- Swaps back to sword

**Options:**
| Option | Description | Default | Min | Max | Step |
|--------|-------------|---------|-----|-----|------|
| Reach | Attack reach distance | `3.0` | 1.0 | 6.0 | 0.5 |
| Ground Delay | Ticks delay when on ground | `3` | 0 | 10 | 1 |
| Cooldown (ms) | Cooldown in milliseconds | `100` | 0 | 500 | 50 |

**Default Keybind:** None

---

### Auto Protect

**ID:** `autoprotect`

*Work in Progress* - Intended behavior is to detect when another player is about to mace you, so the client would automatically hold up your shield and potentially either look, or only block when looking, in their direction.

**Default Keybind:** None

---

### Wind Charge Booster

**ID:** `autowindchargepunch`

Automatically punches your own wind charges to gain extra boost while falling.
WARNING: Even though this is a vanilla feature that you can do, it relies on the fact that the wind charge entities on your client's side are slightly ahead of your player position on the server side, so using this module may be considered cheating by some servers.

**Logic:**
- Detects when looking down and falling
- Automatically attacks wind charge entities in crosshair
- Random cooldown variation for natural behavior

**Options:**
| Option | Description | Default | Min | Max | Step |
|--------|-------------|---------|-----|-----|------|
| Min Fall | Minimum fall distance | `0.5` | 0 | 5 | 0.5 |
| Min Pitch | Minimum pitch (looking down) | `80°` | 60 | 90 | 5 |
| CD Variation | Random cooldown variation (ticks) | `2` | 0 | 5 | 1 |

**Default Keybind:** None

---

## Sword Modules

Modules for traditional sword-based PvP combat.

### Trigger Bot

**ID:** `triggerbot`

Automatically attacks entities in your crosshair when attack key is held (or permanently if enabled).

**Logic:**
- Waits for proper attack cooldown based on weapon type
- Checks if player can crit (not on ground + falling)
- Different cooldown thresholds for swords, axes, and maces
- Only attacks living entities with health

**Options:**
| Option | Description | Default | Min | Max | Step |
|--------|-------------|---------|-----|-----|------|
| Permanent Mode | Always attack (don't require attack key) | `false` | - | - | - |
| Reach | Attack reach distance | `3.0` | 1.0 | 6.0 | 0.5 |

**Default Keybind:** None

---

### W-Tap

**ID:** `wtap`

Automatically performs W-tapping (releasing and re-pressing W) after attacks to reset sprint and deal extra knockback.

**Logic:**
- Triggers on attack while sprinting
- Releases W key after configurable reaction time
- Re-presses W after configurable hold length
- Randomized timings for natural behavior

**Options:**
| Option | Description | Default | Min | Max | Step |
|--------|-------------|---------|-----|-----|------|
| Chance | Activation chance | `100%` | 0 | 100 | 5 |
| Hold Length | W release duration (ms) | `100` | 0 | 400 | 10 |
| Hold Variance | Random variance for hold (ms) | `100` | 0 | 200 | 10 |
| Reaction | Delay before W release (ms) | `10` | 0 | 50 | 5 |
| Reaction Var | Random variance for reaction (ms) | `20` | 0 | 30 | 5 |

**Default Keybind:** None

---

### S-Tap

**ID:** `stap`

*Work in Progress* - Performs S-tapping (pressing S momentarily) after attacks for knockback manipulation.

**Logic:**
- Triggers on attack
- Presses S key momentarily
- Restores previous movement state after delay

**Options:**
| Option | Description | Default | Min | Max | Step |
|--------|-------------|---------|-----|-----|------|
| Delay | S-tap duration (ticks) | `5` | 1 | 15 | 1 |

**Default Keybind:** None

---

## Spear Modules

Modules for spear-based combat (Minecraft 1.21.11+).

### Lunge Swap

**ID:** `lungeswap`

Automatically swaps to a spear for lunge attacks when holding a wind charge.

**Logic:**
- Activates when holding wind charge and attack key pressed
- Swaps to spear, attacks, then swaps back
- Useful for surprise lunge attacks

**Options:**
| Option | Description | Default | Min | Max | Step |
|--------|-------------|---------|-----|-----|------|
| Reach | Attack reach distance | `3.0` | 1.0 | 6.0 | 0.5 |
| Chance | Activation chance | `100%` | 0 | 100 | 5 |

**Default Keybind:** None

---

## Crystal Modules

Modules for crystal PvP combat.

### Auto Crystal

**ID:** `autocrystal`

*Work in Progress* - Automatically places and breaks end crystals.

**Logic:**
- Places crystals on bedrock/obsidian
- Automatically attacks crystals in view
- Optional auto-switch to crystals

**Options:**
| Option | Description | Default | Min | Max | Step |
|--------|-------------|---------|-----|-----|------|
| Auto Switch | Automatically switch to crystals | `true` | - | - | - |
| Disable After | Disable module after action | `true` | - | - | - |
| Reach | Crystal interaction reach | `5.0` | 1.0 | 6.0 | 0.5 |

**Default Keybind:** None

---

### Auto Replace Crystal

**ID:** `autocrystalreplace`

*Work in Progress* - Automatically places a new crystal after breaking one.

**Logic:**
- Triggers when attacking an end crystal
- Immediately places a new crystal
- Only works when holding crystals (by default)

**Default Keybind:** None

---

### Auto Obsidian

**ID:** `autoobsidian`

*Work in Progress* - Automatically places obsidian for crystal placement.

**Logic:**
- Places obsidian when looking at valid surface
- Optional auto-switch to obsidian

**Default Keybind:** None

---

### Auto Totem

**ID:** `autototem`

*Work in Progress* - Automatically moves totems to offhand.

**Logic:**
- Monitors inventory for totems
- Works only in inventory screen (configurable)

**Options:**
| Option | Description | Default | Min | Max | Step |
|--------|-------------|---------|-----|-----|------|
| Only In Inventory | Only work when inventory open | `true` | - | - | - |
| Only When Hovering | Only work when hovering item | `false` | - | - | - |

**Default Keybind:** None

---

### Crystal Optimizer

**ID:** `crystaloptimizer`

*Work in Progress* - Optimizes crystal-related game mechanics.

**Logic:**
- Sets a global flag when enabled for other systems to use

**Default Keybind:** None

---

## Movement Modules

Modules that modify player movement.

### Auto Jump Reset

**ID:** `autojumpreset`

Automatically jumps when getting hit to reduce knockback.

**Logic:**
- Detects when being hit by a player
- Jumps at the right moment to reduce knockback
- Configurable activation chance
- Won't trigger while blocking, using items, or in water

**Options:**
| Option | Description | Default | Min | Max | Step |
|--------|-------------|---------|-----|-----|------|
| Chance | Activation chance | `50%` | 0 | 100 | 5 |

**Default Keybind:** None

---

### No Jump Delay

**ID:** `nojumpdelay`

Removes the delay between jumps, allowing for bunny-hopping.

**Logic:**
- Sets jump cooldown to 0 every tick
- Allows instant consecutive jumps

**Default Keybind:** None

---

### Sprint

**ID:** `sprint`

Automatically sprints when moving forward.

**Logic:**
- Keeps sprint key pressed when player has forward movement
- Ensures you're always sprinting while moving

**Default Keybind:** None

---

## Render Modules

Modules that modify game rendering.

### Freecam

**ID:** `freecam`

Allows camera to move freely, detached from player body.

**Logic:**
- Disables normal player movement controls
- Camera moves independently based on WASD/Space/Shift
- Sprint doubles movement speed
- Optional line rendering to show player position

**Options:**
| Option | Description | Default | Min | Max | Step |
|--------|-------------|---------|-----|-----|------|
| Speed | Camera movement speed | `1.0` | 0.1 | 10.0 | 0.1 |
| Render Line | Draw line to player position | `true` | - | - | - |

**Default Keybind:** None

---

## Client Modules

Modules for client functionality and UI.

### ClickGUI

**ID:** `clickgui`

Opens the module configuration GUI.

**Logic:**
- Toggles the ClickGUI on/off
- Unlocks mouse cursor when open
- Provides access to all module settings

**Options:**
| Option | Description | Default |
|--------|-------------|---------|
| Accent | GUI accent color | Blue |
| Header BG | Header background color | Dark |
| Module BG | Module background color | Dark |
| Enabled | Enabled module text color | Green |
| Disabled | Disabled module text color | Gray |
| Border | Border color | Gray |
| Rounding | Corner rounding (0-1) | `0.0` |

**Default Keybind:** `Right Shift`

---

### ArrayList

**ID:** `arraylist`

Displays a list of currently enabled modules on screen.

**Logic:**
- Shows all enabled modules
- Sortable by length or alphabetically

**Options:**
| Option | Description | Default |
|--------|-------------|---------|
| Module Color | Text color for module names | `#7287fd` (Blue) |
| Sort Mode | Sorting method | `By Length` |

**Sort Modes:**
- **By Length** - Longest names first
- **Alphabetical** - A-Z ordering

**Default Keybind:** None

---

### Notifications

**ID:** `notifications`

*Placeholder* - Intended for displaying chat notifications (by adding a message to the chat buffer directly without adding it to your logs) when you toggle modules.

**Default Keybind:** None

---

### HUD

**ID:** `hud`

Displays a heads-up display with client information.

**Logic:**
- Shows customizable logo with gradient text
- Displays FPS, Ping, and TPS statistics
- Real-time TPS calculation

**Options:**
| Option | Description | Default |
|--------|-------------|---------|
| Gradient Start | Logo gradient start color | `#9A64C1` |
| Gradient End | Logo gradient end color | `#30004E` |
| Logo Text | Custom logo text | `Judgment Client` |
| Show FPS | Display FPS counter | `true` |
| Show Ping | Display ping | `true` |
| Show TPS | Display TPS | `true` |

**Default Keybind:** None

---

### Destruct

**ID:** `destruct`

Emergency self-destruct - disables all modules and cleans up client state.

**Logic:**
- Calls the client destruct function
- Automatically disables itself after execution
- Use for emergency cleanup

**Default Keybind:** None

---

### Login

**ID:** `login`

Authentication module for licensed access (disabled by default).

**Logic:**
- Provides username/password input fields
- Authenticates against auth server if enabled
- Hardware ID verification
- Bypassed when auth is disabled

**Options:**
| Option | Description |
|--------|-------------|
| Username | Account username |
| Password | Account password (masked) |

**Note:** Authentication is disabled by default. See README.md for setup instructions.

**Default Keybind:** None

---

## Notes

- **WIP Modules:** Modules marked as "Work in Progress" may have incomplete functionality
- **Default Keybinds:** Most modules have no default keybind (`GLFW_KEY_UNKNOWN`), bind them in the ClickGUI at runtime
- **Reach Values:** The reach value is calculated from the absolute player position to the other entity on the player's crosshair's absolute position, **it does NOT give you additional Reach** even if you set it to 6.0 or anything above, in fact I'd recommend you set it to 6.0 to ensure you get the maximum amount of reach.
- **Chance Options:** Set below 100% for more natural/human-like behavior
