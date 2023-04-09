package me.uwuaden.kotlinplugin

import io.github.monun.kommand.kommand
import org.bukkit.*
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

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
        }
    }
    override fun onDisable() {
        logger.info("Plugin Disabled")
    }
}
