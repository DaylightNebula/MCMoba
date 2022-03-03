package daylightnebula.mcmobaplugin.classes

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class GameClass(val id: Int, val name: String, val desc: String, val displayStack: ItemStack, val primaryItemIDs: Array<Int>, val secondItemIDs: Array<Int>, val armorItemIDs: Array<Int>) {
    companion object {
        val classes = mutableListOf<GameClass>()

        // classes
        val fighter = GameClass(
            0,
            "${ChatColor.BLUE}${ChatColor.BOLD}Fighter",
            "A simple all around melee class, designed for a good combination of speed, armor, and damage.",
            ItemStack(Material.IRON_SWORD),
            arrayOf(0), arrayOf(1), arrayOf(2)
        )
        val tank = GameClass(
            1,
            "${ChatColor.GRAY}${ChatColor.BOLD}Tank",
            "A slow melee class that leverages a large amounts of armor to survive to deal large amounts of damage to its enemies.",
            ItemStack(Material.NETHERITE_CHESTPLATE),
            arrayOf(0), arrayOf(1), arrayOf(2)
        )
        val assassin = GameClass(
            2,
            "${ChatColor.RED}${ChatColor.BOLD}Assassin",
            "A fast melee class that leverages speed to quickly defeat its enemies.",
            ItemStack(Material.SPIDER_EYE),
            arrayOf(0), arrayOf(1), arrayOf(2)
        )
        val marksperson = GameClass(
            3,
            "${ChatColor.GOLD}${ChatColor.BOLD}Marksperson",
            "A ranged class that uses range to defeat its enemies.",
            ItemStack(Material.BOW),
            arrayOf(0), arrayOf(1), arrayOf(2)
        )
        val support = GameClass(
            4,
            "${ChatColor.GREEN}${ChatColor.BOLD}Support",
            "A class that uses its allies to defeat its enemies.",
            ItemStack(Material.EMERALD),
            arrayOf(0), arrayOf(1), arrayOf(2)
        )
    }

    init {
        classes.add(this)
    }
}