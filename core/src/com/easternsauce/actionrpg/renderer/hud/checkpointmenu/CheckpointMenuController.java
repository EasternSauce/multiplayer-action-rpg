package com.easternsauce.actionrpg.renderer.hud.checkpointmenu;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.command.ActionPerformCommand;
import com.easternsauce.actionrpg.model.action.CheckpointSetAction;
import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.renderer.CheckpointRenderer;
import com.easternsauce.actionrpg.renderer.util.Rect;
import com.esotericsoftware.kryonet.Client;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@NoArgsConstructor(staticName = "of")
public class CheckpointMenuController {
  public boolean performItemPickupMenuClick(Client client, CoreGame game) {
    PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

    float x = game.hudMousePos().getX();
    float y = game.hudMousePos().getY();

    AtomicBoolean isSuccessful = new AtomicBoolean(false);

    playerConfig.getCheckpointMenuCheckpoints().stream()
      .filter(checkpointId -> game.getGameState().getCheckpoints().containsKey(checkpointId))
      .findAny().ifPresent(checkpointId -> {
        Rect rect = Rect.of(CheckpointMenuConsts.POS_X, CheckpointMenuConsts.POS_Y, CheckpointMenuConsts.WIDTH,
          CheckpointMenuConsts.HEIGHT);

        if (rect.contains(x, y)) {
          isSuccessful.set(true);
          Map<EntityId<Checkpoint>, CheckpointRenderer> checkpointRenderers = game.getEntityManager().getGameEntityRenderer()
            .getCheckpointRenderers();
          if (checkpointRenderers.containsKey(checkpointId)) {
            checkpointRenderers.get(checkpointId).setLastCheckpointSetTime(game.getGameState().getTime());
          }

          client.sendTCP(
            ActionPerformCommand.of(CheckpointSetAction.of(game.getGameState().getThisClientPlayerId(), checkpointId)));
        }
      });

    return isSuccessful.get();
  }
}
