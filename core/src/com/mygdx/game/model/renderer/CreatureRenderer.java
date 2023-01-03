//package com.mygdx.game.model.renderer;
//
//import com.badlogic.gdx.graphics.g2d.Animation;
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.mygdx.game.model.creature.CreatureId;
//import com.mygdx.game.model.game.GameState;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@NoArgsConstructor(staticName = "of")
//@AllArgsConstructor(staticName = "of")
//@Data
//@Builder
//public class CreatureRenderer {
//    private CreatureId creatureId;
//
//    private Sprite sprite;
//
//    private TextureRegion[] facingTextures;
//
//    private Animation<TextureRegion>[] runningAnimations;
//
//    private TextureRegion textureRegion;
//
//    public void init(TextureAtlas atlas, GameState gameState) {
//        sprite = new Sprite();
//
//        facingTextures = new TextureRegion[4];
//
//        runningAnimations = new Animation<TextureRegion>[4];
//    }
//}
