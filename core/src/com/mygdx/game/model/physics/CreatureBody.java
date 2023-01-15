package com.mygdx.game.model.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.game.GameState;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureBody {
    CreatureId creatureId;

    Body b2Body;

    Boolean bodyCreated;

    PhysicsWorld world;

    public void init(GamePhysics gamePhysics, GameState gameState) { // TODO: get world by area id
        Creature creature = gameState.creatures().get(creatureId);

        world = gamePhysics.physicsWorlds().get(creature.params().areaId());

        b2Body = B2BodyFactory.createCreatureB2Body(world, this, creature);

        if (!creature.isAlive()) b2Body.getFixtureList().get(0).setSensor(true);

        bodyCreated = true;
    }

    public void update(GameState gameState) {
        Creature creature = gameState.creatures().get(creatureId);

        float v = creature.params().speed();

        Vector2 normalMovingVector = creature.params().movingVector();

        float vectorX = normalMovingVector.x() * v;
        float vectorY = normalMovingVector.y() * v;

        if (bodyCreated) {
            //TODO: knockback
            if (creature.params().isMoving()) {
                setVelocity(Vector2.of(vectorX, vectorY));
            } else {
                setVelocity(Vector2.of(0, 0));
            }

        }

    }

    public void setVelocity(Vector2 velocity) {
        b2Body.setLinearVelocity(new com.badlogic.gdx.math.Vector2(velocity.x(), velocity.y()));
    }

    public Vector2 pos() {
        return Vector2.of(b2Body.getWorldCenter().x, b2Body.getWorldCenter().y);
    }

    public void pos(Vector2 vector) {
        b2Body.setTransform(vector.x(), vector.y(), b2Body.getAngle());
    }

    public void onRemove() {
        world.b2world().destroyBody(b2Body);
    }

    public static CreatureBody of(CreatureId creatureId) {
        CreatureBody creatureBody = new CreatureBody();
        creatureBody.creatureId = creatureId;
        return creatureBody;
    }


}
