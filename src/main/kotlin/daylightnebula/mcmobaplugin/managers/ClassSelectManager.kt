package daylightnebula.mcmobaplugin.managers

import daylightnebula.mcmobaplugin.Main
import daylightnebula.mcmobaplugin.MatchState
import daylightnebula.mcmobaplugin.classes.GameClass
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.util.ChatPaginator
import org.bukkit.util.Vector
import kotlin.math.abs

class ClassSelectManager: Listener {

    val offset = abs((GameClass.classes.size - 9) / 2)
    var ticksLeft = 0

    lateinit var timerTask: BukkitTask
    fun start() {
        // for each player
        Main.gamePlayers.forEach { player ->
            // set player items
            player.player.inventory.clear()
            GameClass.classes.forEachIndexed { index, gameClass ->
                val item = gameClass.displayStack
                val meta = item.itemMeta
                meta.setDisplayName(gameClass.name)
                item.itemMeta = meta
                player.player.inventory.setItem(index + offset, gameClass.displayStack)
            }
            player.player.inventory.heldItemSlot = 4
            drawSidebar(player.player, GameClass.classes[(GameClass.classes.size / 2) + 1])

            // hide all players
            Bukkit.getOnlinePlayers().forEach {
                player.player.hidePlayer(Main.plugin, it)
            }

            // teleport players
            player.player.teleport(
                Location(Bukkit.getWorlds()[0], 103.5, 58.0, 44.5, 90f, 0f)
            )

            // make it so they cannot move since they are in a menu
            player.cancelMovement = true

            // tell them instructions
            player.player.sendMessage(
                "${ChatColor.GRAY}Use the hot bar to checkout your available classes.  Select one by clicking with the classes items."
            )
        }
        // Model location: 98.5 58.0, 44.5, -90f, 0f

        // timer
        ticksLeft = 400
        timerTask = Bukkit.getScheduler().runTaskTimer(Main.plugin, Runnable {
            // update ticks left to create final end
            ticksLeft--

            // Cancel if the timer is out or every player is ready.  If not needed, display the time left on the action bar.
            if (ticksLeft < 0 || Main.gamePlayers.filter { it.currentClass == -1 }.isEmpty()) {
                Bukkit.getScheduler().cancelTask(timerTask.taskId)
                Main.plugin.changeGameState(MatchState.ITEM_SELECT)
            } else if (ticksLeft % 20 == 0) {
                Main.gamePlayers.forEach {
                    it.player.sendActionBar("${ChatColor.WHITE}${ticksLeft / 20}")
                }
            }
        }, 0, 1)
    }

    fun drawSidebar(player: Player, gameClass: GameClass) {
        // break up description
        val desc = ChatPaginator.wordWrap("${ChatColor.GRAY}${gameClass.desc}", 20)

        // set scoreboard
        val board = Bukkit.getScoreboardManager().newScoreboard
        val obj = board.registerNewObjective("GameClass", "dummy", gameClass.name)
        obj.displaySlot = DisplaySlot.SIDEBAR
        desc.forEachIndexed { index, s ->
            val score = board.getScores(s)
            score.forEach {
                it.score = desc.size - index
            }
        }
        player.scoreboard = board
    }

    fun changeHotbarSlot(player: Player, index: Int) {
        if (index - offset < 0)
            player.inventory.heldItemSlot = offset
        else if (index - offset >= GameClass.classes.size)
            player.inventory.heldItemSlot = GameClass.classes.size + offset - 1
        else
            drawSidebar(player, GameClass.classes[index - offset])
    }

    fun useItem(player: Player, index: Int) {
        // if clicked item is within the range, set current class
        val i = index - offset
        if (i >= 0 && i  < GameClass.classes.size) {
            Main.gamePlayers.firstOrNull { it.player == player }?.currentClass = i
            player.sendMessage("${GameClass.classes[i].name} ${ChatColor.RESET}${ChatColor.GRAY}selected!")
        }
    }

    fun end() {
        // close inventories, and if a player doesn't have a class, give them a random one
        Main.gamePlayers.forEach {
            it.player.closeInventory()
            if (it.currentClass == -1) {
                it.currentClass = GameClass.classes.random().id
                it.player.sendMessage("${GameClass.classes[it.currentClass].name} ${ChatColor.RESET}${ChatColor.GRAY}selected!")
            }
        }
    }
}