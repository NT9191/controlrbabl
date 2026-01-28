package com.github.nt919.controlrbabl;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import com.github.nt919.controlrbabl.controller.ControllerManager;
import org.apache.logging.log4j.Logger;

public class ControlrBablMod {
    @Entrypoint.Namespace
    public static final Namespace NAMESPACE = Null.get();

    @Entrypoint.Logger
    public static final Logger LOGGER = Null.get();

    @Entrypoint.Instance
    public static final ControlrBablMod INSTANCE = Null.get();

    // Controller system
    private static ControllerManager controllerManager;
    private static boolean enabled = false;
    private static boolean initialized = false;

    @EventListener
    public void init(InitEvent event) {
        LOGGER.info("[ControlrBabl] Mod loaded - controller initialization will happen after game starts");
    }

    /**
     * Called every game tick via Mixin injection
     * This is where we poll the controller state
     */
    public static void tick() {
        // Initialize on first tick (when OpenGL context is ready)
        if (!initialized) {
            initialized = true;
            LOGGER.info("[ControlrBabl] Initializing controller system...");

            controllerManager = ControllerManager.getInstance(LOGGER);
            boolean initSuccess = controllerManager.initialize();

            if (initSuccess && controllerManager.getControllerCount() > 0) {
                enabled = true;
                LOGGER.info("[ControlrBabl] Enabled - " + controllerManager.getControllerCount() + " controller(s) detected");
                if (controllerManager.getActiveController() != null) {
                    LOGGER.info("[ControlrBabl] Active controller: " + controllerManager.getActiveController().getName());
                }
            } else {
                LOGGER.info("[ControlrBabl] No controllers detected - mod will remain dormant");
            }
        }

        if (!enabled || controllerManager == null) {
            return;
        }

        // Poll controller state
        controllerManager.poll();

        // TODO: Process controller input and apply to game
    }

    /**
     * Get the controller manager instance
     */
    public static ControllerManager getControllerManager() {
        return controllerManager;
    }

    /**
     * Check if controller support is enabled
     */
    public static boolean isEnabled() {
        return enabled;
    }
}