package com.mygdx.game.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.shape.BodyShape;
import com.mygdx.game.physics.shape.Circle;
import com.mygdx.game.physics.shape.Polygon;
import com.mygdx.game.physics.shape.Rectangle;
import com.mygdx.game.physics.world.PhysicsWorld;


public class B2BodyFactory {
    public static Body createB2Body(PhysicsWorld world,
                                    Vector2 pos,
                                    BodyType bodyType,
                                    Object userData,
                                    BodyShape shape,
                                    Boolean isSensor,
                                    Boolean sleepingAllowed,
                                    Float linearDamping,
                                    Float mass) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(pos.x(), pos.y());

        Body b2body = world.b2world().createBody(bodyDef);

        b2body.setUserData(userData);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape.b2Shape();
        fixtureDef.isSensor = isSensor;

        b2body.createFixture(fixtureDef);

        if (linearDamping != null) {
            b2body.setLinearDamping(linearDamping);
        }

        if (mass != null) {
            MassData massData = new MassData();
            massData.mass = mass;
            b2body.setMassData(massData);
        }

        if (sleepingAllowed != null) {
            b2body.setSleepingAllowed(sleepingAllowed);
        }

        return b2body;
    }


    public static Body createTerrainTileB2body(PhysicsWorld world, TerrainTileBody terrainTileBody) {
        return createB2Body(world,
                            Vector2.of(terrainTileBody.pos().x() * terrainTileBody.tileWidth() +
                                       terrainTileBody.tileWidth() / 2,
                                       terrainTileBody.pos().y() * terrainTileBody.tileHeight() +
                                       terrainTileBody.tileHeight() / 2),
                            BodyType.StaticBody,
                            terrainTileBody,
                            Rectangle.of(terrainTileBody.tileWidth(), terrainTileBody.tileHeight()),
                            false,
                            false,
                            null,
                            null);

    }

    public static Body createCreatureB2Body(PhysicsWorld world, CreatureBody creatureBody, Creature creature) {
        return createB2Body(world,
                            creature.params().pos(),
                            BodyType.DynamicBody,
                            creatureBody,
                            Circle.of(creature.animationConfig().spriteWidth() / 2f),
                            false,
                            false,
                            10f,
                            1000f);
    }

    public static Body createAbilityB2Body(PhysicsWorld world, AbilityBody abilityBody, Vector2 pos, float[] vertices) {
        return createB2Body(world,
                            pos,
                            BodyType.DynamicBody,
                            abilityBody,
                            Polygon.of(vertices),
                            true,
                            false,
                            null,
                            null);
    }

    public static Body createAreaGateB2body(PhysicsWorld world,
                                            AreaGateBody areaGateBody,
                                            Vector2 pos,
                                            float width,
                                            float height) {
        return createB2Body(world,
                            pos,
                            BodyType.StaticBody,
                            areaGateBody,
                            Rectangle.of(width, height),
                            true,
                            false,
                            null,
                            null);
    }

    public static Body createLootPileB2body(PhysicsWorld world,
                                            LootPileBody areaGateBody,
                                            Vector2 pos,
                                            float width,
                                            float height) {
        return createB2Body(world,
                            pos,
                            BodyType.StaticBody,
                            areaGateBody,
                            Rectangle.of(width, height),
                            true,
                            false,
                            null,
                            null);
    }


}




