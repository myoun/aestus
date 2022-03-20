package live.myoun.aestus.mode

import live.myoun.aestus.Direction
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import kotlin.math.abs

class Cloner(pos1: Vector, pos2: Vector, val pivot2: Vector,
             val tick: Int, val direction: Direction, val player: Player,
             val plugin: JavaPlugin) {

    var taskId: Int? = null
        private set

    val pos1: Vector
    val pos2: Vector

    init {
        if (pos1.y > pos2.y) {
            val updown = pos1.y to pos2.y
            pos1.y = updown.second
            pos2.y = updown.first
        }
        this.pos1 = pos1
        this.pos2 = pos2
    }

    fun launch() {
        val world = player.world
        var min: Vector
        val vectors = mapCloneVector(pos1, pos2)
            .also { player.sendMessage(it.size.toString()) }
            .filter { it.toLocation(world).block.type != Material.AIR }
            .run {
                if (direction == Direction.DOWN)
                    sortedByDescending { it.y }
                else sortedBy { it.y }
            }
            .toMutableList()


        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            if (vectors.isEmpty()) {
                val idx = Mode.tasks[player.uniqueId]!!.indexOf(taskId)
                Mode.tasks[player.uniqueId]!!.removeAt(idx)
                Bukkit.getScheduler().cancelTask(taskId!!)
                return@scheduleSyncRepeatingTask
            }
//            player.sendMessage("${vectors.first().x} ${vectors.first().y} ${vectors.first().z}")
            val vector = vectors.first()

            vectors.removeFirst()

            val origin = Vector().copy(pos1).add(vector)
            val dest = Vector().copy(pivot2).add(vector)
//            player.sendMessage("===============================")
//            player.sendMessage("${vector.x}/${vector.y}/${vector.z}")
//            player.sendMessage("${pivot2.x}/${pivot2.y}/${pivot2.z}")
//            player.sendMessage("${origin.toLocation(player.world).block.type} to ${dest.toLocation(player.world).block.type}")
//            player.sendMessage("clone ${origin.x} ${origin.y} ${origin.z} ${origin.x} ${origin.y} ${origin.z} ${dest.x} ${dest.y} ${dest.z}")
            plugin.server.dispatchCommand(player, "clone ${origin.x} ${origin.y} ${origin.z} ${origin.x} ${origin.y} ${origin.z} ${dest.x} ${dest.y} ${dest.z}")

        },0, tick.toLong())

        if (Mode.tasks[player.uniqueId] == null) {
            Mode.tasks[player.uniqueId] = mutableListOf()
        }

        Mode.tasks[player.uniqueId]!!.add(taskId!!)
    }
}


private fun mapCloneVector(pos1: Vector, pos2: Vector) : MutableList<Vector> {
    val ys = (0..abs(pos1.y.toInt() - pos2.y.toInt())).toMutableList()
    val xmove = (pos1.x.toInt()-pos2.x.toInt()) / abs(pos1.x.toInt()-pos2.x.toInt())
    val zmove = (pos1.z.toInt()-pos2.z.toInt()) / abs(pos1.z.toInt()-pos2.z.toInt())
    val vectors = mutableListOf<Vector>()
    for (y in ys) {
        for (z in 0..abs(pos1.z.toInt()-pos2.z.toInt())) {
            for (x in 0..abs(pos1.x.toInt()-pos2.x.toInt())) {
                if (vectors.isEmpty()) {
                    vectors.add(Vector())
                } else {
                    val prev = vectors.last().clone()
//                    println("${prev.x}/${prev.y}/${prev.z} , ${pos2.clone().subtract(pos1).x}")
                    if (prev.y  != y.toDouble()) {
                        vectors.add(Vector(0.0, y.toDouble(), 0.0))
                    } else if (prev.x == pos2.clone().subtract(pos1).x) {
                        vectors.add(Vector(0.0, y.toDouble(), prev.z-zmove))
                    } else {
                        vectors.add(Vector(prev.x-xmove, y.toDouble(), prev.z))
                    }
                }
            }
        }
    }
    println("$xmove/$zmove")
    return vectors
}
