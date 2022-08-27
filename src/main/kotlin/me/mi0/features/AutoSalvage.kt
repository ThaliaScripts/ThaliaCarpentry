package me.mi0.features

import me.mi0.CarpentryConfig
import me.mi0.mc
import me.mi0.mixins.IMinecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


object AutoSalvage {
    var inGui: Boolean = false
    var dungeonBlacksmith: Boolean = false
    var craftItemGui: Boolean = false
    var timeClosed = Long.MIN_VALUE

    @SubscribeEvent
    fun onGuiOpen(event: GuiOpenEvent) {
        if (CarpentryFeature.currentState == CarpentryFeature.State.WAITING) {
            return
        }

        val name = getGuiName(event.gui ?: return)
        lastItem = -1
        when (name) {
            "Salvage Item" -> {
                inGui = true
                dungeonBlacksmith = false
                craftItemGui = false
            }
            "Dungeon Blacksmith" -> {
                dungeonBlacksmith = true
                inGui = false
                craftItemGui = false
            }
            "Craft Item" -> {
                craftItemGui = true
                dungeonBlacksmith = false
                inGui = false
            }
        }
    }

    var tickCounter = 0
    var lastItem = -1
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (CarpentryFeature.currentState == CarpentryFeature.State.WAITING) {
            return
        }

        if (!(mc.thePlayer.posX.toInt() == -80 && mc.thePlayer.posY.toInt() == 56 && mc.thePlayer.posZ.toInt() == -119)) {
            CarpentryFeature.stop()
            return
        }

        if (mc.currentScreen !is GuiChest) {
            inGui = false
            dungeonBlacksmith = false
            craftItemGui = false

            val currentTime = System.currentTimeMillis()
            if (currentTime - CarpentryConfig.openInvMS > timeClosed) {
                timeClosed = currentTime

                var foundInv = mc.thePlayer.inventoryContainer.inventorySlots.filter {
                    it.hasStack && it.stack.hasDisplayName() && it.stack.displayName.contains("Enchanted Mycelium Cube")
                }
                if (foundInv.isNotEmpty()) {
                    mc.thePlayer.sendChatMessage("/craft")
                    return
                }

                foundInv = mc.thePlayer.inventoryContainer.inventorySlots.filter {
                    it.hasStack && it.stack.hasDisplayName() && it.stack.displayName.contains("Shimmering Light Slippers")
                }
                if (foundInv.isNotEmpty()) {
                    (mc as IMinecraft).rightClickMouse()
                    return
                }
            }

            return
        }

        val gui = mc.currentScreen as GuiChest
        val slots = gui.inventorySlots.inventorySlots?.slice(0 until 45) ?: return

        tickCounter++
        if (dungeonBlacksmith && tickCounter % CarpentryConfig.tickSpeedSalvage == 0) {
            val found = slots.find {
                it.hasStack && it.stack.displayName.contains("Salvage Items")
            } ?: return
            mc.playerController.windowClick(
                mc.thePlayer.openContainer.windowId,
                found.slotIndex,
                2,
                0,
                mc.thePlayer
            )
        } else if (inGui) {
            val foundFuckUp = slots.filter {
                var name: String? = null
                if (it.hasStack) {
                    name = getInternalName(it.stack)
                }
                (name != null && name == "X") || (it.hasStack && it.stack.hasDisplayName() && it.stack.displayName.contains("Enchanted Mycelium Cube"))
            }
            if (foundFuckUp.isNotEmpty()) {
                mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, foundFuckUp.first().slotNumber, 0, 1, mc.thePlayer)
                return
            }

            val found = slots.filter {
                it.hasStack && shouldSalvage(it.stack)
            }

            val foundInv = mc.thePlayer.inventoryContainer.inventorySlots.filter {
                it.hasStack && it.stack.hasDisplayName() && it.stack.displayName.contains("Shimmering Light Slippers")
            }

            if (found.isNotEmpty()) {
                if (tickCounter % CarpentryConfig.tickSpeedSalvage == 0) {
                    val findSalvageButton = slots.find {
                        it.hasStack && it.stack.hasDisplayName() && it.stack.displayName.contains("Salvage Item")
                    } ?: return
                    mc.playerController.windowClick(
                        mc.thePlayer.openContainer.windowId,
                        findSalvageButton.slotNumber,
                        2,
                        0,
                        mc.thePlayer
                    )
                }
            } else if (foundInv.isNotEmpty()) {
                if (tickCounter % CarpentryConfig.tickSpeedItemAfter == 0) {
                    mc.playerController.windowClick(
                        mc.thePlayer.openContainer.windowId,
                        45 + foundInv.first().slotNumber,
                        0,
                        1,
                        mc.thePlayer
                    )
                }
            } else if (tickCounter % CarpentryConfig.tickSpeedSalvage == 0) {
                mc.thePlayer.closeScreen()
                timeClosed = System.currentTimeMillis()
            }
        }
        else if (tickCounter % CarpentryConfig.tickSpeedCraft == 0 && craftItemGui) {
            val craftThing = slots.filter {
                it.hasStack && it.stack.hasDisplayName() && it.stack.displayName.contains("Shimmering Light Slippers")
            }

            val foundInv = mc.thePlayer.inventoryContainer.inventorySlots.filter {
                it.hasStack && it.stack.hasDisplayName() && it.stack.displayName.contains("Enchanted Mycelium Cube")
            }

            if (foundInv.isEmpty()) {
                mc.thePlayer.closeScreen()
                timeClosed = System.currentTimeMillis()
                return
            }

            if (craftThing.isNotEmpty()) {
                lastItem = craftThing.first().slotNumber
            }
            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, lastItem, 0, 1, mc.thePlayer)
        }
    }

    fun shouldSalvage(item: ItemStack): Boolean =
        getInternalName(item) == "SHIMMERING_LIGHT_SLIPPERS"

    fun getInternalName(stack: ItemStack): String? {
        val tag: NBTTagCompound = stack.tagCompound ?: return ""
        return getInternalNameFromNBT(tag)
    }

    fun getInternalNameFromNBT(tag: NBTTagCompound): String? {
        if (!tag.hasKey("ExtraAttributes", 10)) {
            return null
        }

        val ea = tag.getCompoundTag("ExtraAttributes")
        if (!ea.hasKey("id", 8)) {
            return null
        }
        return ea.getString("id").replace(":", "-")
    }

    fun getGuiName(guiScreen: GuiScreen): String {
        return if (guiScreen is GuiChest) {
            (guiScreen.inventorySlots as ContainerChest).lowerChestInventory.displayName.unformattedText
        } else ""
    }
}