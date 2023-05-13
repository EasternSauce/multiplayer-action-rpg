package com.easternsauce.actionrpg.physics.body;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class AbilityBody {

    final Sprite sprite = new Sprite(); // only used for calculating vertices
    @Getter
    AbilityId abilityId;
    @Getter
    CreatureId creatureId;
    Body b2body = null;
    PhysicsWorld world;

    @Getter
    Boolean isBodyInitialized = false;

    public static AbilityBody of(AbilityId abilityId) {
        AbilityBody abilityBody = new AbilityBody();
        abilityBody.abilityId = abilityId;
        return abilityBody;
    }

    private float[] hitboxVertices(CoreGame game) {
        Ability ability = game.getGameState().accessAbilities().getAbility(abilityId);

        sprite.setSize(ability.getParams().getWidth(), ability.getParams().getHeight());
        sprite.setCenter(0, 0);
        sprite.setOriginCenter();
        sprite.setRotation(ability.getParams().getRotationAngle());

        float[] vertices = sprite.getVertices();

        return new float[]{
            vertices[0],
            vertices[1],
            vertices[5],
            vertices[6],
            vertices[10],
            vertices[11],
            vertices[15],
            vertices[16]};
    }

    public void setVelocity(Vector2 velocity) {
        b2body.setLinearVelocity(new com.badlogic.gdx.math.Vector2(velocity.getX(), velocity.getY()));
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
        Ability ability = game.getGameState().accessAbilities().getAbility(abilityId);

        if (!isBodyInitialized && !skipCreatingBody && ability != null) {
            world = game.getPhysicsWorld(ability.getParams().getAreaId());

            creatureId = ability.getParams().getCreatureId();

            b2body = B2BodyFactory.createAbilityB2Body(world, this, ability.getParams().getPos(), hitboxVertices(game));

            isBodyInitialized = true;
        }

    }

    public void update(CoreGame game) {
        Ability ability = game.getGameState().accessAbilities().getAbilities().get(abilityId);

        if (getIsBodyInitialized() && ability != null && ability.bodyShouldExist()) {
            if (ability.isPositionChangedOnUpdate() && (ability.getParams().getState() == AbilityState.CHANNEL ||
                                                        ability.getParams().getState() == AbilityState.ACTIVE)) {
                b2body.setTransform(ability.getParams().getPos().getX(), ability.getParams().getPos().getY(), 0f);
            }

            if (ability.getParams().getVelocity() != null) {
                setVelocity(ability.getParams().getVelocity());
            }

        }
    }

    public void onRemove() {
        if (getIsBodyInitialized()) {
            world.getB2world().destroyBody(b2body);

        }

    }
}