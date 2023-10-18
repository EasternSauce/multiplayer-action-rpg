package com.easternsauce.actionrpg.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class CheckpointBody {
  private Body b2body;

  private PhysicsWorld world;

  @Getter
  private EntityId<Checkpoint> checkpointId;

  public static CheckpointBody of(EntityId<Checkpoint> checkpointId) {
    CheckpointBody checkpointBody = CheckpointBody.of();
    checkpointBody.checkpointId = checkpointId;

    return checkpointBody;
  }

  public void init(CoreGame game) {
    Checkpoint checkpoint = game.getGameState().getCheckpoints().get(checkpointId);

    EntityId<Area> areaId = checkpoint.getAreaId();
    world = game.getEntityManager().getGameEntityPhysics().getPhysicsWorlds().get(areaId);

    b2body = B2BodyFactory.createCheckpointB2body(world, this, checkpoint.getPos(), checkpoint.getWidth(),
      checkpoint.getHeight());
  }

  public void onRemove() {
    world.getB2world().destroyBody(b2body);
  }
}
