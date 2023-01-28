package com.mygdx.game.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.mygdx.game.renderer.DrawingLayer;
import com.mygdx.game.util.Vector2;

public class Assets {

    static String youngSerifFontPath = "assets/font/Helvetica 400.ttf";

    static BitmapFont defaultFont;
    static BitmapFont largeFont;

    static {
        defaultFont = loadFont(Assets.youngSerifFontPath, 16);
        largeFont = loadFont(Assets.youngSerifFontPath, 64);
    }

    public static BitmapFont loadFont(String fontPath, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);
        font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

        generator.dispose();
        return font;
    }

    public static void drawFont(DrawingLayer drawingLayer, String text, Vector2 pos, Color color) {
        defaultFont.setColor(color);
        defaultFont.draw(drawingLayer.spriteBatch(), text, pos.x(), pos.y());
    }

    public static void drawLargeFont(DrawingLayer drawingLayer, String text, Vector2 pos, Color color) {
        largeFont.setColor(color);
        largeFont.draw(drawingLayer.spriteBatch(), text, pos.x(), pos.y());
    }
}
