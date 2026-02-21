package com.mrcaelum.recallgrimoire.interactions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerConfigData;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerRespawnPointData;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class RecallTeleportInteraction extends SimpleInstantInteraction
{
    public static final BuilderCodec<RecallTeleportInteraction> CODEC = BuilderCodec.builder(RecallTeleportInteraction.class, RecallTeleportInteraction::new, SimpleInstantInteraction.CODEC).documentation("Teleports to the closest respawn point.").build();

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldown) {
        Ref<EntityStore> ref = context.getEntity();

        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        if (commandBuffer == null) return;

        Store<EntityStore> store = ref.getStore();

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        Ref<EntityStore> playerReference = player.getReference();
        if (playerReference == null) return;

        Transform respawnPoint = _getClosestRespawnPoint(player, commandBuffer);
        if (respawnPoint == null) return;

        commandBuffer.addComponent(playerReference, Teleport.getComponentType(), Teleport.createForPlayer(player.getWorld(), respawnPoint));
    }

    @Nullable
    private static Transform _getClosestRespawnPoint(Player player, ComponentAccessor<EntityStore> componentAccessor) {
        Ref<EntityStore> ref = player.getReference();
        World world = player.getWorld();
        if (world == null || ref == null) return null;

        PlayerConfigData playerData = player.getPlayerConfigData();
        PlayerRespawnPointData[] respawnPoints = playerData.getPerWorldData(world.getName()).getRespawnPoints();

        if (respawnPoints == null || respawnPoints.length == 0) {
            ISpawnProvider worldSpawnProvider = world.getWorldConfig().getSpawnProvider();
            if (worldSpawnProvider == null) return null;

            Transform worldSpawnPoint = worldSpawnProvider.getSpawnPoint(ref, componentAccessor);
            worldSpawnPoint.setRotation(Vector3f.ZERO);
            return worldSpawnPoint;
        }

        TransformComponent playerTransformPosition = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
        if (playerTransformPosition == null) return null;

        Vector3d playerPosition = playerTransformPosition.getPosition();
        Optional<PlayerRespawnPointData> nearestPosition = Arrays.stream(respawnPoints).min((a, b) -> {
            Vector3d posA = a.getRespawnPosition(), posB = b.getRespawnPosition();
            return Double.compare(playerPosition.distanceSquaredTo(posA.x, playerPosition.y, posA.z), playerPosition.distanceSquaredTo(posB.x, playerPosition.y, posB.z));
        });
        return new Transform((nearestPosition.get()).getRespawnPosition());
    }
}
