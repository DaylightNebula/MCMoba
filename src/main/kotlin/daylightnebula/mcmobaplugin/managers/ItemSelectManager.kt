package daylightnebula.mcmobaplugin.managers

import daylightnebula.mcmobaplugin.GamePlayer
import daylightnebula.mcmobaplugin.Main
import daylightnebula.mcmobaplugin.MatchState
import daylightnebula.mcmobaplugin.classes.GameClass
import daylightnebula.mcmobaplugin.items.GameItem
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.util.ChatPaginator
import kotlin.math.abs

class ItemSelectManager {

    // general stuffs
    val playerData = hashMapOf<GamePlayer, Triple<Int, Int, Array<Int>>>() // format, Key: player, Value: <Offset, Type, Item List>

    // timer stuffs
    lateinit var timerTask: BukkitTask
    var ticksLeft = 0

    fun start() {
        // for each player
        Main.gamePlayers.forEach {
            // give first hotbar
            giveHotbar(it, 0)
        }

        // timer
        ticksLeft = 800
        timerTask = Bukkit.getScheduler().runTaskTimer(Main.plugin, Runnable {
            // update ticks left to create final end
            ticksLeft--

            // Cancel if the timer is out or every player is ready.  If not needed, display the time left on the action bar.
            if (ticksLeft < 0 || Main.gamePlayers.filter { it.currentArmor == -1 }.isEmpty()) {
                Bukkit.getScheduler().cancelTask(timerTask.taskId)
                Main.plugin.changeGameState(MatchState.ROUND)
            } else if (ticksLeft % 20 == 0) {
                Main.gamePlayers.forEach {
                    it.player.sendActionBar("${ChatColor.WHITE}${ticksLeft / 20}")
                }
            }
        }, 0, 1)
    }

    fun giveHotbar(player: GamePlayer, type: Int) {
        // get values
        val gameClass = GameClass.classes[player.currentClass] ?: return
        val itemList = if (type == 0) gameClass.primaryItemIDs else if (type == 1) gameClass.secondItemIDs else if (type == 2) gameClass.armorItemIDs else return
        val offset = abs(itemList.size - 9) / 2
        playerData[player] = Triple(offset, type, itemList)

        // send message to the player
        val str = if (type == 0) "primary weapon" else if (type == 1) "secondary item" else "armor"
        player.player.sendMessage("${ChatColor.GRAY}Select your $str weapon!  Use the hotbar to check out your options, and click with your choice in hand to select.")

        // set up hotbar
        player.player.inventory.clear()
        itemList.forEachIndexed { index, itemID ->
            val gameItem = GameItem.allItems.firstOrNull { it.id == itemID } ?: return@forEachIndexed
            val item = gameItem.displayStack
            val meta = item.itemMeta
            meta.setDisplayName(gameItem.name)
            item.itemMeta = meta
            player.player.inventory.setItem(index + offset, item)
        }
        player.player.inventory.heldItemSlot = 4
        drawSidebar(player.player, GameItem.allItems.firstOrNull { itemList[4 - offset] == it.id } ?: return)
    }

    fun drawSidebar(player: Player, gameItem: GameItem) {
        // break up description
        val desc = ChatPaginator.wordWrap("${ChatColor.GRAY}${gameItem.desc}", 20)

        // set scoreboard
        val board = Bukkit.getScoreboardManager().newScoreboard
        val obj = board.registerNewObjective("GameClass", "dummy", gameItem.name)
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
        // get data
        val gp = Main.gamePlayers.firstOrNull { it.player == player } ?: return
        val offset = playerData[gp]?.first ?: return
        val itemList = playerData[gp]?.third ?: return

        // if out of bounds, return to bounds
        if (index - offset < 0)
            player.inventory.heldItemSlot = offset
        else if (index - offset >= itemList.size)
            player.inventory.heldItemSlot = itemList.size + offset - 1
        // else, draw the new sidebar
        else
            drawSidebar(player, GameItem.allItems.firstOrNull { itemList[index - offset] == it.id } ?: return)
    }

    fun useItem(player: Player, index: Int) {
        // get data
        val gp = Main.gamePlayers.firstOrNull { it.player == player } ?: return
        val offset = playerData[gp]?.first ?: return
        val type = playerData[gp]?.second ?: return
        val itemList = playerData[gp]?.third ?: return

        // if clicked item is within the range, set current item
        val i = index - offset
        if (i >= 0 && i  < itemList.size) {
            // save item
            if (type == 0) gp.currentPrime = i
            else if (type == 1) gp.currentSecond = i
            else if (type == 2) gp.currentArmor = i

            // print results
            player.sendMessage("${GameItem.allItems.firstOrNull { itemList[i] == it.id }?.name ?: return} ${ChatColor.RESET}${ChatColor.GRAY}selected!")

            // proceed to next part
            if (type < 2) {
                giveHotbar(gp, type + 1)
            } else {
                player.sendMessage("${ChatColor.GRAY}Waiting for the all players to finish!")
                player.inventory.clear()
            }
        }
    }

    fun end() {
        // make sure everyone has selected all their items
        Main.gamePlayers.forEach { gp ->
            // check items
            val gc = GameClass.classes[gp.currentClass]
            if (gp.currentPrime == -1) {
                val itemID = gc.primaryItemIDs.random()
                val item = GameItem.allItems.first { itemID == it.id }
                gp.currentPrime = itemID
                gp.player.sendMessage("${item.name} ${ChatColor.RESET}${ChatColor.GRAY}selected")
            }
            if (gp.currentSecond == -1) {
                val itemID = gc.secondItemIDs.random()
                val item = GameItem.allItems.first { itemID == it.id }
                gp.currentSecond = itemID
                gp.player.sendMessage("${item.name} ${ChatColor.RESET}${ChatColor.GRAY}selected")
            }
            if (gp.currentArmor == -1) {
                val itemID = gc.armorItemIDs.random()
                val item = GameItem.allItems.first { itemID == it.id }
                gp.currentArmor = itemID
                gp.player.sendMessage("${item.name} ${ChatColor.RESET}${ChatColor.GRAY}selected")
            }

            // un hide all players
            Bukkit.getOnlinePlayers().forEach {
                gp.player.showPlayer(Main.plugin, it)
            }
        }

    }
}