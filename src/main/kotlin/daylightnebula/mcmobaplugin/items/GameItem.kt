package daylightnebula.mcmobaplugin.items

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

abstract class GameItem(val id: Int, val type: GameItemType, val name: String, val desc: String, val displayStack: ItemStack) {

    companion object {
        // lists
        val allItems = mutableListOf<GameItem>()
        val organizedItems = hashMapOf<GameItemType, MutableList<GameItem>>()

        // items
        // primary
        val shortBowGameItem = ShortBowGameItem(0)

        // secondary
        val knifeGameItem = KnifeGameItem(1)

        // armor
        val lightArmorGameItem = LightArmorGameItem(2)
    }

    // on init, add the item to its lists
    init {
        allItems.add(this)
        val list = organizedItems[type]
        if (list == null) {
            organizedItems[type] = mutableListOf(this)
        } else
            list.add(this)
    }

    abstract fun select()
    abstract fun deselect()
    abstract fun useItem()
    abstract fun canUse(): Boolean
}
enum class GameItemType {
    PRIMARY,
    SECOND,
    ARMOR
}
class ShortBowGameItem(id: Int): GameItem(
    id, GameItemType.PRIMARY,
    "${ChatColor.GOLD}Short Bow", "A ranged weapon designed to be quick and effective.",
    ItemStack(Material.BOW)
) {
    override fun select() {
        TODO("Not yet implemented")
    }

    override fun deselect() {
        TODO("Not yet implemented")
    }

    override fun useItem() {
        TODO("Not yet implemented")
    }

    override fun canUse(): Boolean {
        TODO("Not yet implemented")
    }
}
class KnifeGameItem(id: Int): GameItem(
    id, GameItemType.SECOND,
    "${ChatColor.GRAY}Knife", "A good sidearm meant to be quick and effective",
    ItemStack(Material.FEATHER)
) {
    override fun select() {
        TODO("Not yet implemented")
    }

    override fun deselect() {
        TODO("Not yet implemented")
    }

    override fun useItem() {
        TODO("Not yet implemented")
    }

    override fun canUse(): Boolean {
        TODO("Not yet implemented")
    }
}
class LightArmorGameItem(id: Int): GameItem(
    id, GameItemType.ARMOR,
    "${ChatColor.BLUE}Light Armor", "A light armor designed to give some protection while allowing for speed.",
    ItemStack(Material.LEATHER_CHESTPLATE)
) {
    override fun select() {
        TODO("Not yet implemented")
    }

    override fun deselect() {
        TODO("Not yet implemented")
    }

    override fun useItem() {
        TODO("Not yet implemented")
    }

    override fun canUse(): Boolean {
        TODO("Not yet implemented")
    }
}