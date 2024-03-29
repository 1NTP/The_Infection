package me.uwuaden.kotlinplugin

import me.uwuaden.kotlinplugin.Main.Companion.plugin
import org.bukkit.*
import org.bukkit.block.data.type.Leaves
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.server.PluginEnableEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.math.abs


private fun healPlayer(player: Player, heal: Double) {
    var updateHealth = player.health + heal
    if (updateHealth > player.maxHealth) updateHealth = player.maxHealth
    player.health = updateHealth
}

class Events: Listener {
    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        if (e.entity !is Player) return
        val victim = e.entity as Player
        if (e.finalDamage >= 6.0) {
            victim.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20*5, 4, false, true))
            victim.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 20*5, 9, false, true))
            victim.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20*5, 0, false, true))
        }
    }

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        if (
        e.player.hasPotionEffect(PotionEffectType.SLOW)
        && e.player.hasPotionEffect(PotionEffectType.BLINDNESS)
        && e.player.hasPotionEffect(PotionEffectType.WEAKNESS)
        ) e.isCancelled = true

    }
    @EventHandler
    fun onDamage(e: EntityDamageByEntityEvent) {
        if(e.damager !is Player) return
        val player = e.damager as Player
        if (ZombieManager.isZombie(player)) {
            e.damage = e.damage*1.5
        }
    }
    @EventHandler
    fun onShoot(e: ProjectileLaunchEvent) {
        val shooter = e.entity.shooter ?: return

        if(e.entity !is Arrow) return
        if (shooter !is Player) return
        if(ZombieManager.isZombie(shooter)) return
        if (shooter.inventory.itemInOffHand.type != Material.DIAMOND) return

        shooter.inventory.itemInOffHand.amount -= 1
        e.entity.addScoreboardTag("TI_CureShot")

    }

    @EventHandler
    fun onCure(e: EntityDamageByEntityEvent) {
        if(e.entity !is Player) return
        val victim = e.entity as Player
        if(!ZombieManager.isZombie(victim)) return
        if(e.damager !is Projectile) return
        if(!e.damager.scoreboardTags.contains("TI_CureShot")) return
        ZombieManager.setZombie(victim, false)
        victim.sendMessage("${ChatColor.GREEN}치료되었습니다.")
        victim.playSound(victim, Sound.ITEM_TOTEM_USE, 1.0F, 1.0F)
        e.isCancelled = true
    }

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        ZombieManager.setZombie(e.player, true)
    }

    @EventHandler
    fun onWorldLoad(e: PluginEnableEvent) {
        plugin.server.worlds.forEach { world ->
            world.setGameRule(GameRule.SPAWN_RADIUS, 5000)
            world.setGameRule(GameRule.NATURAL_REGENERATION, false)
            world.setSpawnLocation(0, 0, 0)
            world.worldBorder.center = Location(world, 0.0, 0.0, 0.0)
            world.worldBorder.setSize(1000.0, 5)
            world.difficulty = Difficulty.HARD
        }
    }

    @EventHandler
    fun onEat(e: PlayerItemConsumeEvent) {
        if (e.item.type == Material.GOLDEN_APPLE) {
            healPlayer(e.player, 4.0)
        } else if (e.item.type == Material.ENCHANTED_GOLDEN_APPLE) {
            healPlayer(e.player, 8.0)
        }
    }


    @EventHandler
    fun onMine(e: BlockBreakEvent) {
        if(e.block.type == Material.STONE) {
            when ((1..100).random()) {
                in 1..20 -> {
                    e.block.world.dropItem(e.block.location, ItemStack(Material.IRON_INGOT))
                    e.block.world.dropItem(e.block.location, ItemStack(Material.COBBLESTONE))
                    e.isDropItems = false
                }
                in 21..30 -> {
                    e.block.world.dropItem(e.block.location, ItemStack(Material.GOLD_INGOT))
                    e.block.world.dropItem(e.block.location, ItemStack(Material.COBBLESTONE))
                    e.isDropItems = false
                }
                in 31..35 -> {
                    e.block.world.dropItem(e.block.location, ItemStack(Material.DIAMOND))
                    e.block.world.dropItem(e.block.location, ItemStack(Material.COBBLESTONE))
                    e.isDropItems = false
                }
                in 36..40 -> {
                    e.block.world.dropItem(e.block.location, ItemStack(Material.COAL))
                    e.block.world.dropItem(e.block.location, ItemStack(Material.COBBLESTONE))
                    e.isDropItems = false
                }
            }
        } else if(e.block.blockData is Leaves) {
            when ((1..100).random()) {
                in 1..30 -> {
                    e.block.world.dropItem(e.block.location, ItemStack(Material.APPLE))
                    e.isDropItems = false
                }
            }
        }
    }
}