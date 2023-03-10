package com.mygdx.game.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.physics.world.PhysicsWorld;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")

public class CreatureBody {
    CreatureId creatureId;

    Body b2Body;

    Boolean bodyCreated;

    PhysicsWorld world;

    Boolean isActive = true;

    public static CreatureBody of(CreatureId creatureId) {
        CreatureBody creatureBody = new CreatureBody();
        creatureBody.creatureId = creatureId;
        return creatureBody;
    }

    public CreatureId creatureId() {
        return creatureId;
    }

    public void init(GamePhysics gamePhysics, GameState gameState, AreaId areaId) {
        Creature creature = gameState.creatures().get(creatureId);

        world = gamePhysics.physicsWorlds().get(areaId);

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
        if (this.isActive != isActive) {
            this.isActive = isActive;
            b2Body.setActive(isActive);
        }
    }
}
