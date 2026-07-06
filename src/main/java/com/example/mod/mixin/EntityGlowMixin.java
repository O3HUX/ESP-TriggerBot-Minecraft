package com.example.mod.mixin;

import com.example.mod.TriggerEspMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityGlowMixin {

    @Inject(method = "isGlowing", at = @At("RETURN"), cancellable = true)
    private void onIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (!TriggerEspMod.esp) return;
        Entity self = (Entity) (Object) this;
        if (self instanceof PlayerEntity) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.player != self) {
                cir.setReturnValue(true);
            }
        }
    }
}