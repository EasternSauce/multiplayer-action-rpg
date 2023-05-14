package com.easternsauce.actionrpg.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class AreaGateBody {
    @SuppressWarnings("FieldCanBeLocal")
    Body b2body;

    PhysicsWorld world;

    Vector2 pos;
    Float width;
    Float height;
    AreaId areaId;

    Vector2 connectedPos;
    Float connectedWidth;
    Float connectedHeight;
    AreaId connectedAreaId;

    public static AreaGateBody of(Vector2 pos, Float width, Float height, AreaId areaId, Vector2 connectedPos,
                                  Float connectedWidth, Float connectedHeight, AreaId connectedAreaId) {
        AreaGateBody areaGateBody = AreaGateBody.of();
        areaGateBody.pos = pos;
        areaGateBody.width = width;
        areaGateBody.height = height;
        areaGateBody.areaId = areaId;
        areaGateBody.connectedPos = connectedPos;
        areaGateBody.connectedWidth = connectedWidth;
        areaGateBody.connectedHeight = connectedHeight;
        areaGateBody.connectedAreaId = connectedAreaId;

        return areaGateBody;
    }

    public void init(CoreGame game) {
        world = game.getPhysicsWorld(areaId);

        b2body = B2BodyFactory.createAreaGateB2body(world, this, pos, width, height);
    }
}
