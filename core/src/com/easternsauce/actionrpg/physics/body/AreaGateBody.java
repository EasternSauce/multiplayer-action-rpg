package com.easternsauce.actionrpg.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.area.AreaGateId;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class AreaGateBody {
    private Body b2body;

    private PhysicsWorld world;

    @Getter
    private AreaGateId areaGateId;

    public static AreaGateBody of(AreaGateId areaGateId) {
        AreaGateBody areaGateBody = AreaGateBody.of();
        areaGateBody.areaGateId = areaGateId;

        return areaGateBody;
    }

    public void init(CoreGame game) {
        AreaGate areaGate = game.getGameState().getAreaGates().get(areaGateId);

        AreaId areaId = areaGate.getAreaId();
        world = game.getEntityManager().getGameEntityPhysics().getPhysicsWorlds().get(areaId);

        b2body = B2BodyFactory.createAreaGateB2body(world,
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
