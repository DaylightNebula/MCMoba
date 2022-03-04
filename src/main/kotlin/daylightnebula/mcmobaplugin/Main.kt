package daylightnebula.mcmobaplugin

import daylightnebula.mcmobaplugin.managers.ClassSelectManager
import daylightnebula.mcmobaplugin.managers.ItemSelectManager
import daylightnebula.mcmobaplugin.managers.RoundManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.atomic.AtomicInteger

class Main: JavaPlugin() {

    companion object {
        lateinit var plugin: Main

        // constants
        const val MAX_ROUNDS = 7

        // managers
        val classSelectManager = ClassSelectManager()
        val itemSelectManager = ItemSelectManager()
        val roundManager = RoundManager()

        // management vars
        val gamePlayers = mutableListOf<GamePlayer>()
        val allowedInventories = mutableListOf<String>()
        var starting = false
        var started = false

        // global game vars
        var matchState = MatchState.WAITING
        var rounds = 0
    }

    override fun onEnable() {
        plugin = this

        // init listeners
        Bukkit.getPluginManager().registerEvents(GameListener(), this)
    }

    lateinit var startTask: BukkitTask
    fun start() {
        // check should start
        if (started || starting) return
        started = true

        // wait 10 seconds before start
        val i = AtomicInteger(0)
        startTask = Bukkit.getScheduler().runTaskTimer(this, Runnable {
            val tick = i.getAndIncrement()

            if (tick == 200) {
                // send message started
                Bukkit.getOnlinePlayers().forEach {
                    it.sendTitle("${ChatColor.BLUE}Game starting!", "", 1, 20, 1)
                }
            } else if (tick == 220) {
                // after 1 second, change game state and stop loop
                changeGameState(MatchState.CLASS_SELECT)
                Bukkit.getScheduler().cancelTask(startTask.taskId)
            } else if (tick % 20 == 0) {
                // send time to start title
                val secondsLeft = 10 - (tick / 20)
                Bukkit.getOnlinePlayers().forEach {
                    it.sendActionBar(secondsLeft.toString())
                }
            }
        }, 0L, 1L)
    }

    fun close() {
        // kick all players and shutdown
        Bukkit.getOnlinePlayers().forEach {
            it.kickPlayer("Game over!") // todo replace with send to hub
        }
        Bukkit.shutdown()
    }

    fun changeGameState(newState: MatchState) {
        // close old game state
        when (matchState) {
            MatchState.CLASS_SELECT -> classSelectManager.end()
            MatchState.ITEM_SELECT -> itemSelectManager.end()
            MatchState.ROUND -> roundManager.end()
            else -> {
                println("Nothing for closing state $matchState")
            }
        }

        // open new game state
        matchState = newState
        when (newState) {
            MatchState.CLASS_SELECT -> classSelectManager.start()
            MatchState.ITEM_SELECT -> itemSelectManager.start()
            MatchState.ROUND -> roundManager.start()
            MatchState.CLOSING -> close()
            else -> {
                println("Nothing for opening state $newState")
            }
        }
    }
}
enum class Team {
    RED, BLUE
}