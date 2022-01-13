package live.myoun.aestus.mode

import org.bukkit.Material

interface Mode {
    val direction: Direction
    val material: Material?
    fun breakBlock()
    fun changeLocation()
    fun launch(tick: Long)
}

enum class Direction {
    UP, DOWN
}