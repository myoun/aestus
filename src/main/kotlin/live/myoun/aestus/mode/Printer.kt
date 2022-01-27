package live.myoun.aestus.mode

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.util.*

class Printer(pos1: Vector, pos2: Vector, val sender: CommandSender, override val direction: Direction,
              val plugin: JavaPlugin, override val material: Material? = null) : Mode {
    val min: Vector = Vector()
    val max: Vector = Vector()
    private var x: Double
    private var y: Double
    private var z: Double
    private var rest: Boolean = true
    private val player = Bukkit.getPlayer(sender.name)


    init {
        if (pos1.x < pos2.x) {
            min.x = pos1.x
            max.x = pos2.x
        } else {
            min.x = pos2.x
            max.x = pos1.x
        }
        if (pos1.y > pos2.y) {
            min.y = pos1.y
            max.y = pos2.y
        } else {
            min.y = pos2.y
            max.y = pos1.y
        }
        if (pos1.z < pos2.z) {
            min.z = pos1.z
            max.z = pos2.z
        } else {
            min.z = pos2.z
            max.z = pos1.z
        }
        x = min.x
        y = if (direction == Direction.DOWN) min.y else max.y
        z = min.z
    }

    override fun changeLocation() {
        if (x < max.x) {
            x++
        } else {
            x = min.x
            if (z < max.z) {
                z++
            } else {
                z = min.z
                if (direction == Direction.UP) {
                    if (y < min.y) {
                        y++
                    } else {
                        Bukkit.getScheduler().cancelAllTasks()
                        rest = false
                        sender.sendMessage("§d파괴가 완료되었습니다.")
                        return
                    }
                } else {
                    if (y > max.y) {
                        y--
                    } else {
                        Bukkit.getScheduler().cancelAllTasks()
                        rest = false
                        sender.sendMessage("§d파괴가 완료되었습니다.")
                        return
                    }
                }
            }
        }
    }

    override fun breakBlock() {
        rest = true
        while (rest) {
            val location = Location(player.world, x, y, z)
            if (location.block.type == Material.AIR) {
                changeLocation()
                continue
            }
            if (material != null) {
                if (material.isBlock) {
                    if (location.block.type == material) {
                        location.block.type = Material.AIR
                        changeLocation()
                        rest = false
                        break
                    }

                } else {
                    sender.sendMessage("Non-Block Material")
                }
            } else {
                location.block.type = Material.AIR
            }
            changeLocation()
            rest = false
            break

        }
    }

    override fun launch(tick: Long) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            breakBlock()
        }, 0, tick)
    }
}