package daylightnebula.mcmobaplugin.managers

import daylightnebula.mcmobaplugin.Main
import daylightnebula.mcmobaplugin.Team
import org.bukkit.Bukkit
import org.bukkit.Location

class RoundManager {
    fun start() {
        // for each player
        Main.gamePlayers.forEach {
            // teleport based on team
            if (it.team == Team.BLUE)
                it.player.teleport(Location(Bukkit.getWorlds()[0], 37.0, 66.0, 88.0, 180f, 0f))
            else
                it.player.teleport(Location(Bukkit.getWorlds()[0], 52.0, 65.5, -4.0, 0f, 0f))

            // allow for movement
            it.cancelMovement = false

        }
    }

    fun end() {

    }
}