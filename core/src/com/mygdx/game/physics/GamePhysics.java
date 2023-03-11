package com.mygdx.game.physics;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.game.data.AreaGate;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.body.AbilityBody;
import com.mygdx.game.physics.body.AreaGateBody;
import com.mygdx.game.physics.body.CreatureBody;
import com.mygdx.game.physics.body.TerrainTileBody;
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

    Map<AreaId, PhysicsWorld> physicsWorlds;

    Map<CreatureId, CreatureBody> creatureBodies = new HashMap<>();
    Map<AbilityId, AbilityBody> abilityBodies = new HashMap<>();

    Set<AreaGateBody> areaGateBodies = new HashSet<>();

    Box2DDebugRenderer debugRenderer;

    Boolean forceUpdateBodyPositions = false;

    final List<PhysicsEvent> physicsEventQueue = Collections.synchronizedList(new ArrayList<>());

    public void init(Map<AreaId, TiledMap> maps, Set<AreaGate> areaGates, MyGdxGame game) {
        physicsWorlds = maps.entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, entry -> PhysicsWorld.of(entry.getValue())));

        physicsWorlds.forEach((areaId, physicsWorld) -> {
            physicsWorld.init();
            createContactListener(physicsWorld);
        });

        areaGateBodies = areaGates.stream().map(AreaGateBody::of).collect(Collectors.toSet());
        areaGateBodies.forEach(areaGateBody -> areaGateBody.init(game));


    }

    public void onContactStart(Object objA, Object objB) {
        if (objA instanceof CreatureBody && objB instanceof AbilityBody) {
            CreatureBody creatureBody = (CreatureBody) objA;
            AbilityBody abilityBody = (AbilityBody) objB;

            physicsEventQueue.add(AbilityHitsCreatureEvent.of(abilityBody.creatureId(),
                                                              creatureBody.creatureId(),
                                                              abilityBody.abilityId()));


        }
        else if (objA instanceof TerrainTileBody && objB instanceof AbilityBody) {
            TerrainTileBody terrainTileBody = (TerrainTileBody) objA;
            if (!terrainTileBody.flyover()) {
                AbilityBody abilityBody = (AbilityBody) objB;
                Vector2
                        tilePos =
                        Vector2.of(terrainTileBody.b2Body().getWorldCenter().x,
                                   terrainTileBody.b2Body().getWorldCenter().y);

                Vector2
                        abilityPos =
                        Vector2.of(abilityBody.b2Body().getWorldCenter().x,
                                   abilityBody.b2Body().getWorldCenter().y);

                physicsEventQueue.add(AbilityHitsTerrainEvent.of(abilityBody.abilityId(), abilityPos, tilePos));
            }

        }
        else if (objA instanceof AbilityBody && objB instanceof AbilityBody) {
            AbilityBody abilityBodyA = (AbilityBody) objA;
            AbilityBody abilityBodyB = (AbilityBody) objB;

            physicsEventQueue.add(AbilityHitsAbilityEvent.of(abilityBodyA.abilityId(), abilityBodyB.abilityId()));


        }
        else if (objA instanceof CreatureBody && objB instanceof AreaGateBody) {
            CreatureBody creatureBody = (CreatureBody) objA;
            AreaGateBody areaGateBody = (AreaGateBody) objB;

            physicsEventQueue.add(CreatureHitsAreaGateEvent.of(creatureBody.creatureId(), areaGateBody.areaGate()));


        }
    }

    public void onContactEnd(Object objA, Object objB) {
        if (objA instanceof CreatureBody && objB instanceof AreaGateBody) {
            CreatureBody creatureBody = (CreatureBody) objA;
            AreaGateBody areaGateBody = (AreaGateBody) objB;
            physicsEventQueue.add(CreatureLeavesAreaGateEvent.of(creatureBody.creatureId(), areaGateBody.areaGate()));
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
