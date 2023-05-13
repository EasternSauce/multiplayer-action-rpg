package com.easternsauce.actionrpg.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class CreatureBody {
    private CreatureId creatureId;

    private Body b2Body;

    private Boolean bodyCreated;

    private PhysicsWorld world;

    private Boolean isActive = true;

    public static CreatureBody of(CreatureId creatureId) {
        CreatureBody creatureBody = new CreatureBody();
        creatureBody.creatureId = creatureId;
        return creatureBody;
    }

    public CreatureId creatureId() {
        return creatureId;
    }

    public void init(AreaId areaId, CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        world = game.getPhysicsWorld(areaId);

        b2Body = B2BodyFactory.createCreatureB2Body(world, this, creature);

        if (!creature.isAlive()) {
            b2Body.getFixtureList().get(0).setSensor(true);
        }

        bodyCreated = true;
    }

    public void update(CoreGame game) {
        if (!game.getGameState().accessCreatures().getCreatures().containsKey(creatureId)) {
            return;
        }

        Creature creature = game.getGameState().accessCreatures().getCreatures().get(creatureId);

        if (creature == null) {
            return;
        }

        setSensor(!creature.isAlive());

        float v = creature.getParams().getSpeed();

        Vector2 normalMovingVector = creature.getParams().getMovingVector();

        float vectorX = normalMovingVector.getX() * v;
        float vectorY = normalMovingVector.getY() * v;

        if (bodyCreated) {
            if (creature.getParams().getIsMoving()) {
                setVelocity(Vector2.of(vectorX, vectorY));
            }
            else {
                setVelocity(Vector2.of(0, 0));
            }

        }

    }

    public void setVelocity(Vector2 velocity) {
        b2Body.setLinearVelocity(new com.badlogic.gdx.math.Vector2(velocity.getX(), velocity.getY()));
    }

    public Vector2 getBodyPos() {
        return Vector2.of(b2Body.getWorldCenter().x, b2Body.getWorldCenter().y);
    }

    public void trySetTransform(Vector2 vector) {
        if (!world.getB2world().isLocked()) {
            forceSetTransform(vector);
        }

    }

    public void forceSetTransform(Vector2 vector) {
        b2Body.setTransform(vector.getX(), vector.getY(), b2Body.getAngle());
    }

    public void onRemove() {
        world.getB2world().destroyBody(b2Body);
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

    public CreatureId getCreatureId() {
        return creatureId;
    }
}