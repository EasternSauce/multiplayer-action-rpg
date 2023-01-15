package com.mygdx.game.physics;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class B2BodyFactory {
    public static Body createB2Body(PhysicsWorld world, Vector2 pos, BodyType bodyType, Object userData,
                                    BodyShape shape,
                                    Boolean isSensor, Boolean sleepingAllowed, Float linearDamping, Float mass) {
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

        return b2body;
    }


    public static Body createTerrainTileB2body(PhysicsWorld world, TerrainTileBody terrainTileBody) {
        return createB2Body(world,
                Vector2.of(terrainTileBody.pos().x() * terrainTileBody.tileWidth() + terrainTileBody.tileWidth() / 2,
                        terrainTileBody.pos().y() * terrainTileBody.tileHeight() + terrainTileBody.tileHeight() / 2),
                BodyType.StaticBody,
                terrainTileBody, Rectangle.of(terrainTileBody.tileWidth(), terrainTileBody.tileHeight()), false, null,
                null, null);

    }

    public static Body createCreatureB2Body(PhysicsWorld world, CreatureBody creatureBody, Creature creature) {
        return createB2Body(world, creature.params().pos(), BodyType.DynamicBody, creatureBody,
                Circle.of(creature.params().animationConfig().spriteWidth() / 2f), false, false, 10f, 1000f);
    }
}

interface BodyShape {
    Shape b2Shape();
}

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
class Circle implements BodyShape {
    float radius;

    @Override
    public Shape b2Shape() {
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        return shape;
    }
}

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
class Rectangle implements BodyShape {
    float width;
    float height;

    @Override
    public Shape b2Shape() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);
        return shape;
    }
}

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
class Polygon implements BodyShape {
    float[] vertices;

    @Override
    public Shape b2Shape() {
        PolygonShape shape = new PolygonShape();
        shape.set(vertices);
        return shape;
    }
}