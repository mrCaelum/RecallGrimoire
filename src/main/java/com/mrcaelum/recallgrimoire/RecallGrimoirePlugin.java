package com.mrcaelum.recallgrimoire;

import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.logger.HytaleLogger;
import com.mrcaelum.recallgrimoire.interactions.RecallTeleportInteraction;

import javax.annotation.Nonnull;
import java.util.logging.Level;

/**
 * RecallGrimoire - Adds the recall grimoire
 *
 * @author mrcaelum
 * @version 1.0.0
 */
public class RecallGrimoirePlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static RecallGrimoirePlugin instance;

    public RecallGrimoirePlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static RecallGrimoirePlugin getInstance() {
        return instance;
    }

    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("Setting up...");

        getCodecRegistry(Interaction.CODEC).register(
                "RecallTeleport",
                RecallTeleportInteraction.class, RecallTeleportInteraction.CODEC
        );

        LOGGER.at(Level.INFO).log("Setup complete!");
    }

    @Override
    protected void start() {
        LOGGER.at(Level.INFO).log("Started!");
    }

    @Override
    protected void shutdown() {
        LOGGER.at(Level.INFO).log("Shutting down...");
        instance = null;
    }
}