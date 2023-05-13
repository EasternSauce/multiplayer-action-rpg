package com.easternsauce.actionrpg.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
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

    PhysicsWorld worldA;
    PhysicsWorld worldB;

    public static AreaGateBody of(AreaGate areaGate) {
        AreaGateBody areaGateBody = AreaGateBody.of();
        areaGateBody.areaGate = areaGate;
        return areaGateBody;
    }

    public void init(CoreGame game) {
        worldA = game.getPhysicsWorld(areaGate.getAreaA_Id());
        worldB = game.getPhysicsWorld(areaGate.getAreaB_Id());

        areaGateA_b2body = B2BodyFactory.createAreaGateB2body(worldA,
                                                              this,
                                                              areaGate.getPosA(),
                                                              areaGate.getWidth(),
                                                              areaGate.getHeight());
        areaGateB_b2body = B2BodyFactory.createAreaGateB2body(worldB,
                                                              this,
                                                              areaGate.getPosB(),
                                                              areaGate.getWidth(),
                                                              areaGate.getHeight());
    }
}
