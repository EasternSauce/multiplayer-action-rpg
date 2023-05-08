package com.mygdx.game.util;

import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.command.*;
import com.mygdx.game.model.GameStateData;
import com.mygdx.game.model.ability.*;
import com.mygdx.game.model.action.ActionsHolder;
import com.mygdx.game.model.action.ability.AbilityActivateAction;
import com.mygdx.game.model.action.ability.AbilityRemoveAction;
import com.mygdx.game.model.action.ability.AbilityTryAddAction;
import com.mygdx.game.model.action.ability.SkillTryPerformAction;
import com.mygdx.game.model.action.creature.*;
import com.mygdx.game.model.action.inventory.*;
import com.mygdx.game.model.action.loot.LootPileDespawnAction;
import com.mygdx.game.model.action.loot.LootPileItemTryPickUpAction;
import com.mygdx.game.model.action.loot.LootPileSpawnAction;
import com.mygdx.game.model.action.loot.LootPileSpawnOnPlayerItemDropAction;
import com.mygdx.game.model.action.skillmenu.SkillPickerMenuActivateAction;
import com.mygdx.game.model.action.skillmenu.SkillPickerMenuDeactivateAction;
import com.mygdx.game.model.action.skillmenu.SkillPickerMenuSlotChangeAction;
import com.mygdx.game.model.area.*;
import com.mygdx.game.model.creature.*;
import com.mygdx.game.model.creature.effect.CreatureEffect;
import com.mygdx.game.model.creature.effect.CreatureEffectState;
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
        endPoint.getKryo().register(AreaId.class);
        endPoint.getKryo().register(AbilityId.class);
        endPoint.getKryo().register(LootPileId.class);

        endPoint.getKryo().register(Vector2.class);
        endPoint.getKryo().register(Vector2Int.class);
        endPoint.getKryo().register(SimpleTimer.class);
        endPoint.getKryo().register(AbilityType.class);
        endPoint.getKryo().register(AbilityState.class);
        endPoint.getKryo().register(EnemyType.class);
        endPoint.getKryo().register(SkillType.class);
        endPoint.getKryo().register(EnemySpawn.class);
        endPoint.getKryo().register(EnemyAiState.class);
        endPoint.getKryo().register(LootPile.class);
        endPoint.getKryo().register(CreatureEffect.class);
        endPoint.getKryo().register(CreatureEffectState.class);

        endPoint.getKryo().register(ConnectionInitCommand.class);
        endPoint.getKryo().register(PlayerInitCommand.class);
        endPoint.getKryo().register(ChatMessageSendCommand.class);
        endPoint.getKryo().register(EnemySpawnCommand.class);
        endPoint.getKryo().register(ActionPerformCommand.class);

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
        endPoint.getKryo().register(TeleportSource.class);
        endPoint.getKryo().register(TeleportDestination.class);
        endPoint.getKryo().register(CrossbowShot.class);

        endPoint.getKryo().register(Enemy.class);
        endPoint.getKryo().register(Area.class);
        endPoint.getKryo().register(Player.class);
        endPoint.getKryo().register(ScheduledAbility.class);
        endPoint.getKryo().register(Skill.class);
        endPoint.getKryo().register(WorldDirection.class);
        endPoint.getKryo().register(CreatureParams.class);
        endPoint.getKryo().register(AbilityParams.class);
        endPoint.getKryo().register(AreaGate.class);
        endPoint.getKryo().register(DropTableEntry.class);

        endPoint.getKryo().register(AbilityTryAddAction.class);
        endPoint.getKryo().register(PlayerInitAction.class);
        endPoint.getKryo().register(CreatureMoveTowardsTargetAction.class);
        endPoint.getKryo().register(AbilityRemoveAction.class);
        endPoint.getKryo().register(PlayerRemoveAction.class);
        endPoint.getKryo().register(CreatureRespawnAction.class);
        endPoint.getKryo().register(SkillTryPerformAction.class);
        endPoint.getKryo().register(AbilityActivateAction.class);
        endPoint.getKryo().register(CreatureMovingVectorSetAction.class);
        endPoint.getKryo().register(InventorySwapSlotsAction.class);
        endPoint.getKryo().register(InventoryToggleAction.class);
        endPoint.getKryo().register(InventoryItemPickUpAction.class);
        endPoint.getKryo().register(InventoryMoveCancelAction.class);
        endPoint.getKryo().register(InventoryAndEquipmentSwapSlotsAction.class);
        endPoint.getKryo().register(EquipmentItemPickUpAction.class);
        endPoint.getKryo().register(LootPileDespawnAction.class);
        endPoint.getKryo().register(LootPileItemTryPickUpAction.class);
        endPoint.getKryo().register(LootPileSpawnAction.class);
        endPoint.getKryo().register(LootPileSpawnOnPlayerItemDropAction.class);
        endPoint.getKryo().register(SkillPickerMenuActivateAction.class);
        endPoint.getKryo().register(SkillPickerMenuSlotChangeAction.class);
        endPoint.getKryo().register(SkillPickerMenuDeactivateAction.class);
        endPoint.getKryo().register(CreatureHitAction.class);
        endPoint.getKryo().register(CreatureChangeAimDirectionAction.class);

        endPoint.getKryo().register(EquipmentSlotType.class);
        endPoint.getKryo().register(ItemTemplate.class);
        endPoint.getKryo().register(Item.class);

        endPoint.getKryo().register(PlayerParams.class);
        endPoint.getKryo().register(ActionsHolder.class);
        endPoint.getKryo().register(GameStateData.class);
        endPoint.getKryo().register(GameStateBroadcast.class);

    }
}
