@file:JvmName("Aestus")

package live.myoun.aestus

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.io.File
import java.util.*

class AestusPlugin : JavaPlugin() {

    val configFile = File(dataFolder, "config.yml")

    override fun onEnable() {
        if (!configFile.exists()) {
            config.set("wand", "BLAZE_ROD")
            config.save(configFile)
        } else {
            config.load(configFile)
        }

        server.pluginManager.registerEvents(AestusListener(this), this)
        getCommand("break").apply {
            val executor = AestusCommand(this@AestusPlugin)
            setExecutor(executor)
            tabCompleter = executor
        }
        getCommand("cancel").apply {
            val executor = AestusCommand(this@AestusPlugin)
            setExecutor(executor)
            tabCompleter = executor
        }
        getCommand("st").apply {
            val executor = AestusCommand(this@AestusPlugin)
            setExecutor(executor)
            tabCompleter = executor
        }
        getCommand("undo").apply {
            val executor = AestusCommand(this@AestusPlugin)
            setExecutor(executor)
            tabCompleter = executor
        }
    }
}

internal val posMap : HashMap<UUID, Pos> = hashMapOf()


data class Pos(var first: Vector?, var second: Vector?)