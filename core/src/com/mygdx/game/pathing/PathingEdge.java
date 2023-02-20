package com.mygdx.game.pathing;

import com.mygdx.game.physics.util.TilePos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class PathingEdge {
    Float weight;
    TilePos neighborPos;
}
