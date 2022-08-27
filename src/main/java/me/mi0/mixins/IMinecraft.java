package me.mi0.mixins;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Minecraft.class})
public interface IMinecraft {
    @Invoker("clickMouse")
    void leftClick();

    @Invoker("rightClickMouse")
    void rightClickMouse();
}
