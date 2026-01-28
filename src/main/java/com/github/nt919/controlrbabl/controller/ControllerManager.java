package com.github.nt919.controlrbabl.controller;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.LWJGLException;
import org.apache.logging.log4j.Logger;

/**
 * Manages controller detection, initialization, and polling using LWJGL 2.x
 */
public class ControllerManager {
    private static ControllerManager instance;
    private final Logger logger;

    private boolean initialized = false;
    private Controller activeController = null;
    private int activeControllerIndex = -1;

    // Deadzone for analog sticks (0.0 to 1.0)
    private float deadzone = 0.15f;

    private ControllerManager(Logger logger) {
        this.logger = logger;
    }

    public static ControllerManager getInstance(Logger logger) {
        if (instance == null) {
            instance = new ControllerManager(logger);
        }
        return instance;
    }

    /**
     * Initialize the LWJGL controller system
     */
    public boolean initialize() {
        if (initialized) {
            logger.info("[ControlrBabl] Controller system already initialized");
            return true;
        }

        try {
            // Load jinput native library from bundled resources
            logger.info("[ControlrBabl] Loading jinput native library...");
            boolean nativeLoaded = com.github.nt919.controlrbabl.util.NativeLoader.loadJInputNative(logger);

            if (!nativeLoaded) {
                logger.warn("[ControlrBabl] Could not load bundled native, trying system library...");
            }

            logger.info("[ControlrBabl] Attempting to create LWJGL Controllers...");
            Controllers.create();
            logger.info("[ControlrBabl] Controllers.create() succeeded");

            // Detect available controllers
            int controllerCount = Controllers.getControllerCount();
            logger.info("[ControlrBabl] Controllers.getControllerCount() returned: " + controllerCount);

            if (controllerCount == 0) {
                logger.warn("[ControlrBabl] No controllers found. Is your controller connected and recognized by your OS?");
                logger.info("[ControlrBabl] Note: PS5/Xbox controllers should be detected by Linux");
            }

            // Log each controller's details
            for (int i = 0; i < controllerCount; i++) {
                Controller controller = Controllers.getController(i);
                logger.info("[ControlrBabl] Controller " + i + ": " + controller.getName() +
                        " (" + controller.getButtonCount() + " buttons, " +
                        controller.getAxisCount() + " axes)");
            }

            // Select first controller as active
            if (controllerCount > 0) {
                setActiveController(0);
            } else {
                logger.info("[ControlrBabl] No controllers detected - mod will remain dormant");
            }

            initialized = true;
            return true;

        } catch (LWJGLException e) {
            logger.error("[ControlrBabl] LWJGL Exception while initializing controllers: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            logger.error("[ControlrBabl] Unexpected exception while initializing controllers: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Set the active controller by index
     */
    public boolean setActiveController(int index) {
        if (index < 0 || index >= Controllers.getControllerCount()) {
            logger.warn("[ControlrBabl] Invalid controller index: " + index);
            return false;
        }

        activeController = Controllers.getController(index);
        activeControllerIndex = index;
        logger.info("[ControlrBabl] Active controller set to: " + activeController.getName());
        return true;
    }

    /**
     * Poll all controllers to update their state
     * Should be called every tick
     */
    public void poll() {
        if (!initialized || activeController == null) {
            return;
        }

        try {
            Controllers.poll();
        } catch (Exception e) {
            logger.error("[ControlrBabl] Error polling controllers: " + e.getMessage());
        }
    }

    /**
     * Check if a button is currently pressed
     */
    public boolean isButtonPressed(int buttonIndex) {
        if (activeController == null || buttonIndex >= activeController.getButtonCount()) {
            return false;
        }
        return activeController.isButtonPressed(buttonIndex);
    }

    /**
     * Get axis value with deadzone applied (-1.0 to 1.0)
     */
    public float getAxisValue(int axisIndex) {
        if (activeController == null || axisIndex >= activeController.getAxisCount()) {
            return 0.0f;
        }

        float value = activeController.getAxisValue(axisIndex);

        // Apply deadzone
        if (Math.abs(value) < deadzone) {
            return 0.0f;
        }

        // Rescale to account for deadzone
        float sign = value > 0 ? 1.0f : -1.0f;
        float rescaled = (Math.abs(value) - deadzone) / (1.0f - deadzone);
        return sign * rescaled;
    }

    /**
     * Get raw axis value without deadzone
     */
    public float getAxisValueRaw(int axisIndex) {
        if (activeController == null || axisIndex >= activeController.getAxisCount()) {
            return 0.0f;
        }
        return activeController.getAxisValue(axisIndex);
    }

    /**
     * Get POV (D-pad) X value
     */
    public float getPovX() {
        if (activeController == null) {
            return 0.0f;
        }
        return activeController.getPovX();
    }

    /**
     * Get POV (D-pad) Y value
     */
    public float getPovY() {
        if (activeController == null) {
            return 0.0f;
        }
        return activeController.getPovY();
    }

    /**
     * Check if any controller is connected
     */
    public boolean isControllerConnected() {
        return initialized && activeController != null;
    }

    /**
     * Get the active controller instance
     */
    public Controller getActiveController() {
        return activeController;
    }

    /**
     * Get total controller count
     */
    public int getControllerCount() {
        return initialized ? Controllers.getControllerCount() : 0;
    }

    /**
     * Set the deadzone for analog sticks
     */
    public void setDeadzone(float deadzone) {
        this.deadzone = Math.max(0.0f, Math.min(1.0f, deadzone));
        logger.info("[ControlrBabl] Deadzone set to: " + this.deadzone);
    }

    /**
     * Get current deadzone value
     */
    public float getDeadzone() {
        return deadzone;
    }

    /**
     * Cleanup and destroy controller system
     */
    public void destroy() {
        if (initialized) {
            Controllers.destroy();
            initialized = false;
            activeController = null;
            logger.info("[ControlrBabl] Controller system destroyed");
        }
    }
}