package me.mi0

import gg.essential.api.EssentialAPI
import me.mi0.features.AutoSalvage
import me.mi0.features.CarpentryFeature
import me.mi0.features.CarpentryFeature.currentState
import me.mi0.features.CarpentryFeature.start
import me.mi0.features.CarpentryFeature.stop
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import org.apache.logging.log4j.Logger
import org.lwjgl.input.Keyboard

var mc: Minecraft = Minecraft.getMinecraft()
lateinit var logger: Logger

const val MODID = "carpentry"
const val VERSION = "1.0"

@Mod(modid = MODID, version = VERSION)
class Carpentry {
    private lateinit var carpentryBind: KeyBinding

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent?) {
        mc = Minecraft.getMinecraft()
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(AutoSalvage)
        EssentialAPI.getCommandRegistry().registerCommand(CarpentryCommand())
        carpentryBind = KeyBinding("Carpentry Helper", Keyboard.KEY_SEMICOLON, "Carpentry")
        ClientRegistry.registerKeyBinding(carpentryBind)
    }

    @SubscribeEvent
    fun input(event: InputEvent?) {
        if (carpentryBind.isPressed) {
            if (currentState === CarpentryFeature.State.WAITING) {
                start()
            } else {
                stop()
            }
        }
    }
}