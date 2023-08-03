package com.easternsauce.actionrpg.game.server;

import com.esotericsoftware.kryonet.Server;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor(staticName = "of")
public class ServerConnectionEstablisher {
    public void establish(CoreGameServerListener listener, CoreGameServer game) {
        Server endPoint = new Server(12800000, 12800000);
        endPoint.getKryo().setRegistrationRequired(false);
        endPoint.start();

        try {
            endPoint.bind(20445, 20445);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        endPoint.addListener(listener);

        game.setEndPoint(endPoint);
    }
}
