package daylightnebula.mcmobaplugin.managers

import daylightnebula.mcmobaplugin.Main
import daylightnebula.mcmobaplugin.MatchState
import daylightnebula.mcmobaplugin.classes.GameClass
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.ChatPaginator

class ClassSelectManager: Listener {

    val offset = (GameClass.classes.size - 9) / 2
    var ticksLeft = 0

    lateinit var timerTask: BukkitTask
    fun start() {
        // for each player, fill hotbar and draw sidebar
        Main.gamePlayers.forEach { player ->
            GameClass.classes.forEachIndexed { index, gameClass ->
                player.player.inventory.setItem(index + offset, gameClass.displayStack)
            }
        }

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
        player.scoreboardTags.clear()
        player.addScoreboardTag(gameClass.name)
        desc.forEach {
            player.addScoreboardTag(it)
        }
    }

    fun changeHotbarSlot(player: Player, index: Int) {
        drawSidebar(player, GameClass.classes[index - offset])
    }

    fun useItem(player: Player, index: Int) {
        // if clicked item is within the range, set current class
        val i = index - offset
        if (i >= 0 && i  < GameClass.classes.size)
            Main.gamePlayers.firstOrNull { it.player == player }?.currentClass = index
    }

    fun end() {
        // close inventories, and if a player doesn't have a class, give them a random one
        Main.gamePlayers.forEach {
            it.player.closeInventory()
            if (it.currentClass == -1)
                it.currentClass = GameClass.classes.random().id
        }
    }
}