package live.myoun.aestus.mode

import live.myoun.aestus.taskQueue
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.util.*

class Random(pos1: Vector, pos2: Vector, val sender: CommandSender, override val direction: Direction,
             val plugin: JavaPlugin, override val material: Material? = null) : Mode {

    private val player = Bukkit.getPlayer(sender.name)

    private val locations : MutableList<Vector> = mutableListOf()
    private val min: Vector = Vector()
    private val max: Vector = Vector()

    init {
        if (pos1.x < pos2.x) {
            min.x = pos1.x
            max.x = pos2.x
        } else {
            min.x = pos2.x
            max.x = pos1.x
        }
        if (pos1.y < pos2.y) {
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
    }

    override fun breakBlock() {
        val location = locations.random().toLocation(player.world)
        location.block.type = Material.AIR
        if (material != null) {
            if (material.isBlock) {
                if (location.block.type == material) {
                    location.block.type = Material.AIR
                }
            } else {
                sender.sendMessage("Non-Block Material")
            }
        } else {
            location.block.type = Material.AIR
        }
        locations.remove(location.toVector())
    }

    @Suppress("Unused")
    override fun changeLocation() {}

    override fun launch(tick: Long) {
        // Mapping Blocks
        val temploc = mutableListOf<Vector>()
        (min.y.toInt()..max.y.toInt()).forEach { y ->
            (min.z.toInt()..max.z.toInt()).forEach { z ->
                (min.x.toInt()..max.x.toInt()).forEach { x ->
                    temploc.add(Vector(x, y, z))
                }
            }
        }
        locations.addAll(temploc.filter {
            val type = it.toLocation(player.world).block.type
            if (material == null) type != Material.AIR
            else type != Material.AIR && type == material
        }.shuffled().let {
            if (direction == Direction.DOWN) it.sortedByDescending { vec ->
                vec.y
            } else it.sortedBy { vec -> vec.y }
        })
        taskQueue[sender].also {
            val taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
                if (locations.isEmpty()) {
                    Bukkit.getScheduler().cancelTask(it!!.poll())
                }
                breakBlock()
            }, 0, tick)
            if (it == null) {
                taskQueue[sender] = PriorityQueue<Int>(5).also { queue ->
                    queue.add(taskId)
                }
            } else {
                it.offer(taskId)
            }
        }
    }
}
