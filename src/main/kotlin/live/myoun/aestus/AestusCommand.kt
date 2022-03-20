package live.myoun.aestus

import live.myoun.aestus.mode.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector

class AestusCommand(val plugin: JavaPlugin) : TabExecutor {


    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        return when (command.name) {
            "break" ->         when (args.size) {
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

                var isReversed = false

                val material: Material? = if (args[0] == "a") {
                    null
                } else if (args[0].startsWith("-")) {
                    isReversed = true
                    Material.getMaterial(args[0].removePrefix("-minecraft:").uppercase())
                } else {
                    Material.getMaterial(args[0].removePrefix("minecraft:").uppercase())
                }
                player.sendMessage("$isReversed")
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
                    "printer" -> Printer(pos1, pos2, material, tick, direction, player, plugin).launch(isReversed)
                    "dprinter" -> DoublePrinter(pos1, pos2, material, tick, direction, player, plugin).launch(isReversed)
                    "random" -> Random(pos1, pos2, material, tick, direction, player, plugin).launch(isReversed)
                }
                true
            }
            "cancel" -> {
                Bukkit.getScheduler().cancelTasks(plugin)
                true
            }
            "st" -> {
                Mode.tasks[player.uniqueId]?.also {
                    Bukkit.getScheduler().cancelTask(it[0])
                }
                true
            }
            "undo" -> {
                val blocks = (Mode.blocks[player.uniqueId] ?: kotlin.run {
                    player.sendMessage("§c기록이 없습니다.")
                    return false
                })[0]

                blocks.forEach {
                    it.first.block.type = it.second
                }

                Mode.blocks[player.uniqueId]!!.removeFirst()

                true
            }
            "cloner" -> {

                // /cloner <pivot:x> <pivot:y> <pivot:z> <tick> <direction>

                // pivot1 = pos1
                // pivot2 = input from user (command)

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

                val pivot2 = Vector().apply {
                    x = args[0].checkRelativePos(player, Axis.X)
                    y = args[1].checkRelativePos(player, Axis.Y)
                    z = args[2].checkRelativePos(player, Axis.Z)
                }

                val tick = args[3].toIntOrNull() ?: run {
                    player.sendMessage("§ctick이 지정되지 않았습니다.")
                    return false
                }

                val direction = when (args[4]) {
                    "up" -> Direction.UP
                    "down" -> Direction.DOWN
                    else -> {
                        sender.sendMessage("Invalid Direction")
                        return false
                    }
                }

                Cloner(pos1, pos2, pivot2, tick, direction, player, plugin) .launch()
                true
            }
            else -> false
        }
    }
}

private fun String.checkRelativePos(player: Entity, axis: Axis) : Double {
    return if (this == "~") {
        when (axis) {
            Axis.X -> player.location.blockX.toDouble()
            Axis.Y -> player.location.blockY.toDouble()
            Axis.Z -> player.location.blockZ.toDouble()
        }
    }
    else this.toDouble()
}

enum class Axis {
    X, Y, Z
}
