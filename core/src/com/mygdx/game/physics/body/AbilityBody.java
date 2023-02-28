package com.mygdx.game.physics.body;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.ability.AbilityState;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.physics.world.PhysicsWorld;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class AbilityBody {

    @Getter
    AbilityId abilityId;

    @Getter
    CreatureId creatureId;

    Body b2Body = null;
    final Sprite sprite = new Sprite(); // only used for calculating vertices

    PhysicsWorld world;

    @Getter
    Boolean isBodyInitialized = false;

    public static AbilityBody of(AbilityId abilityId) {
        AbilityBody abilityBody = new AbilityBody();
        abilityBody.abilityId = abilityId;
        return abilityBody;
    }

    private float[] hitboxVertices(GameState gameState) {
        Ability ability = gameState.abilities().get(abilityId);

        sprite.setSize(ability.params().width(), ability.params().height());
        sprite.setCenter(0, 0);
        sprite.setOriginCenter();
        sprite.setRotation(ability.params().rotationAngle());

        float[] vertices = sprite.getVertices();

        return new float[]{vertices[0], vertices[1], vertices[5], vertices[6], vertices[10], vertices[11], vertices[15], vertices[16]};
    }

    public void setVelocity(Vector2 velocity) {
        b2Body.setLinearVelocity(new com.badlogic.gdx.math.Vector2(velocity.x(), velocity.y()));
    }

    public Vector2 getBodyPos() {
        return Vector2.of(b2Body.getWorldCenter().x, b2Body.getWorldCenter().y);
    }

    public void trySetTransform(Vector2 vector) {
        if (!world.b2world().isLocked()) {
            b2Body.setTransform(vector.x(), vector.y(), b2Body.getAngle());
        }

    }

    public void init(GamePhysics physics, GameState gameState, boolean skipCreatingBody) {
        Ability ability = gameState.abilities().get(abilityId);

        if (!isBodyInitialized && !skipCreatingBody && ability != null) {
            world = physics.physicsWorlds().get(ability.params().areaId());

            creatureId = ability.params().creatureId();

            b2Body = B2BodyFactory.createAbilityB2Body(world, this, ability.params().pos(), hitboxVertices(gameState));

            isBodyInitialized = true;
        }

    }

    public void update(GameState gameState) {
        Ability ability = gameState.abilities().get(abilityId);

        if (isBodyInitialized() && ability != null && ability.bodyShouldExist()) {
            if (ability.isPositionManipulated() && (ability.params().state() == AbilityState.CHANNEL || ability.params()
                                                                                                               .state() ==
                                                                                                        AbilityState.ACTIVE)) {
                b2Body.setTransform(ability.params().pos().x(), ability.params().pos().y(), 0f);
            }

            if (ability.params().velocity() != null) {
                setVelocity(ability.params().velocity());
            }


        }
    }

    public void onRemove() {
        if (isBodyInitialized()) {
            world.b2world().destroyBody(b2Body);

        }

    }
}