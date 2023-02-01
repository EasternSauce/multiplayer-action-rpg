package com.mygdx.game.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")

public class CreatureBody {
    CreatureId creatureId;

    Body b2Body;

    Boolean bodyCreated;

    PhysicsWorld world;

    public static CreatureBody of(CreatureId creatureId) {
        CreatureBody creatureBody = new CreatureBody();
        creatureBody.creatureId = creatureId;
        return creatureBody;
    }

    public CreatureId creatureId() {
        return creatureId;
    }

    public void init(GamePhysics gamePhysics, GameState gameState) { // TODO: get world by area id
        Creature creature = gameState.creatures().get(creatureId);

        world = gamePhysics.physicsWorlds().get(creature.params().areaId());

        //        while (world.b2world().isLocked()) {
        //            try {
        //                Thread.sleep(10);
        //            } catch (InterruptedException e) {
        //                throw new RuntimeException(e);
        //            }
        //        }
        b2Body = B2BodyFactory.createCreatureB2Body(world, this, creature);

        if (!creature.isAlive()) {
            b2Body.getFixtureList().get(0).setSensor(true);
        }

        bodyCreated = true;
    }

    public void update(GameState gameState) {
        if (!gameState.creatures().containsKey(creatureId)) {
            return;
        }

        Creature creature = gameState.creatures().get(creatureId);

        setSensor(!creature.isAlive());

        float v = creature.params().speed();

        Vector2 normalMovingVector = creature.params().movingVector();

        float vectorX = normalMovingVector.x() * v;
        float vectorY = normalMovingVector.y() * v;

        if (bodyCreated) {
            //TODO: knockback
            if (creature.params().isMoving()) {
                setVelocity(Vector2.of(vectorX, vectorY));
            }
            else {
                setVelocity(Vector2.of(0, 0));
            }

        }

    }

    public void setVelocity(Vector2 velocity) {
        b2Body.setLinearVelocity(new com.badlogic.gdx.math.Vector2(velocity.x(), velocity.y()));
    }

    public Vector2 getBodyPos() {
        return Vector2.of(b2Body.getWorldCenter().x, b2Body.getWorldCenter().y);
    }

    public void trySetTransform(Vector2 vector) {
        if (!world.b2world().isLocked()) {
            forceSetTransform(vector);
        }

    }


    public void forceSetTransform(Vector2 vector) {
        b2Body.setTransform(vector.x(), vector.y(), b2Body.getAngle());
    }

    public void onRemove() {
        world.b2world().destroyBody(b2Body);
    }


    public void setSensor(boolean sensor) {
        b2Body.getFixtureList().get(0).setSensor(sensor);
    }

    public void setActive(boolean isActive) {
        b2Body.setActive(isActive);
    }
}
