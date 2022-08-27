package me.mi0

import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.utils.GuiUtil
import java.util.*

class CarpentryCommand : Command("carp") {
    @DefaultHandler
    fun handle() {
        GuiUtil.open(Objects.requireNonNull(CarpentryConfig.gui()))
    }
}