package com.example.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class TriggerEspMod implements ClientModInitializer {
    public static boolean tb = false, esp = false;
    private static boolean rDown = false, zDown = false;
    private static long lastAttack = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> onTick());
    }

    public static void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        long h = mc.getWindow().getHandle();
        boolean gui = mc.currentScreen != null;

        boolean r = GLFW.glfwGetKey(h, GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS;
        if (r && !rDown && !gui) { tb = !tb; mc.player.sendMessage(Text.literal("§eTB: " + (tb ? "§aON" : "§cOFF")), true); }
        rDown = r;

        boolean z = GLFW.glfwGetKey(h, GLFW.GLFW_KEY_Z) == GLFW.GLFW_PRESS;
        if (z && !zDown && !gui) { esp = !esp; mc.player.sendMessage(Text.literal("§eESP: " + (esp ? "§aON" : "§cOFF")), true); }
        zDown = z;

        if (tb && !gui && System.currentTimeMillis() - lastAttack >= 650) {
            Entity target = null;
            Vec3d eye = mc.player.getEyePos();
            Vec3d look = mc.player.getRotationVec(1.0F);
            Vec3d end = eye.add(look.multiply(3.0));

            for (Entity e : mc.world.getEntities()) {
                if (e instanceof LivingEntity && e != mc.player && e.isAlive()) {
                    Box expanded = e.getBoundingBox().expand(0.2);
                    if (expanded.raycast(eye, end).isPresent() && mc.player.distanceTo(e) <= 3.0) {
                        target = e;
                        break;
                    }
                }
            }

            if (target == null && mc.crosshairTarget instanceof EntityHitResult hit) {
                Entity e = hit.getEntity();
                if (e instanceof LivingEntity && e != mc.player && e.isAlive() && mc.player.distanceTo(e) <= 3.0) {
                    target = e;
                }
            }

            if (target != null) {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
                mc.options.sprintKey.setPressed(true);
                lastAttack = System.currentTimeMillis();
            }
        }
    }
}