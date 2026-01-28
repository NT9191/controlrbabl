package com.github.nt919.controlrbabl.util;

import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Loads native libraries bundled in the mod's resources
 */
public class NativeLoader {

    /**
     * Extract and load the jinput native library from resources
     * @param logger Logger for error/info messages
     * @return true if successful, false otherwise
     */
    public static boolean loadJInputNative(Logger logger) {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch");

        String nativeFileName;
        String resourcePath;

        // Determine which native library to load
        if (osName.contains("linux")) {
            if (osArch.contains("64")) {
                nativeFileName = "libjinput-linux64.so";
                resourcePath = "/natives/linux/libjinput-linux64.so";
            } else {
                nativeFileName = "libjinput-linux.so";
                resourcePath = "/natives/linux/libjinput-linux.so";
            }
        } else if (osName.contains("windows")) {
            if (osArch.contains("64")) {
                nativeFileName = "jinput-dx8_64.dll";
                resourcePath = "/natives/windows/jinput-dx8_64.dll";
            } else {
                nativeFileName = "jinput-dx8.dll";
                resourcePath = "/natives/windows/jinput-dx8.dll";
            }
        } else if (osName.contains("mac")) {
            nativeFileName = "libjinput-osx.dylib";
            resourcePath = "/natives/macos/libjinput-osx.dylib";
        } else {
            logger.error("[ControlrBabl] Unsupported operating system: " + osName);
            return false;
        }

        logger.info("[ControlrBabl] Attempting to load native: " + nativeFileName);
        logger.info("[ControlrBabl] OS: " + osName + ", Arch: " + osArch);

        try {
            // Try to load from resources
            InputStream nativeStream = NativeLoader.class.getResourceAsStream(resourcePath);

            if (nativeStream == null) {
                logger.warn("[ControlrBabl] Native library not found in resources: " + resourcePath);
                logger.info("[ControlrBabl] Falling back to system library...");
                return false; // Let LWJGL try to find it on the system
            }

            // Create temp file
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "controlrbabl-natives");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            File tempNative = new File(tempDir, nativeFileName);

            // Extract native to temp file
            logger.info("[ControlrBabl] Extracting native to: " + tempNative.getAbsolutePath());

            try (FileOutputStream fos = new FileOutputStream(tempNative)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = nativeStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            nativeStream.close();

            // Load the native library
            System.load(tempNative.getAbsolutePath());

            logger.info("[ControlrBabl] Successfully loaded native library!");
            return true;

        } catch (IOException e) {
            logger.error("[ControlrBabl] Failed to extract native library: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (UnsatisfiedLinkError e) {
            logger.error("[ControlrBabl] Failed to load native library: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}