package me.uwuaden.kotlinplugin

import org.bukkit.ChatColor
import org.bukkit.GameRule
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.world.WorldLoadEvent
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
        e.isCancelled = true
        val originalHP = victim.health
        e.isCancelled = false
        val currentHP = victim.health

        if (abs(originalHP - currentHP) >= 3.0) {
            victim.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20*5, 4, false, true))
            victim.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 20*5, 9, false, true))
            victim.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20*5, 0, false, true))
        }
    }

    @EventHandler
    fun onShoot(e: ProjectileLaunchEvent) {
        val shooter = e.entity.shooter ?: return
        if(e.entity !is Arrow) return
        if (shooter !is Player) return
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
    fun onWorldLoad(e: WorldLoadEvent) {
        e.world.setGameRule(GameRule.SPAWN_RADIUS, 5000)
        e.world.setGameRule(GameRule.NATURAL_REGENERATION, false)
    }

    @EventHandler
    fun onEat(e: PlayerItemConsumeEvent) {
        if (e.item.type == Material.GOLDEN_APPLE) {
            healPlayer(e.player, 4.0)
        } else if (e.item.type == Material.ENCHANTED_GOLDEN_APPLE) {
            healPlayer(e.player, 8.0)
        }
    }
}