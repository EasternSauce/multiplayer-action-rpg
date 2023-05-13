package com.easternsauce.actionrpg.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.area.LootPileId;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class LootPileBody {
    LootPileId lootPileId;
    @SuppressWarnings("FieldCanBeLocal")
    Body b2Body;

    PhysicsWorld world;

    public static LootPileBody of(LootPileId lootPileId) {
        LootPileBody lootPileBody = LootPileBody.of();
        lootPileBody.lootPileId = lootPileId;
        return lootPileBody;
    }

    public void init(CoreGame game) {
        LootPile lootPile = game.getGameState().getLootPile(lootPileId);

        world = game.getPhysicsWorld(lootPile.getAreaId());

        b2Body = B2BodyFactory.createLootPileB2body(world, this, lootPile.getPos(), lootPile.getWidth(), lootPile.getHeight());
    }

    public void onRemove() {
        world.getB2world().destroyBody(b2Body);
    }
}