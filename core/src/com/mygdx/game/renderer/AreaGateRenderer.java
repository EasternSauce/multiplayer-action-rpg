package com.mygdx.game.renderer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.game.data.AreaGate;
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
        areaGateRenderer.spriteA.setCenter(areaGate.posA().x(), areaGate.posA().y());
        areaGateRenderer.spriteA.setSize(areaGate.width(), areaGate.height());

        areaGateRenderer.spriteB = new Sprite();
        areaGateRenderer.spriteB.setRegion(atlas.findRegion("downarrow"));
        areaGateRenderer.spriteB.setCenter(areaGate.posB().x(), areaGate.posB().y());
        areaGateRenderer.spriteB.setSize(areaGate.width(), areaGate.height());

        areaGateRenderer.areaA_Id = areaGate.areaA_Id();
        areaGateRenderer.areaB_Id = areaGate.areaB_Id();

        return areaGateRenderer;

    }

    public void render(DrawingLayer drawingLayer, MyGdxGame game) {
        AreaId currentAreaId = game.currentPlayerAreaId();

        if (currentAreaId.equals(areaA_Id)) {
            spriteA.draw(drawingLayer.spriteBatch());
        }
        if (currentAreaId.equals(areaB_Id)) {
            spriteB.draw(drawingLayer.spriteBatch());
        }
    }
}
