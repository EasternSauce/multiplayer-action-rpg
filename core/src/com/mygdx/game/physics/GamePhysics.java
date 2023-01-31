package com.mygdx.game.physics;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.physics.event.AbilityHitsCreatureEvent;
import com.mygdx.game.physics.event.AbilityHitsTerrainEvent;
import com.mygdx.game.physics.event.PhysicsEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class GamePhysics {

    Map<AreaId, PhysicsWorld> physicsWorlds;

    Map<CreatureId, CreatureBody> creatureBodies = new HashMap<>();
    Map<AbilityId, AbilityBody> abilityBodies = new HashMap<>();

    Box2DDebugRenderer debugRenderer;

    Boolean forceUpdateBodyPositions = false;

    final List<PhysicsEvent> physicsEventQueue = new LinkedList<>();

    public void init(Map<AreaId, TiledMap> maps, GameState gameState) {
        physicsWorlds = maps.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> PhysicsWorld.of(entry.getValue())));

        physicsWorlds.forEach((areaId, physicsWorld) -> {
            physicsWorld.init();
            createContactListener(physicsWorld);
        });

    }

    public void onContactStart(Object objA, Object objB) {
        if (objA instanceof CreatureBody && objB instanceof AbilityBody) {
            CreatureBody creatureBody = (CreatureBody) objA;
            AbilityBody abilityBody = (AbilityBody) objB;
            if (!abilityBody.creatureId().equals(creatureBody.creatureId())) {
                synchronized (physicsEventQueue) {
                    physicsEventQueue.add(
                            AbilityHitsCreatureEvent.of(abilityBody.creatureId(), creatureBody.creatureId(),
                                    abilityBody.abilityId()));
                }
            }
        }
        if (objA instanceof TerrainTileBody && objB instanceof AbilityBody) {
//            TerrainTileBody terrainTileBody = (TerrainTileBody) objA;
            AbilityBody abilityBody = (AbilityBody) objB;
            synchronized (physicsEventQueue) {
                physicsEventQueue.add(AbilityHitsTerrainEvent.of(abilityBody.abilityId()));
            }

        }
    }


    private void createContactListener(PhysicsWorld physicsWorld) {
        World b2World = physicsWorld.b2world();

        ContactListener contactListener = new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Object objA = contact.getFixtureA().getBody().getUserData();
                Object objB = contact.getFixtureB().getBody().getUserData();


                onContactStart(objA, objB);
                onContactStart(objB, objA);
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        };

        b2World.setContactListener(contactListener);
    }
}
