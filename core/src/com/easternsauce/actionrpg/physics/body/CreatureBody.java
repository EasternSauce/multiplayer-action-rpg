package com.easternsauce.actionrpg.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class CreatureBody {
    private CreatureId creatureId;

    private Body b2body;

    private Boolean bodyCreated;

    private PhysicsWorld world;

    @Getter
    private AreaId areaId;

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

        this.world = game.getPhysicsWorld(areaId);

        this.areaId = areaId;

        this.b2body = B2BodyFactory.createCreatureB2Body(world, this, creature);

        if (!creature.isAlive()) {
            this.b2body.getFixtureList().get(0).setSensor(true);
        }

        this.bodyCreated = true;
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

        if (bodyCreated) {
            if (!creature.getParams().getIsDashing()) {
                if (creature.getParams().getIsMoving()) {
                    Vector2 normalizedMovingVector;

                    if (creature.isEffectActive(CreatureEffect.SLOW, game) ||
                        creature.isEffectActive(CreatureEffect.SELF_SLOW, game)) {
                        normalizedMovingVector = creature
                            .getParams()
                            .getMovingVector()
                            .normalized()
                            .multiplyBy(1f - creature.getParams().getCurrentSlowMagnitude());
                    }
                    else {
                        normalizedMovingVector = creature.getParams().getMovingVector().normalized();
                    }

                    if (!creature.isStunned(game) && normalizedMovingVector.len() > 0f) {
                        creature.getParams().setFacingVector(normalizedMovingVector.copy());
                    }

                    float vectorX = normalizedMovingVector.getX() * v;
                    float vectorY = normalizedMovingVector.getY() * v;

                    setVelocity(Vector2.of(vectorX, vectorY));
                }
                else {
                    setVelocity(Vector2.of(0, 0));
                }
            }
            else {
                Vector2 normalizedDashingVector = creature.getParams().getDashingVector().normalized();

                float vectorX = normalizedDashingVector.getX() * creature.getParams().getDashingVelocity();
                float vectorY = normalizedDashingVector.getY() * creature.getParams().getDashingVelocity();

                setVelocity(Vector2.of(vectorX, vectorY));
            }
        }
    }

    public void setVelocity(Vector2 velocity) {
        b2body.setLinearVelocity(new com.badlogic.gdx.math.Vector2(velocity.getX(), velocity.getY()));
    }

    public Vector2 getBodyPos() {
        return Vector2.of(b2body.getWorldCenter().x, b2body.getWorldCenter().y);
    }

    public void trySetTransform(Vector2 vector) {
        if (!world.getB2world().isLocked()) {
            forceSetTransform(vector);
        }

    }

    public void forceSetTransform(Vector2 vector) {
        b2body.setTransform(vector.getX(), vector.getY(), b2body.getAngle());
    }

    public void onRemove() {
        world.getB2world().destroyBody(b2body);
    }

    public void setSensor(boolean sensor) {
        b2body.getFixtureList().get(0).setSensor(sensor);
    }

    public void setActive(boolean isActive) {
        if (this.isActive != isActive) {
            this.isActive = isActive;
            b2body.setActive(isActive);
        }
    }

    public CreatureId getCreatureId() {
        return creatureId;
    }

    public void moveBodyToNewArea(AreaId areaId, CoreGame game) {
        onRemove();
        init(areaId, game);
    }
}
