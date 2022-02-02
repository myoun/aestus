package live.myoun.aestus.mode

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.roundToInt


class DoublePrinter(pos1: Vector, pos2: Vector, val sender: CommandSender, override val direction: Direction,
                    val plugin: JavaPlugin, override val material: Material? = null) : Mode {

    val high = if (pos1.y >= pos2.y) pos1.y else pos2.y
    val low = if (pos1.y <= pos2.y) pos1.y else pos2.y

    val mp = Vector((pos1.x+pos2.x)/2, high, (pos1.z+pos2.z)/2).let {
        Vector(it.x.roundToInt().toDouble(), it.y, it.z.roundToInt().toDouble())
    }
    val fp = Vector(if (pos1.x <= pos2.x) pos1.x else pos2.x, mp.y, if (pos1.z <= pos2.z) pos1.z else pos2.z)
    val lp = Vector(if (pos1.x >= pos2.x) pos1.x else pos2.x, mp.y, if (pos1.z >= pos2.z) pos1.z else pos2.z)

    var fx = fp.x
    var fy = fp.y
    var fz = fp.z

    var lx = lp.x
    var ly = lp.y
    var lz = lp.z

    val player = Bukkit.getPlayer(sender.name)!!
    var rest: Boolean = true

    override fun breakBlock() {
        val fl = Location(player.world, fx, fy, fz)
        val ll = Location(player.world, lx, ly, lz)
        if (material != null) {
            if (material == fl.block.type) {
                fl.block.type = Material.AIR
            }
            if (material == ll.block.type) {
                ll.block.type = Material.AIR
            }
        } else {
            fl.block.type= Material.AIR
            ll.block.type= Material.AIR
        }

        if (lx == mp.x && lz == mp.z && ly == low) {
            Bukkit.getScheduler().cancelTasks(plugin)
            sender.sendMessage("§d파괴가 완료되었습니다.")
            rest = false
            return
        }
        changeLocation()
    }

    override fun changeLocation() {
        if (fx == mp.x && fz == mp.z) { // middle
            fz = fp.z
            fx = fp.x
            fy--
        } else {
            if (fx < lp.x) {
                fx++
            } else {
                fx = fp.x
                if (fz < lp.z) {
                    fz++
                }
            }
        }
        if (lx == mp.x && lz == mp.z) {
            lx = lp.x
            lz = lp.z
            ly--
        } else {
            if (lx  > fp.x) {
                lx--
            } else {
                lx = lp.x
                if (lz > fp.z) {
                    lz--
                }
            }
        }
    }

    override fun launch(tick: Long) {
        if (abs(fp.z-lp.z).toInt() % 2 != 0) {
            sender.sendMessage("§c더블 프린터를 사용할 수 없습니다.")
            return
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            breakBlock()
        }, 0, tick)
    }
}