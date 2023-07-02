package com.easternsauce.actionrpg.game.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.RenderingLayer;

public class Assets { // TODO: make separate font class

    final static String youngSerifFontPath = "assets/font/Helvetica 400.ttf";

    final static BitmapFont smallFont;

    final static BitmapFont verySmallFont;

    final static BitmapFont mediumFont;
    final static BitmapFont largeFont;
    final static BitmapFont veryLargeFont;

    static {
        smallFont = loadFont(Assets.youngSerifFontPath, 16);
        verySmallFont = loadFont(Assets.youngSerifFontPath, 12);
        mediumFont = loadFont(Assets.youngSerifFontPath, 20);
        largeFont = loadFont(Assets.youngSerifFontPath, 32);
        veryLargeFont = loadFont(Assets.youngSerifFontPath, 64);
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

    public static void renderSmallFont(RenderingLayer renderingLayer, String text, Vector2 pos, Color color) {
        smallFont.setColor(color);
        smallFont.draw(renderingLayer.getSpriteBatch(), text, pos.getX(), pos.getY());
    }

    public static void renderVerySmallFont(RenderingLayer renderingLayer, String text, Vector2 pos, Color color) {
        verySmallFont.setColor(color);
        verySmallFont.draw(renderingLayer.getSpriteBatch(), text, pos.getX(), pos.getY());
    }

    public static void renderMediumFont(RenderingLayer renderingLayer, String text, Vector2 pos, Color color) {
        mediumFont.setColor(color);
        mediumFont.draw(renderingLayer.getSpriteBatch(), text, pos.getX(), pos.getY());
    }

    public static void renderLargeFont(RenderingLayer renderingLayer, String text, Vector2 pos, Color color) {
        largeFont.setColor(color);
        largeFont.draw(renderingLayer.getSpriteBatch(), text, pos.getX(), pos.getY());
    }

    public static void renderVeryLargeFont(RenderingLayer renderingLayer, String text, Vector2 pos, Color color) {
        veryLargeFont.setColor(color);
        veryLargeFont.draw(renderingLayer.getSpriteBatch(), text, pos.getX(), pos.getY());
    }
}
