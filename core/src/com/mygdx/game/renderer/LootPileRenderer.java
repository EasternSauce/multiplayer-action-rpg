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
        lootPileRenderer.setLootPileId(lootPileId);
        return lootPileRenderer;
    }


    public void init(TextureAtlas atlas, GameState gameState) { // TODO: change to UpdatableGame?

        LootPile lootPile = gameState.getLootPiles().get(lootPileId);

        sprite = new Sprite();
        sprite.setRegion(atlas.findRegion("bag"));
        sprite.setSize(lootPile.getWidth(), lootPile.getHeight());
        sprite.setCenter(lootPile.getPos().getX(), lootPile.getPos().getY());


    }

    public void render(RenderingLayer renderingLayer, GameRenderable game) {
        AreaId currentAreaId = game.getCurrentPlayerAreaId();
        LootPile lootPile = game.getLootPile(lootPileId);

        if (lootPile != null && currentAreaId.equals(lootPile.getAreaId())) {
            sprite.draw(renderingLayer.getSpriteBatch());
        }

    }

}
