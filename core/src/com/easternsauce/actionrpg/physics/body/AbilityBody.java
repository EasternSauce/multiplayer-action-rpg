package com.easternsauce.actionrpg.physics.body;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import lombok.Getter;
import lombok.NoArgsConstructor;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
public class AbilityBody {
    private final Sprite sprite = new Sprite(); // only used for calculating vertices
    @Getter
    private AbilityId abilityId;
    @Getter
    private CreatureId creatureId;
    @Getter
    private Body b2body = null;
    private PhysicsWorld world;

    @Getter
    private Boolean bodyInitialized = false;

    public static AbilityBody of(AbilityId abilityId) {
        AbilityBody abilityBody = new AbilityBody();
        abilityBody.abilityId = abilityId;
        return abilityBody;
    }

    public Vector2 getBodyPos() {
        return Vector2.of(b2body.getWorldCenter().x, b2body.getWorldCenter().y);
    }

    public void trySetTransform(Vector2 vector) {
        if (!world.getB2world().isLocked()) {
            b2body.setTransform(vector.getX(), vector.getY(), b2body.getAngle());
        }

    }

    public void activate(boolean skipCreatingBody, CoreGame game) {
        Ability ability = game.getAbility(abilityId);

        if (!skipCreatingBody && ability != null) {
            world = game.getPhysicsWorld(ability.getParams().getAreaId());

            creatureId = ability.getParams().getCreatureId();

            b2body = B2BodyFactory.createAbilityB2Body(world,
                this,
                ability.getParams().getPos(),
                hitboxVertices(ability)
            );

            bodyInitialized = true;
        }

    }

    private float[] hitboxVertices(Ability ability) {
        if (ability.getParams().getOverrideScale() != null) {
            sprite.setSize(
                ability.getParams().getOverrideScale() * ability.getParams().getWidth(),
                ability.getParams().getOverrideScale() * ability.getParams().getOverrideScale()
            );
        } else {
            sprite.setSize(ability.getParams().getWidth(), ability.getParams().getHeight());
        }
        sprite.setCenter(0, 0);
        sprite.setOriginCenter();
        sprite.setRotation(ability.getParams().getRotationAngle());

        float[] vertices = sprite.getVertices();

        return new float[]{vertices[0], vertices[1], vertices[5], vertices[6], vertices[10], vertices[11], vertices[15], vertices[16]};
    }

    public void update(CoreGame game) {
        Ability ability = game.getAbilities().get(abilityId);

        if (getBodyInitialized() && ability != null && !ability.getParams().getSkipCreatingBody()) {
            if (ability.isPositionChangedOnUpdate()) {
                b2body.setTransform(ability.getParams().getPos().getX(), ability.getParams().getPos().getY(), 0f);
            }

            if (ability.getParams().getVelocity() != Vector2.of(0f, 0f)) {
                setVelocity(ability.getParams().getVelocity());
            }

        }
    }

    public void setVelocity(Vector2 velocity) {
        b2body.setLinearVelocity(new com.badlogic.gdx.math.Vector2(velocity.getX(), velocity.getY()));
    }

    public void onRemove() {
        if (getBodyInitialized()) {
            world.getB2world().destroyBody(b2body);

        }

    }
}