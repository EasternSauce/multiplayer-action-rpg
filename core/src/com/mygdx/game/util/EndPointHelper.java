package com.mygdx.game.util;

import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.command.*;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.*;
import com.mygdx.game.model.action.*;
import com.mygdx.game.model.area.Area;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.*;
import com.mygdx.game.model.item.EquipmentSlotType;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.item.ItemTemplate;
import com.mygdx.game.model.skill.ScheduledAbility;
import com.mygdx.game.model.skill.Skill;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class EndPointHelper {
    public static void registerEndPointClasses(EndPoint endPoint) {
        endPoint.getKryo().setRegistrationRequired(true);

        endPoint.getKryo().register(ArrayList.class);
        endPoint.getKryo().register(LinkedList.class);
        endPoint.getKryo().register(ConcurrentSkipListSet.class);
        endPoint.getKryo().register(ConcurrentSkipListMap.class);

        endPoint.getKryo().register(CreatureId.class);
        endPoint.getKryo().register(Vector2.class);
        endPoint.getKryo().register(Vector2Int.class);
        endPoint.getKryo().register(AreaId.class);
        endPoint.getKryo().register(SimpleTimer.class);
        endPoint.getKryo().register(AbilityType.class);
        endPoint.getKryo().register(AbilityState.class);
        endPoint.getKryo().register(EnemyType.class);
        endPoint.getKryo().register(SkillType.class);
        endPoint.getKryo().register(EnemySpawn.class);
        endPoint.getKryo().register(AbilityId.class);
        endPoint.getKryo().register(EnemyAiState.class);

        endPoint.getKryo().register(InitPlayerCommand.class);
        endPoint.getKryo().register(SendChatMessageCommand.class);
        endPoint.getKryo().register(SpawnEnemyCommand.class);
        endPoint.getKryo().register(PerformActionCommand.class);
        endPoint.getKryo().register(TryPerformSkillCommand.class);

        endPoint.getKryo().register(Ability.class);
        endPoint.getKryo().register(SummonGhosts.class);
        endPoint.getKryo().register(SwordSlash.class);
        endPoint.getKryo().register(CrossbowBolt.class);
        endPoint.getKryo().register(Fireball.class);
        endPoint.getKryo().register(FireballExplosion.class);
        endPoint.getKryo().register(LightningChain.class);
        endPoint.getKryo().register(LightningNode.class);
        endPoint.getKryo().register(LightningSpark.class);
        endPoint.getKryo().register(MagicOrb.class);
        endPoint.getKryo().register(VolatileBubble.class);
        endPoint.getKryo().register(IceSpear.class);
        endPoint.getKryo().register(PlayfulGhost.class);
        endPoint.getKryo().register(RicochetBullet.class);
        endPoint.getKryo().register(RicochetBallista.class);
        endPoint.getKryo().register(Boomerang.class);
        endPoint.getKryo().register(SummonShield.class);
        endPoint.getKryo().register(SwordSpin.class);
        endPoint.getKryo().register(Teleport.class);
        endPoint.getKryo().register(TeleportDestination.class);


        endPoint.getKryo().register(Enemy.class);
        endPoint.getKryo().register(Area.class);
        endPoint.getKryo().register(Player.class);
        endPoint.getKryo().register(ScheduledAbility.class);
        endPoint.getKryo().register(Skill.class);
        endPoint.getKryo().register(WorldDirection.class);
        endPoint.getKryo().register(CreatureParams.class);
        endPoint.getKryo().register(AbilityParams.class);


        endPoint.getKryo().register(AddAbilityAction.class);
        endPoint.getKryo().register(InitPlayerAction.class);
        endPoint.getKryo().register(CreatureDeathAction.class);
        endPoint.getKryo().register(MoveCreatureTowardsTargetAction.class);
        endPoint.getKryo().register(RemoveAbilityAction.class);
        endPoint.getKryo().register(RemovePlayerAction.class);
        endPoint.getKryo().register(RespawnCreatureAction.class);
        endPoint.getKryo().register(TryPerformSkillAction.class);
        endPoint.getKryo().register(AbilityActivateAction.class);
        endPoint.getKryo().register(SetCreatureMovingVectorAction.class);
        endPoint.getKryo().register(SwapSlotsInsideInventoryAction.class);
        endPoint.getKryo().register(ToggleInventoryAction.class);
        endPoint.getKryo().register(PickUpInventoryItemAction.class);
        endPoint.getKryo().register(FinishInventoryMoveAction.class);
        endPoint.getKryo().register(SwapSlotsBetweenInventoryAndEquipmentAction.class);
        endPoint.getKryo().register(PickUpEquipmentItemAction.class);

        endPoint.getKryo().register(EquipmentSlotType.class);
        endPoint.getKryo().register(ItemTemplate.class);
        endPoint.getKryo().register(Item.class);

        endPoint.getKryo().register(PlayerParams.class);
        endPoint.getKryo().register(ActionsHolder.class);
        endPoint.getKryo().register(GameState.class);
        endPoint.getKryo().register(GameStateBroadcast.class);

    }
}
