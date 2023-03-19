package com.mygdx.game.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.physics.world.PhysicsWorld;
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

    public void init(MyGdxGame game) {
        LootPile lootPile = game.getLootPile(lootPileId);

        world = game.getPhysicsWorld(lootPile.areaId());

        b2Body =
                B2BodyFactory.createLootPileB2body(world,
                                                   this,
                                                   lootPile.pos(),
                                                   lootPile.width(),
                                                   lootPile.height());
    }

    public void onRemove() {
        world.b2world().destroyBody(b2Body);
    }
}
