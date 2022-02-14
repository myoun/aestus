@file:JvmName("Aestus")

package live.myoun.aestus

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.util.*

class AestusPlugin : JavaPlugin() {

    override fun onEnable() {

        server.pluginManager.registerEvents(AestusListener(), this)
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
    }
}

internal val posMap : HashMap<UUID, Pos> = hashMapOf()


data class Pos(var first: Vector?, var second: Vector?)