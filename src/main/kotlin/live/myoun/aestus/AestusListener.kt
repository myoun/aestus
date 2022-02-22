package live.myoun.aestus

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class AestusListener(val plugin: AestusPlugin) : Listener {

    val wand: Material
        get() = Material.getMaterial(plugin.config.getString("wand"))


    @EventHandler
    fun wand(event: PlayerInteractEvent) {
        if (event.item == null) return
        if (event.item.type == wand) {
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