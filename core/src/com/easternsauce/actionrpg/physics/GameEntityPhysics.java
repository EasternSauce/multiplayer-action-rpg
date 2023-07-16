package com.easternsauce.actionrpg.physics;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.*;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.area.AreaGateId;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.area.LootPileId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.body.*;
import com.easternsauce.actionrpg.physics.event.*;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class GameEntityPhysics {
    @Getter
    private final List<PhysicsEvent> physicsEventQueue = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private Map<AreaId, PhysicsWorld> physicsWorlds;
    @Getter
    private Map<CreatureId, CreatureBody> creatureBodies = new HashMap<>();
    @Getter
    private Map<AbilityId, AbilityBody> abilityBodies = new HashMap<>();
    @Getter
    private Map<AreaGateId, AreaGateBody> areaGateBodies = new HashMap<>();

    @Getter
    private Map<LootPileId, LootPileBody> lootPileBodies = new HashMap<>();

    @Getter
    @Setter
    private Box2DDebugRenderer debugRenderer;
    @Getter
    @Setter
    private Boolean forceUpdateBodyPositions = false;

    public void init(Map<AreaId, TiledMap> maps, CoreGame game) {
        physicsWorlds = maps.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
            entry -> PhysicsWorld.of(entry.getValue())
        ));

        physicsWorlds.forEach((areaId, physicsWorld) -> { // TODO: do this dynamically
            physicsWorld.init();
            createContactListener(physicsWorld);
        });

        this.areaGateBodies = game // TODO: do this dynamically
                                   .getGameState()
                                   .getAreaGates()
                                   .keySet()
                                   .stream()
                                   .collect(Collectors.toMap(areaGateId -> areaGateId, AreaGateBody::of));

        this.areaGateBodies.values().forEach(areaGateBody -> areaGateBody.init(game));

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

    public void onContactStart(Object objA, Object objB) {
        if (objA instanceof CreatureBody && objB instanceof AbilityBody) {
            CreatureBody creatureBody = (CreatureBody) objA;
            AbilityBody abilityBody = (AbilityBody) objB;

            physicsEventQueue.add(AbilityHitsCreatureEvent.of(abilityBody.getCreatureId(),
                creatureBody.getCreatureId(),
                abilityBody.getAbilityId()
            ));

        } else if (objA instanceof TerrainTileBody && objB instanceof AbilityBody) {
            TerrainTileBody terrainTileBody = (TerrainTileBody) objA;
            if (!terrainTileBody.getFlyover()) {
                AbilityBody abilityBody = (AbilityBody) objB;
                Vector2 tilePos = Vector2.of(terrainTileBody.getB2body().getWorldCenter().x,
                    terrainTileBody.getB2body().getWorldCenter().y
                );

                Vector2 abilityPos = Vector2.of(abilityBody.getB2body().getWorldCenter().x,
                    abilityBody.getB2body().getWorldCenter().y
                );

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
                areaGateBody.getAreaGateId()
            ));

        } else if (objA instanceof CreatureBody && objB instanceof LootPileBody) {
            CreatureBody creatureBody = (CreatureBody) objA;
            LootPileBody lootPileBody = (LootPileBody) objB;

            physicsEventQueue.add(CreatureHitsLootPileEvent.of(creatureBody.getCreatureId(),
                lootPileBody.getLootPileId()
            ));

        }
    }

    public void onContactEnd(Object objA, Object objB) {
        if (objA instanceof CreatureBody && objB instanceof AreaGateBody) {
            CreatureBody creatureBody = (CreatureBody) objA;
            AreaGateBody areaGateBody = (AreaGateBody) objB;
            physicsEventQueue.add(CreatureLeavesAreaGateEvent.of(creatureBody.getCreatureId(),
                areaGateBody.getAreaGateId()
            ));
        } else if (objA instanceof CreatureBody && objB instanceof LootPileBody) {
            CreatureBody creatureBody = (CreatureBody) objA;
            LootPileBody lootPileBody = (LootPileBody) objB;
            physicsEventQueue.add(CreatureLeavesLootPileEvent.of(creatureBody.getCreatureId(),
                lootPileBody.getLootPileId()
            ));
        }
    }
}
