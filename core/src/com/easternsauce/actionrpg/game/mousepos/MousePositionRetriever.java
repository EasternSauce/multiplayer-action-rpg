package com.easternsauce.actionrpg.game.mousepos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class MousePositionRetriever {
    public Vector2 mousePosRelativeToCenter(CoreGame game) { // relative to center of screen, in in-game length units
        //noinspection SpellCheckingInspection
        Vector3 screenCoords = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f);
        game.getViewportsHandler().unprojectHudCamera(screenCoords);
        Vector2 mousePos = Vector2.of(screenCoords.x - Constants.WINDOW_WIDTH / 2f,
            screenCoords.y - Constants.WINDOW_HEIGHT / 2f
        );

        float viewportRatioX = Constants.VIEWPOINT_WORLD_WIDTH / Constants.WINDOW_WIDTH;
        float viewportRatioY = Constants.VIEWPOINT_WORLD_HEIGHT / Constants.WINDOW_HEIGHT;

        return Vector2.of(mousePos.getX() * viewportRatioX / Constants.PPM,
            mousePos.getY() * viewportRatioY / Constants.PPM
        );
    }

    public Vector2 hudMousePos(CoreGame game) {
        //noinspection SpellCheckingInspection
        Vector3 screenCoords = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f);
        game.getViewportsHandler().unprojectHudCamera(screenCoords);
        return Vector2.of(screenCoords.x, screenCoords.y);
    }
}
