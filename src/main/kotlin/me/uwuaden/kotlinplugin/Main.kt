package me.uwuaden.kotlinplugin

import io.github.monun.kommand.kommand
import me.uwuaden.kotlinplugin.Main.Companion.plugin
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

private fun getNearestPlayer(target: Player): Player? {
    var nearestPlayer: Player? = null
    var nearestDistance = Double.MAX_VALUE

    val list = target.world.players
    list.removeIf { ZombieManager.isZombie(it) }

    for (player in list) {
        if (player != target) {
            val distance = player.location.distance(target.location)
            if (distance < nearestDistance) {
                nearestPlayer = player
                nearestDistance = distance
            }
        }
    }

    return nearestPlayer
}

class Main: JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
            private set
        var scheduler = Bukkit.getScheduler()

        val zombies = mutableSetOf<UUID>()
    }
    override fun onEnable() {
        logger.info("Plugin Enabled")
        plugin = this

        Bukkit.getPluginManager().registerEvents(Events(), this)

        ZombieManager.sch()

        kommand {
            register("theinf") {
                requires { isOp }
                executes {
                    player.sendMessage("${ChatColor.GREEN}By uwuaden.")
                }
                then("resetall") {
                    executes {
                        ZombieManager.resetZombie()
                        player.sendMessage("${ChatColor.GREEN}좀비 초기화됨.")
                    }
                }
            }
            register("track") {
                requires { isPlayer }
                executes {
                    if(ZombieManager.isZombie(player)) {
                        if(getNearestPlayer(player) != null) {
                            val np = getNearestPlayer(player)!!
                            player.sendMessage("${ChatColor.GREEN}가장 가까이 있는 플레이어:")
                            player.sendMessage("${ChatColor.GREEN}${np.name}, X: ${np.location.x.toInt()}, Y: ${np.location.x.toInt()}, Z: ${np.location.x.toInt()} ${ChatColor.GRAY}(${np.location.distance(player.location)})")
                            player.sendMessage(" ")

                            val list = plugin.server.onlinePlayers
                            list.removeIf { ZombieManager.isZombie(it) }

                            player.sendMessage("${ChatColor.GREEN}남은 플레이어 수: ${list.size}")
                        }
                    }
                }
            }
        }
    }
    override fun onDisable() {
        logger.info("Plugin Disabled")
    }
}
