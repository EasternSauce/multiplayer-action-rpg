package com.easternsauce.actionrpg.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullAreaId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class CreatureBody {
  private EntityId<Creature> creatureId = NullCreatureId.of();

  private Body b2body;

  private Boolean bodyCreated = false;

  private PhysicsWorld world;

  @Getter
  private EntityId<Area> areaId = NullAreaId.of();

  private Boolean isActive = true;

  public static CreatureBody of(EntityId<Creature> creatureId) {
    CreatureBody creatureBody = new CreatureBody();
    creatureBody.creatureId = creatureId;
    return creatureBody;
  }

  public void update(CoreGame game) {
    if (!game.getActiveCreatures().containsKey(creatureId)) {
      return;
    }

    Creature creature = game.getCreature(creatureId);

    setSensor(!creature.isAlive());

    float v = creature.getParams().getStats().getSpeed();

    if (!creatureId.getValue().startsWith("Enemy")) {
      System.out.println("println");
    }

    if (bodyCreated) {
      if (!creatureId.getValue().startsWith("Enemy")) System.out.println("body was created!");
      if (!creature.getParams().getMovementParams().getDashing()) {
        if (creature.getParams().getMovementParams().getMoving()) {
          if (!creatureId.getValue().startsWith("Enemy")) System.out.println("is moving!");

          Vector2 normalizedMovingVector;

          if (creature.isEffectActive(CreatureEffect.SLOW, game) ||
            creature.isEffectActive(CreatureEffect.SELF_SLOW, game)) {
            normalizedMovingVector = creature.getParams().getMovementParams().getMovingVector().normalized()
              .multiplyBy(1f - creature.getParams().getEffectParams().getCurrentSlowMagnitude());
          } else {
            normalizedMovingVector = creature.getParams().getMovementParams().getMovingVector().normalized();
          }

          if (!creature.isStunned(game) && normalizedMovingVector.len() > 0f) {
            creature.getParams().getMovementParams().setFacingVector(normalizedMovingVector.copy());
          }

          float vectorX = normalizedMovingVector.getX() * v;
          float vectorY = normalizedMovingVector.getY() * v;

          setVelocity(Vector2.of(vectorX, vectorY));
        } else {
          setVelocity(Vector2.of(0, 0));
        }
      } else {
        Vector2 normalizedDashingVector = creature.getParams().getMovementParams().getDashingVector().normalized();

        float vectorX = normalizedDashingVector.getX() * creature.getParams().getMovementParams().getDashingVelocity();
        float vectorY = normalizedDashingVector.getY() * creature.getParams().getMovementParams().getDashingVelocity();

        setVelocity(Vector2.of(vectorX, vectorY));
      }
    }
  }

  public void setSensor(boolean sensor) {
    b2body.getFixtureList().get(0).setSensor(sensor);
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

  public void setActive(boolean isActive) {
    if (this.isActive != isActive) {
      this.isActive = isActive;
      b2body.setActive(isActive);
    }
  }

  public EntityId<Creature> getCreatureId() {
    return creatureId;
  }

  public void moveBodyToNewArea(EntityId<Area> areaId, CoreGame game) {
    onRemove();
    init(areaId, game);
  }

  public void onRemove() {
    world.getB2world().destroyBody(b2body);
  }

  public void init(EntityId<Area> areaId, CoreGame game) {
    System.out.println("init " + creatureId.getValue());
    Creature creature = game.getCreature(creatureId);

    this.world = game.getPhysicsWorld(areaId);

    this.areaId = areaId;

    this.b2body = B2BodyFactory.createCreatureB2Body(world, this, creature);

    if (!creature.isAlive()) {
      this.b2body.getFixtureList().get(0).setSensor(true);
    }

    this.bodyCreated = true;
  }
}
