package com.mygdx.game.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.game.data.AreaGate;
import com.mygdx.game.game.interface_.GameUpdatable;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class AreaGateBody {

    AreaGate areaGate;
    @SuppressWarnings("FieldCanBeLocal")
    Body areaGateA_b2body;
    @SuppressWarnings("FieldCanBeLocal")
    Body areaGateB_b2body;

    public static AreaGateBody of(AreaGate areaGate) {
        AreaGateBody areaGateBody = AreaGateBody.of();
        areaGateBody.areaGate = areaGate;
        return areaGateBody;
    }

    public void init(GameUpdatable game) {
        areaGateA_b2body =
                B2BodyFactory.createAreaGateB2body(game.getPhysicsWorld(areaGate.areaA_Id()),
                                                   this,
                                                   areaGate.posA(),
                                                   areaGate.width(),
                                                   areaGate.height());
        areaGateB_b2body =
                B2BodyFactory.createAreaGateB2body(game.getPhysicsWorld(areaGate.areaB_Id()),
                                                   this,
                                                   areaGate.posB(),
                                                   areaGate.width(),
                                                   areaGate.height());
    }
}
