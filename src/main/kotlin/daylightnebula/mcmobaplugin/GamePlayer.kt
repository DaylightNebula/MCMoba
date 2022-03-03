package daylightnebula.mcmobaplugin

import org.bukkit.entity.Player

class GamePlayer(val player: Player, var currentClass: Int = -1, var cancelMovement: Boolean = false) {
}