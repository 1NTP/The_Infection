package me.uwuaden.kotlinplugin

import io.github.monun.kommand.kommand
import org.bukkit.*
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
            private set
    }
    override fun onEnable() {
        logger.info("Plugin Enabled")
        plugin = this

        Bukkit.getPluginManager().registerEvents(Events(), this)

        kommand {
            register("example") {
                requires { isOp }
                executes {
                }
            }
        }
    }
    override fun onDisable() {
        logger.info("Plugin Disabled")
    }
}
