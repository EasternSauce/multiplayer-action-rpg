package com.mygdx.game.physics;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.body.*;
import com.mygdx.game.physics.event.*;
import com.mygdx.game.physics.world.PhysicsWorld;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class GamePhysics {

    final List<PhysicsEvent> physicsEventQueue = Collections.synchronizedList(new ArrayList<>());
    Map<AreaId, PhysicsWorld> physicsWorlds;
    Map<CreatureId, CreatureBody> creatureBodies = new HashMap<>();
    Map<AbilityId, AbilityBody> abilityBodies = new HashMap<>();
    Set<AreaGateBody> areaGateBodies = new HashSet<>();

    Map<LootPileId, LootPileBody> lootPileBodies = new HashMap<>();

    Box2DDebugRenderer debugRenderer;
    Boolean isForceUpdateBodyPositions = false;

    public void init(Map<AreaId, TiledMap> maps, GameUpdatable game) {
        physicsWorlds = maps.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> PhysicsWorld.of(entry.getValue())));

        physicsWorlds.forEach((areaId, physicsWorld) -> {
            physicsWorld.init();
            createContactListener(physicsWorld);
        });

        areaGateBodies = game.getGameState().getAreaGates().stream().map(AreaGateBody::of).collect(Collectors.toSet());
        areaGateBodies.forEach(areaGateBody -> areaGateBody.init(game));


    }

    public void onContactStart(Object objA, Object objB) {
        if (objA instanceof CreatureBody && objB instanceof AbilityBody) {
            CreatureBody creatureBody = (CreatureBody) objA;
            AbilityBody abilityBody = (AbilityBody) objB;

            physicsEventQueue.add(AbilityHitsCreatureEvent.of(abilityBody.getCreatureId(),
                    creatureBody.getCreatureId(),
                    abilityBody.getAbilityId()));


        } else if (objA instanceof TerrainTileBody && objB instanceof AbilityBody) {
            TerrainTileBody terrainTileBody = (TerrainTileBody) objA;
            if (!terrainTileBody.getIsFlyover()) {
                AbilityBody abilityBody = (AbilityBody) objB;
                Vector2 tilePos = Vector2.of(terrainTileBody.getB2Body().getWorldCenter().x,
                        terrainTileBody.getB2Body().getWorldCenter().y);

                Vector2 abilityPos = Vector2.of(abilityBody.getB2body().getWorldCenter().x,
                        abilityBody.getB2body().getWorldCenter().y);

                physicsEventQueue.add(AbilityHitsTerrainEvent.of(abilityBody.getAbilityId(), abilityPos, tilePos));
            }

        } else if (objA instanceof AbilityBody && objB instanceof AbilityBody) {
            AbilityBody abilityBodyA = (AbilityBody) objA;
            AbilityBody abilityBodyB = (AbilityBody) objB;

            physicsEventQueue.add(AbilityHitsAbilityEvent.of(abilityBodyA.getAbilityId(), abilityBodyB.getAbilityId()));


        } else if (objA instanceof CreatureBody && objB instanceof AreaGateBody) {
            CreatureBody creatureBody = (CreatureBody) objA;
            AreaGateBody areaGateBody = (AreaGateBody) objB;

            physicsEventQueue.add(CreatureHitsAreaGateEvent.of(creatureBody.getCreatureId(),
                    areaGateBody.getAreaGate()));


        } else if (objA instanceof CreatureBody && objB instanceof LootPileBody) {
            CreatureBody creatureBody = (CreatureBody) objA;
            LootPileBody lootPileBody = (LootPileBody) objB;

            physicsEventQueue.add(CreatureHitsLootPileEvent.of(creatureBody.getCreatureId(),
                    lootPileBody.getLootPileId()));

        }
    }

    public void onContactEnd(Object objA, Object objB) {
        if (objA instanceof CreatureBody && objB instanceof AreaGateBody) {
            CreatureBody creatureBody = (CreatureBody) objA;
            AreaGateBody areaGateBody = (AreaGateBody) objB;
            physicsEventQueue.add(CreatureLeavesAreaGateEvent.of(creatureBody.getCreatureId(),
                    areaGateBody.getAreaGate()));
        } else if (objA instanceof CreatureBody && objB instanceof LootPileBody) {
            CreatureBody creatureBody = (CreatureBody) objA;
            LootPileBody lootPileBody = (LootPileBody) objB;
            physicsEventQueue.add(CreatureLeavesLootPileEvent.of(creatureBody.getCreatureId(),
                    lootPileBody.getLootPileId()));
        }
    }


    private void createContactListener(PhysicsWorld physicsWorld) {
        World b2World = physicsWorld.getB2world();

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
                if (contact.getFixtureA() != null && contact.getFixtureB() != null) {
                    Object objA = contact.getFixtureA().getBody().getUserData();
                    Object objB = contact.getFixtureB().getBody().getUserData();

                    onContactEnd(objA, objB);
                    onContactEnd(objB, objA);
                }
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
