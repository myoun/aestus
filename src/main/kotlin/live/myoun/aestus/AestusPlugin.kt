@file:JvmName("Aestus")

package live.myoun.aestus

import live.myoun.aestus.mode.Direction
import live.myoun.aestus.mode.DoublePrinter
import live.myoun.aestus.mode.Printer
import live.myoun.aestus.mode.Random
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
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

val posMap : HashMap<UUID, Pos> = hashMapOf()

class AestusListener : Listener {

    @EventHandler
    fun wand(event: PlayerInteractEvent) {
        if (event.item == null) return
        if (event.item.type == Material.STICK) {
            if (!event.hasBlock()) return
            event.isCancelled = true
            val location = event.clickedBlock.location
            when (event.action) {
                Action.LEFT_CLICK_BLOCK -> { // Pos 1
                    if (posMap.containsKey(event.player.uniqueId)) {
                        val pos = posMap[event.player.uniqueId]!!
                        pos.first = location.toVector()
                        posMap[event.player.uniqueId] = pos
                    } else {
                        posMap[event.player.uniqueId] = Pos(event.clickedBlock.location.toVector(), null)
                    }
                    event.player.sendMessage("§dPOS1이 ${location.x},${location.y},${location.z}로 지정되었습니다.")
                }
                Action.RIGHT_CLICK_BLOCK -> { // Pos 2
                    if (posMap.containsKey(event.player.uniqueId)) {
                        val pos = posMap[event.player.uniqueId]!!
                        pos.second = location.toVector()
                        posMap[event.player.uniqueId] = pos
                    } else {
                        posMap[event.player.uniqueId] = Pos(null, location.toVector())
                    }
                    event.player.sendMessage("§dPOS2가 ${location.x},${location.y},${location.z}로 지정되었습니다.")
                }
                else -> return
            }
        }
    }
}

class AestusCommand(val plugin: JavaPlugin) : TabExecutor {


    override fun onTabComplete(
        sender: CommandSender?,
        command: Command?,
        alias: String?,
        args: Array<out String>
    ): MutableList<String>? {
        return when (args.size) {
            1 -> {
                return if (args[0].isBlank()) {
                    Material.values().filter{ it.isBlock }.map { "minecraft:"+it.toString().lowercase() }.toMutableList()
                } else {
                    Material.values()
                        .filter { it.isBlock && it.toString().startsWith(args[0].removePrefix("minecraft:").uppercase()) }
                        .map { "minecraft:"+it.toString().lowercase() }
                        .toMutableList()
                }
            }
            2 -> mutableListOf("printer", "dprinter", "random")
            else -> null
        }
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
            val player = Bukkit.getPlayer(sender.name)!!
            return when (command.name) {
            "break" -> {
                val pos = posMap[player.uniqueId] ?: run {
                    player.sendMessage("§dPos가 지정되지 않았습니다.")
                    return false
                }
                val pos1 = pos.first ?: run {
                    player.sendMessage("§dPos1이 지정되지 않았습니다.")
                    return false
                }
                val pos2 = pos.second ?: run {
                    player.sendMessage("§dPos2가 지정되지 않았습니다.")
                    return false
                }
                val material: Material? = if (args[0] == "a") {
                    null
                } else {
                    Material.getMaterial(args[0].removePrefix("minecraft:").uppercase())
                }
                val tick = args[2].toLongOrNull() ?: kotlin.run {
                    sender.sendMessage("Invalid Tick")
                    return false
                }
                if (material != null) {
                    sender.sendMessage(material.toString())
                    if (!material.isBlock) {
                        sender.sendMessage("Non-Block Material")
                        return false
                    }
                }
                val direction = when (args[3]) {
                    "up" -> Direction.UP
                    "down" -> Direction.DOWN
                    else -> {
                        sender.sendMessage("Invalid Direction")
                        return false
                    }
                }

                when (args[1]) {
                    "printer" -> Printer(pos1, pos2, sender, direction, plugin, material = material).launch(tick)
                    "dprinter" -> DoublePrinter(pos1, pos2, sender, direction, plugin, material).launch(tick)
                    "random" -> Random(pos1, pos2, sender, direction, plugin, material).launch(tick)
                }
                true
            }
            "cancel" -> {
                Bukkit.getScheduler().cancelTasks(plugin)
                true
            }
            else -> false
        }
    }
}


data class Pos(var first: Vector?, var second: Vector?)