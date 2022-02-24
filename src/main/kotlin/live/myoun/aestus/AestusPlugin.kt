@file:JvmName("Aestus")

package live.myoun.aestus

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.io.File
import java.util.*

class AestusPlugin : JavaPlugin() {

    val configFile = File(dataFolder, "config.yml")

    val isAlphaBuild : Boolean
        get() = version.contains("alpha")

    val isBetaBuild : Boolean
        get() = version.contains("beta")

    val version : String
        get() = description.version


    override fun onEnable() {
        if (isAlphaBuild) {
            server.onlinePlayers.filter { it.isOp }.forEach {
                it.sendMessage(
                    "§c==================================================\n"+
                    "Aestus 플러그인의 Alpha 버전을 사용하고 계십니다.\n"+
                    "사용에 지장이 가는 버그들이 다수 존재할 수 있습니다.\n"+
                    "이 점 유의하여 사용해주세요.\n"+
                    "=================================================="
                )
            }
        } else if (isBetaBuild) {
            server.onlinePlayers.filter { it.isOp }.forEach {
                it.sendMessage(
                    "§e==================================================\n"+
                    "Aestus 플러그인의 Beta 버전을 사용하고 계십니다.\n"+
                    "몇몇 버그가 있을 수도 있습니다.\n"+
                    "이 점 유의하여 사용해주세요.\n"+
                    "=================================================="
                )
            }
        }

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