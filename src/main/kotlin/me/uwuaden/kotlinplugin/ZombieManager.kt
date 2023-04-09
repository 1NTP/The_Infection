package me.uwuaden.kotlinplugin

import me.uwuaden.kotlinplugin.Main.Companion.plugin
import me.uwuaden.kotlinplugin.Main.Companion.scheduler
import me.uwuaden.kotlinplugin.Main.Companion.zombies
import org.bukkit.Particle
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player

private fun getRand(): Double {
    return Math.random() * 2 - 1
}

object ZombieManager {
    fun setZombie(player: Player, boolean: Boolean) {
        zombies.remove(player.uniqueId)
        if(boolean) {
            zombies.add(player.uniqueId)
        }
    }

    fun resetZombie() {
        zombies.clear()
    }

    fun isZombie(player: Player): Boolean {
        return zombies.contains(player.uniqueId)
    }

    fun sch() {
        scheduler.scheduleSyncRepeatingTask(plugin, {
            plugin.server.onlinePlayers.forEach { player ->
                for (i in 0 until 10) {
                    val loc = player.location.clone().add(getRand(), getRand() + 1.0, getRand())
                    player.world.spawnParticle(Particle.COMPOSTER, loc, 1)
                }
            }
        }, 0, 5)

        scheduler.scheduleSyncRepeatingTask(plugin, {
            plugin.server.worlds.forEach { w->
                w.entities.forEach { e->
                    if(e is Arrow && e.scoreboardTags.contains("TI_CureShot")) {
                        if (!e.isOnGround) w.spawnParticle(Particle.CLOUD, e.location, 3)
                    }
                }
            }
        }, 0, 2)
    }
}