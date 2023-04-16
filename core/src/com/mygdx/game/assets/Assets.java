package com.mygdx.game.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.DrawingLayer;

public class Assets {

    final static String youngSerifFontPath = "assets/font/Helvetica 400.ttf";

    final static BitmapFont smallFont;

    final static BitmapFont verySmallFont;

    final static BitmapFont mediumFont;
    final static BitmapFont largeFont;

    static {
        smallFont = loadFont(Assets.youngSerifFontPath, 16);
        verySmallFont = loadFont(Assets.youngSerifFontPath, 12);
        mediumFont = loadFont(Assets.youngSerifFontPath, 20);
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

    public static void drawSmallFont(DrawingLayer drawingLayer, String text, Vector2 pos, Color color) {
        smallFont.setColor(color);
        smallFont.draw(drawingLayer.spriteBatch(), text, pos.x(), pos.y());
    }

    public static void drawVerySmallFont(DrawingLayer drawingLayer, String text, Vector2 pos, Color color) {
        verySmallFont.setColor(color);
        verySmallFont.draw(drawingLayer.spriteBatch(), text, pos.x(), pos.y());
    }

    public static void drawMediumFont(DrawingLayer drawingLayer, String text, Vector2 pos, Color color) {
        mediumFont.setColor(color);
        mediumFont.draw(drawingLayer.spriteBatch(), text, pos.x(), pos.y());
    }

    public static void drawLargeFont(DrawingLayer drawingLayer, String text, Vector2 pos, Color color) {
        largeFont.setColor(color);
        largeFont.draw(drawingLayer.spriteBatch(), text, pos.x(), pos.y());
    }
}
