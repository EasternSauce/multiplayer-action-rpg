package com.mygdx.game.ability;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class LightningChain extends Ability {
    AbilityParams params;


    @Override
    void onAbilityStarted(MyGdxGame game) {

    }

    @Override
    void onDelayedAction(MyGdxGame game) {

    }

    @Override
    void onAbilityCompleted(MyGdxGame game) {

    }

    @Override
    void onUpdatePosition(GameState gameState) {

    }

    @Override
    void onChannelUpdate(GameState gameState) {

    }

    @Override
    void onActiveUpdate(GameState gameState) {

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onTerrainHit() {

    }

    public static LightningChain of(AbilityId abilityId,
                                    AreaId areaId,
                                    CreatureId creatureId,
                                    Vector2 pos,
                                    Vector2 dirVector,
                                    Set<CreatureId> creaturesAlreadyHit,
                                    Vector2 chainFromPos) {
        LightningChain ability = LightningChain.of();
        ability.params = AbilityParams.of()
                                      .id(abilityId)
                                      .areaId(areaId)
                                      .width(1f)
                                      .height(3f)
                                      .channelTime(0f)
                                      .activeTime(0.4f)
                                      .textureName("lightning_chain")
                                      .creatureId(creatureId)
                                      .damage(0f)
                                      .isActiveAnimationLooping(true)
                                      .attackWithoutMoving(true)
                                      .pos(LightningChain.calculatePos(pos, chainFromPos))
                                      .rotationAngle(LightningChain.calculateRotationAngle(pos, chainFromPos))
                                      .creaturesAlreadyHit(creaturesAlreadyHit)
                                      .manaCost(0f)
                                      .staminaCost(0f)
                                      .cooldown(0f)
                                      .performableByCreature(true)
                                      .inactiveBody(true)
                                      .dirVector(dirVector)
                                      .rotationShift(90f);

        return ability;
    }

    private static Float calculateRotationAngle(Vector2 pos, Vector2 chainFromPos) {
        Vector2 chainDirVector = pos.vectorTowards(chainFromPos);

        return chainDirVector.angleDeg();
    }

    private static Vector2 calculatePos(Vector2 pos, Vector2 chainFromPos) {
        Vector2 chainDirVector = pos.vectorTowards(chainFromPos);

        float attackShiftX = chainDirVector.normalized().x() * chainFromPos.distance(pos) / 2;
        float attackShiftY = chainDirVector.normalized().y() * chainFromPos.distance(pos) / 2;

        return Vector2.of(pos.x() + attackShiftX, pos.y() + attackShiftY);

    }
}
