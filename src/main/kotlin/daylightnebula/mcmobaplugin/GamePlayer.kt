package daylightnebula.mcmobaplugin

import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Piglin
import org.bukkit.entity.Player

class GamePlayer(
    val player: Player,
    var cancelMovement: Boolean = false,
    var currentClass: Int = -1,
    var currentPrime: Int = -1,
    var currentSecond: Int = -1,
    var currentArmor: Int = -1
) {
    lateinit var doll: ArmorStand
}