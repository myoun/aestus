package live.myoun.aestus.mode

import live.myoun.aestus.Direction
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector

class DoublePrinter(override val pos1: Vector, override val pos2: Vector, override val material: Material?,
                    override val tick: Long, override val direction: Direction, override val player: Player,
                    override val plugin: JavaPlugin) : Mode {

    private val world: World = player.world
    var taskId: Int? = null
        private set

    override fun launch(reversed: Boolean) {
        val udVec = calculateEdge(pos1, pos2)
            .let { mapVector(it.first, it.second) }
            .sortedByDescending { it.y }.toMutableList()

        val duVec = udVec.reversed().toMutableList()

        Mode.addToHistory(udVec, player)

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            if (udVec[0] == duVec[0] || udVec[1] == duVec[0]) {
                Bukkit.getScheduler().cancelTask(taskId!!)
                val idx = Mode.tasks[player.uniqueId]!!.indexOf(taskId)
                Mode.tasks[player.uniqueId]!!.removeAt(idx)
            }
            val udb = udVec[0].toLocation(world).block
            val dub = duVec[0].toLocation(world).block

            fun removeUdb() {
                udb.type = Material.AIR
                udVec.removeFirst()
            }
            fun removeDub() {
                dub.type = Material.AIR
                duVec.removeFirst()
            }

            if (material == null) {
                removeUdb()
                removeDub()
            } else if (reversed) {
                if (udb.type != material) removeUdb()
                if (dub.type != material) removeDub()
            } else {
                if (udb.type == material) removeUdb()
                if (dub.type == material) removeDub()
            }

        }, 0, tick)

        if (Mode.tasks[player.uniqueId] == null) {
            Mode.tasks[player.uniqueId] = mutableListOf()
        }

        Mode.tasks[player.uniqueId]!!.add(taskId!!)
    }
}