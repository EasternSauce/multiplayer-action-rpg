package com.easternsauce.actionrpg.renderer.physics;

import com.easternsauce.actionrpg.game.CoreGame;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class PhysicsDebugRenderer {
  public void render(CoreGame game) {
    if (game.isDebugEnabled()) {
      game.getEntityManager().getGameEntityPhysics().getDebugRenderer().render(
        game.getEntityManager().getGameEntityPhysics().getPhysicsWorlds().get(game.getCurrentAreaId()).getB2world(),
        game.getViewportsHandler().getWorldCameraCombinedProjectionMatrix());
    }
  }
}
