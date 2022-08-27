package me.mi0.features

import me.mi0.Carpentry
import me.mi0.mc
import net.minecraft.util.ChatComponentText

object CarpentryFeature {
    enum class State { WAITING, WORKING }
    var currentState = State.WAITING

    fun start() {
        if (currentState == State.WAITING) {
            currentState = State.WORKING
            mc.thePlayer.addChatMessage(ChatComponentText("§7[CarpentryHelper] §rAcquiring bitches!"))
        }
    }

    fun stop() {
        if (currentState == State.WORKING) {
            currentState = State.WAITING
            mc.thePlayer.addChatMessage(ChatComponentText("§7[CarpentryHelper] §rDT! DT! DT! This is not how you get weight!"))
        }
    }
}