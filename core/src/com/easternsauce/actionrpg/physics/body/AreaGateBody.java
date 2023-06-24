package com.easternsauce.actionrpg.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.area.AreaGateId;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class AreaGateBody {
    Body b2body;

    PhysicsWorld world;

    AreaGateId areaGateId;

    public static AreaGateBody of(AreaGateId areaGateId) {
        AreaGateBody areaGateBody = AreaGateBody.of();
        areaGateBody.areaGateId = areaGateId;

        return areaGateBody;
    }

    public void init(CoreGame game) {
        AreaGate areaGate = game.getGameState().getAreaGates().get(areaGateId);

        world = game.getPhysicsWorld(areaGate.getAreaId());

        b2body = B2BodyFactory.createAreaGateB2body(
            world,
            this,
            areaGate.getPos(),
            areaGate.getWidth(),
            areaGate.getHeight()
        );
    }

    public void onRemove() {
        world.getB2world().destroyBody(b2body);
    }
}
