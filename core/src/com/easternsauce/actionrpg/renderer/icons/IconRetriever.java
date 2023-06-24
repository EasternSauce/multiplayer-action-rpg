package com.easternsauce.actionrpg.renderer.icons;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class IconRetriever {
    private TextureRegion[][] icons;

    public void init(TextureAtlas atlas) {
        icons = atlas.findRegion("nice_icons").split(
            32,
            32
        );
    }

    public TextureRegion getIcon(int x, int y) {
        return icons[y][x];
    }
}
