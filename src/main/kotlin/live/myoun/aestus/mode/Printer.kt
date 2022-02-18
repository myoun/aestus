package live.myoun.aestus.mode

import live.myoun.aestus.Direction
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector

class Printer(override val pos1: Vector, override val pos2: Vector, override val material: Material?,
              override val tick: Long, override val direction: Direction, override val player: Player,
              override val plugin: JavaPlugin): Mode {

    private val world: World = player.world
    var taskId: Int? = null
        private set

    override fun launch(reversed: Boolean) {
        val vectors = calculateEdge(pos1, pos2)
            .let { mapVector(it.first, it.second) }
            .also { player.sendMessage(it.size.toString()) }
            .filter { it.toLocation(world).block.type != Material.AIR }
            .filter {
                if (material == null) true
                else if (reversed) it.toLocation(world).block.type != material
                else it.toLocation(world).block.type == material
            }
            .run {
                if (direction == Direction.DOWN)
                    sortedByDescending { it.y }
                else sortedBy { it.y }
            }
            .toMutableList()

        Mode.addToHistory(vectors, player)

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            if (vectors.isEmpty()) {
                val idx = Mode.tasks[player.uniqueId]!!.indexOf(taskId)
                Mode.tasks[player.uniqueId]!!.removeAt(idx)
                Bukkit.getScheduler().cancelTask(taskId!!)
                return@scheduleSyncRepeatingTask
            }
            vectors[0].toLocation(world).block.type = Material.AIR
            vectors.removeFirst()
        }, 0, tick)

        if (Mode.tasks[player.uniqueId] == null) {
            Mode.tasks[player.uniqueId] = mutableListOf()
        }

        Mode.tasks[player.uniqueId]!!.add(taskId!!)
    }
}