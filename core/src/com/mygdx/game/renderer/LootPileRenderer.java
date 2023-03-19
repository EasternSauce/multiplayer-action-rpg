package com.mygdx.game.renderer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class LootPileRenderer {

    LootPileId lootPileId;
    Sprite sprite;

    public static LootPileRenderer of(LootPileId lootPileId) {
        LootPileRenderer lootPileRenderer = new LootPileRenderer();
        lootPileRenderer.lootPileId(lootPileId);
        return lootPileRenderer;
    }


    public void init(TextureAtlas atlas, GameState gameState) { // TODO: change to UpdatableGame?

        LootPile lootPile = gameState.lootPiles().get(lootPileId);

        sprite = new Sprite();
        sprite.setRegion(atlas.findRegion("bag"));
        sprite.setSize(lootPile.width(), lootPile.height());
        sprite.setCenter(lootPile.pos().x(), lootPile.pos().y());


    }

    public void render(DrawingLayer drawingLayer, GameRenderable game) {
        AreaId currentAreaId = game.getCurrentPlayerAreaId();
        LootPile lootPile = game.getLootPile(lootPileId);

        if (currentAreaId.equals(lootPile.areaId())) {
            sprite.draw(drawingLayer.spriteBatch());
        }

    }

}
