package live.myoun.aestus.mode

import live.myoun.aestus.Direction
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.util.*

/**
 * 모드 인터페이스
 * 모든 모드들은 이 인터페이스를 상속
 */
interface Mode {

    companion object {
        val tasks: HashMap<UUID, MutableList<Int>> = HashMap()
        val blocks: HashMap<UUID, MutableList<List<Pair<Location, Material>>>> = HashMap()

        /**
         * UNDO를 위한 기록에 추가
         */
        fun addToHistory(vectors: List<Vector>, player: Player) {
            if (blocks[player.uniqueId] == null) {
                blocks[player.uniqueId] = mutableListOf()
            }

            blocks[player.uniqueId]!!.add(
                vectors.map {
                    it.toLocation(player.world) to it.toLocation(player.world).block.type
                }
            )
        }
    }

    val pos1: Vector
    val pos2: Vector
    val direction: Direction
    val tick: Long
    val material: Material?
    val plugin: JavaPlugin
    val player: Player

    fun launch(reversed: Boolean)

}
/**
 * 3차원 공간의 꼭짓점을 계산
 * @return Pair(Vector min, Vector max)
 */
internal fun calculateEdge(pos1: Vector, pos2: Vector) : Pair<Vector, Vector> {
    val min = Vector()
    val max = Vector()
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
    return min to max
}

/**
 * 각 꼭짓점 사이의 모든 좌표 계산
 */
internal fun mapVector(min: Vector, max: Vector) : List<Vector> = mutableListOf<Vector>().apply {
    for (x in min.x.toInt()..max.x.toInt()) {
        for (z in min.z.toInt()..max.z.toInt()) {
            for (y in min.y.toInt()..max.y.toInt()) {
                add(Vector(x, y, z))
            }
        }
    }
}