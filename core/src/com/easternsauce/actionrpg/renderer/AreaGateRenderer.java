package com.easternsauce.actionrpg.renderer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaGateConnection;
import com.easternsauce.actionrpg.model.area.AreaId;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class AreaGateRenderer {
    Sprite spriteA;
    AreaId areaA_Id;
    Sprite spriteB;
    AreaId areaB_Id;

    public static AreaGateRenderer of(AreaGateConnection areaGateConnection, TextureAtlas atlas) {

        AreaGateRenderer areaGateRenderer = AreaGateRenderer.of();

        areaGateRenderer.spriteA = new Sprite();
        areaGateRenderer.spriteA.setRegion(atlas.findRegion("downarrow"));
        areaGateRenderer.spriteA.setSize(areaGateConnection.getWidth(), areaGateConnection.getHeight());
        areaGateRenderer.spriteA.setCenter(areaGateConnection.getPosA().getX(), areaGateConnection.getPosA().getY());

        areaGateRenderer.spriteB = new Sprite();
        areaGateRenderer.spriteB.setRegion(atlas.findRegion("downarrow"));
        areaGateRenderer.spriteB.setSize(areaGateConnection.getWidth(), areaGateConnection.getHeight());
        areaGateRenderer.spriteB.setCenter(areaGateConnection.getPosB().getX(), areaGateConnection.getPosB().getY());

        areaGateRenderer.areaA_Id = areaGateConnection.getAreaA_Id();
        areaGateRenderer.areaB_Id = areaGateConnection.getAreaB_Id();

        return areaGateRenderer;

    }

    public void render(RenderingLayer renderingLayer, CoreGame game) {
        AreaId currentAreaId = game.getGameState().getCurrentAreaId();

        if (currentAreaId.equals(areaA_Id)) {
            spriteA.draw(renderingLayer.getSpriteBatch());
        }
        if (currentAreaId.equals(areaB_Id)) {
            spriteB.draw(renderingLayer.getSpriteBatch());
        }
    }
}
