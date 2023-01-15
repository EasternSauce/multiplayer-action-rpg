package com.mygdx.game.physics;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class GamePhysics {

    Map<AreaId, PhysicsWorld> physicsWorlds;

    Map<CreatureId, CreatureBody> creatureBodies;

    Box2DDebugRenderer debugRenderer;

    public void init(Map<AreaId, TiledMap> maps, GameState gameState) {
        physicsWorlds = maps.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> PhysicsWorld.of(entry.getValue())));

        physicsWorlds.forEach((areaId, physicsWorld) -> {
            physicsWorld.init();
            // TODO: create contact listener...

        });
        // TODO: init bodies existing in gamestate...
    }
}
