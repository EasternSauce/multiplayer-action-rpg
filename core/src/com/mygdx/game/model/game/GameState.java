package com.mygdx.game.model.game;

import com.mygdx.game.model.area.Area;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class GameState {
    @Builder.Default
    @NonNull
    private Map<CreatureId, Creature> creatures = new HashMap<>();
    @Builder.Default
    @NonNull
    private Map<AreaId, Area> areas = new HashMap<>();
    private AreaId currentAreaId;
    @NonNull
    private AreaId defaultAreaId;
}
