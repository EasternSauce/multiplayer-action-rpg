package com.easternsauce.actionrpg.game.client;

import com.esotericsoftware.kryonet.Client;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor(staticName = "of")
public class ClientConnectionEstablisher {
    public void establish(CoreGameClientListener listener, CoreGameClient game) {
        Client endPoint = new Client(6400000, 6400000);
        endPoint.getKryo().setRegistrationRequired(false);
        endPoint.start();
        try {
            endPoint.connect(12000 * 99999, "192.168.1.5", 20445, 20445);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        endPoint.addListener(listener);

        game.setEndPoint(endPoint);
    }
}
