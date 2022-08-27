package me.mi0

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import java.io.File

object CarpentryConfig : Vigilant(File(mc.mcDataDir.toString() + "/config" + "/carpentry", "carpentry.toml"), "Carpentry") {
    @Property(
        type = PropertyType.NUMBER,
        name = "Tick Speed Salvage",
        description = "I'm not typing shit here!",
        min = 1, max = 100,
        category = "General"
    )
    var tickSpeedSalvage = 15

    @Property(
        type = PropertyType.NUMBER,
        name = "Tick Speed Item After Salvage",
        description = "I'm not typing shit here!",
        min = 1, max = 100,
        category = "General"
    )
    var tickSpeedItemAfter = 1

    @Property(
        type = PropertyType.NUMBER,
        name = "Tick Speed Craft",
        description = "I'm not typing shit here!",
        min = 1, max = 100,
        category = "General"
    )
    var tickSpeedCraft = 1

    @Property(
        type = PropertyType.NUMBER,
        name = "MS Between Opening Guis",
        description = "I'm not typing shit here!",
        min = 0, max = 10000,
        increment = 100,
        category = "General"
    )
    var openInvMS = 1000

    init {
        File(mc.mcDataDir.toString() + "/config" + "/carpentry").mkdir()
        initialize()
    }

}