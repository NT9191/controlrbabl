package com.github.nt919.controlrbabl;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import org.apache.logging.log4j.Logger;

public class ControlrBablMod {
    @Entrypoint.Namespace
    public static final Namespace NAMESPACE = Null.get();

    @Entrypoint.Logger
    public static final Logger LOGGER = Null.get();

    @Entrypoint.Instance
    public static final ControlrBablMod INSTANCE = Null.get();

    @EventListener
    public void init() {
        LOGGER.info("ControlBabl initialized!");
        LOGGER.info("Controller support coming soon...");
    }
}
