package com.easternsauce.actionrpg.renderer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.area.AreaGateConnection;
import com.easternsauce.actionrpg.model.area.AreaGateId;
import com.easternsauce.actionrpg.model.area.AreaId;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class AreaGateRenderer {
    AreaGateId areaGateId;

    Sprite sprite;

    public static AreaGateRenderer of(AreaGateId areaGateId) {

        AreaGateRenderer areaGateRenderer = AreaGateRenderer.of();
        areaGateRenderer.areaGateId = areaGateId;
        return areaGateRenderer;

    }

    public void init(TextureAtlas atlas, CoreGame game) {
        sprite = new Sprite();
        sprite.setRegion(atlas.findRegion("downarrow"));

        AreaGate areaGate = game.getGameState().getAreaGate(areaGateId);

        sprite.setSize(areaGate.getWidth(), areaGate.getHeight());
        sprite.setCenter(areaGate.getPos().getX(), areaGate.getPos().getY());

    }

    public void render(RenderingLayer renderingLayer, CoreGame game) {
        AreaId currentAreaId = game.getGameState().getCurrentAreaId();

        AreaGate areaGate = game.getGameState().getAreaGate(areaGateId);

        if (currentAreaId.equals(areaGate.getAreaId())) {
            sprite.draw(renderingLayer.getSpriteBatch());
        }
    }
}
