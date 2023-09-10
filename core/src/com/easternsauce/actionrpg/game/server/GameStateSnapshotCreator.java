package com.easternsauce.actionrpg.game.server;

import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@NoArgsConstructor(staticName = "of")
public class GameStateSnapshotCreator {
  private Thread snapshotCreatorThread;

  public void start(CoreGameServer game) {
    snapshotCreatorThread = createSnapshotCreatorThread(game);
    snapshotCreatorThread.start();
  }

  @SuppressWarnings("BusyWait")
  private Thread createSnapshotCreatorThread(CoreGameServer game) {
    return new Thread(() -> {
      try {
        while (true) {
          Thread.sleep((int) (Constants.TIME_BETWEEN_GAMESTATE_SNAPSHOTS * 1000f));

          String fileName = "./gamestate.json";
          game.getGameState().saveToJsonFile(fileName + ".temp");

          Path source = Paths.get(fileName + ".temp");
          Path dest = Paths.get(fileName);
          Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);

          Files.deleteIfExists(source);
        }
      } catch (InterruptedException e) {
        // do nothing
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public void stop() {
    snapshotCreatorThread.interrupt();
  }
}
