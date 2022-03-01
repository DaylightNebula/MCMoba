package daylightnebula.mcmobaplugin

import br.com.devsrsouza.kotlinbukkitapi.dsl.command.simpleCommand
import br.com.devsrsouza.kotlinbukkitapi.extensions.scheduler.scheduler
import daylightnebula.mcmobaplugin.managers.ClassSelectManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.atomic.AtomicInteger

class Main: JavaPlugin() {

    companion object {
        lateinit var plugin: Main

        // constants
        const val MAX_ROUNDS = 7

        // managers
        val classSelectManager = ClassSelectManager()

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

        // commands
        simpleCommand("forcestart") {
            start()
        }

        // force close inventories unless they have an allowed inventory open (in theory, cancels players inventory)
        scheduler {
            Bukkit.getOnlinePlayers().forEach {
                if (!allowedInventories.contains(it.openInventory.title))
                    it.closeInventory()
            }
        }.runTaskTimer(this,  0L, 1L)
    }

    fun start() {
        // check should start
        if (started || starting) return
        started = true

        // wait 10 seconds before start
        val i = AtomicInteger(0)
        scheduler {
            val tick = i.getAndIncrement()

            if (tick == 200) {
                // send message started
                Bukkit.getOnlinePlayers().forEach {
                    it.sendTitle("${ChatColor.BLUE}Game starting!", "", 1, 20, 1)
                }
            } else if (tick == 220) {
                // after 1 second, change game state and stop loop
                changeGameState(MatchState.CLASS_SELECT)
                cancel()
            } else if (tick % 20 == 0) {
                // send time to start title
                val secondsLeft = 10 - (tick / 20)
                Bukkit.getOnlinePlayers().forEach {
                    it.sendTitle(secondsLeft.toString(), "", 1, 20, 1)
                }
            }
        }.runTaskTimer(this, 0L, 1L)
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
            else -> {
                println("Nothing for closing state $matchState")
            }
        }

        // open new game state
        matchState = newState
        when (newState) {
            MatchState.CLASS_SELECT -> classSelectManager.start()
            MatchState.CLOSING -> close()
            else -> {
                println("Nothing for opening state $newState")
            }
        }
    }
}