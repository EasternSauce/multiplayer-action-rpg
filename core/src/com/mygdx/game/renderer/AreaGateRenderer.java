package com.mygdx.game.renderer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.area.AreaGate;
import com.mygdx.game.model.area.AreaId;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class AreaGateRenderer {
    Sprite spriteA;
    AreaId areaA_Id;
    Sprite spriteB;
    AreaId areaB_Id;

    public static AreaGateRenderer of(AreaGate areaGate, TextureAtlas atlas) {

        AreaGateRenderer areaGateRenderer = AreaGateRenderer.of();

        areaGateRenderer.spriteA = new Sprite();
        areaGateRenderer.spriteA.setRegion(atlas.findRegion("downarrow"));
        areaGateRenderer.spriteA.setSize(areaGate.getWidth(), areaGate.getHeight());
        areaGateRenderer.spriteA.setCenter(areaGate.getPosA().getX(), areaGate.getPosA().getY());

        areaGateRenderer.spriteB = new Sprite();
        areaGateRenderer.spriteB.setRegion(atlas.findRegion("downarrow"));
        areaGateRenderer.spriteB.setSize(areaGate.getWidth(), areaGate.getHeight());
        areaGateRenderer.spriteB.setCenter(areaGate.getPosB().getX(), areaGate.getPosB().getY());

        areaGateRenderer.areaA_Id = areaGate.getAreaA_Id();
        areaGateRenderer.areaB_Id = areaGate.getAreaB_Id();

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
