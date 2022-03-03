package daylightnebula.mcmobaplugin

import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.inventory.InventoryOpenEvent
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

        // clear inventories
        event.player.inventory.clear()

        // set health and hunger
        player.health = 20.0
        player.foodLevel = 20
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
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        // there are no inventories on this server!
        if (event.whoClicked !is Player) return
        event.whoClicked.closeInventory()
        event.whoClicked.sendMessage("${ChatColor.RED}There are no inventories on this server!")
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        // cancel if movement is cancelled
        if (Main.gamePlayers.firstOrNull { event.player == it.player }?.cancelMovement == true)
            event.isCancelled = true
    }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) { event.isCancelled = true }
    @EventHandler
    fun onWeatherChange(event: WeatherChangeEvent) { event.isCancelled = true }
}