package live.myoun.aestus.mode

import live.myoun.aestus.Direction
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector

class Cloner(val pos1: Vector, val pos2: Vector, val pivot2: Vector,
             val tick: Int, val direction: Direction, val player: Player,
             val plugin: JavaPlugin) {


    fun launch() {
        val world = player.world
        var min: Vector
        val vectors = calculateEdge(pos1, pos2)
            .also { min = it.first }
            .let { mapVector(it.first, it.second) }
            .also { player.sendMessage(it.size.toString()) }
            .filter { it.toLocation(world).block.type != Material.AIR }
            .run {
                if (direction == Direction.DOWN)
                    sortedByDescending { it.y }
                else sortedBy { it.y }
            }
            .map {
                Vector(it.x-min.x, it.y-min.y, it.z-min.z)
            }
            .toMutableList()


//        TODO("scheduler")

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            if (vectors.isEmpty()) {
                TODO("exit")
            }

            val originloc = vectors.first().add(pos1)
            val pivloc = originloc.add(pivot2)

            plugin.server.dispatchCommand(player.server.consoleSender, "execute in minecraft:${world.name} run clone ${originloc.x} ${originloc.y} ${originloc.z} ${originloc.x} ${originloc.y} ${originloc.z} ${pivloc.x} ${pivloc.y} ${pivloc.z}")

        },0, tick.toLong())
    }
}
