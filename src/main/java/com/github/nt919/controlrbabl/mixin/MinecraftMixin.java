package com.github.nt919.controlrbabl.mixin;

import com.github.nt919.controlrbabl.ControlrBablMod;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into Minecraft's main tick to poll controller every frame
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        // Poll controller state every tick
        ControlrBablMod.tick();
    }
}