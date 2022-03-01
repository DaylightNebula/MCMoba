package daylightnebula.mcmobaplugin

import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import org.bukkit.event.weather.WeatherChangeEvent

class GameListener: Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // when player joins
        // set game mode to adventure
        val player = event.player
        player.gameMode = GameMode.ADVENTURE

        // create game player
        Main.gamePlayers.add(GamePlayer(player))

        // should game start?
        Main.plugin.start()
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        // handle use item based on match state
        when (Main.matchState) {
            MatchState.CLASS_SELECT -> Main.classSelectManager.useItem(event.player, event.player.inventory.heldItemSlot)
        }
    }

    @EventHandler
    fun onPlayerItemHeld(event: PlayerItemHeldEvent) {
        // handle item change based on match state
        when (Main.matchState) {
            MatchState.CLASS_SELECT -> Main.classSelectManager.changeHotbarSlot(event.player, event.newSlot)
        }
    }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) { event.isCancelled = true }
    @EventHandler
    fun onWeatherChange(event: WeatherChangeEvent) { event.isCancelled = true }
}