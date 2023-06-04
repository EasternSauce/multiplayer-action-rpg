package com.easternsauce.actionrpg.util;

import com.easternsauce.actionrpg.game.command.*;
import com.easternsauce.actionrpg.model.GameStateData;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.action.*;
import com.easternsauce.actionrpg.model.area.*;
import com.easternsauce.actionrpg.model.creature.*;
import com.easternsauce.actionrpg.model.item.EquipmentSlotType;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.item.ItemTemplate;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.*;
import com.esotericsoftware.kryonet.EndPoint;

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
        endPoint.getKryo().register(AreaGateId.class);

        endPoint.getKryo().register(Vector2.class);
        endPoint.getKryo().register(Vector2Int.class);
        endPoint.getKryo().register(SimpleTimer.class);
        endPoint.getKryo().register(AbilityType.class);
        endPoint.getKryo().register(AbilityState.class);
        endPoint.getKryo().register(EnemyType.class);
        endPoint.getKryo().register(SkillType.class);
        endPoint.getKryo().register(EnemySpawn.class);
        endPoint.getKryo().register(EnemyAutoControlState.class);
        endPoint.getKryo().register(LootPile.class);
        endPoint.getKryo().register(AreaGate.class);
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
        endPoint.getKryo().register(MobSwordSlash.class);
        endPoint.getKryo().register(CrossbowBolt.class);
        endPoint.getKryo().register(Fireball.class);
        endPoint.getKryo().register(FireballExplosion.class);
        endPoint.getKryo().register(LightningChain.class);
        endPoint.getKryo().register(LightningNode.class);
        endPoint.getKryo().register(LightningSpark.class);
        endPoint.getKryo().register(MagicOrb.class);
        endPoint.getKryo().register(MobMagicOrb.class);
        endPoint.getKryo().register(VolatileBubble.class);
        endPoint.getKryo().register(IceSpear.class);
        endPoint.getKryo().register(PlayfulGhost.class);
        endPoint.getKryo().register(RicochetBullet.class);
        endPoint.getKryo().register(RicochetBallista.class);
        endPoint.getKryo().register(Boomerang.class);
        endPoint.getKryo().register(ShieldGuard.class);
        endPoint.getKryo().register(SwordSpin.class);
        endPoint.getKryo().register(BossSwordSpin.class);
        endPoint.getKryo().register(TeleportSource.class);
        endPoint.getKryo().register(TeleportDestination.class);
        endPoint.getKryo().register(CrossbowShot.class);
        endPoint.getKryo().register(MobCrossbowShot.class);
        endPoint.getKryo().register(PoisonousCloud.class);
        endPoint.getKryo().register(PoisonousMixture.class);
        endPoint.getKryo().register(SpreadingPoisonousCloud.class);
        endPoint.getKryo().register(Punch.class);
        endPoint.getKryo().register(RingOfFire.class);
        endPoint.getKryo().register(Dash.class);

        endPoint.getKryo().register(Enemy.class);
        endPoint.getKryo().register(Area.class);
        endPoint.getKryo().register(Player.class);
        endPoint.getKryo().register(Skill.class);
        endPoint.getKryo().register(WorldDirection.class);
        endPoint.getKryo().register(CreatureParams.class);
        endPoint.getKryo().register(AbilityParams.class);
        endPoint.getKryo().register(LootPileParams.class);
        endPoint.getKryo().register(DropTableEntry.class);
        endPoint.getKryo().register(EnemySkillUseEntry.class);
        endPoint.getKryo().register(EnemyParams.class);
        endPoint.getKryo().register(CreatureStats.class);
        endPoint.getKryo().register(CreatureEffectParams.class);
        endPoint.getKryo().register(CreatureMovementParams.class);

        endPoint.getKryo().register(PlayerInitAction.class);
        endPoint.getKryo().register(CreatureMoveTowardsTargetAction.class);
        endPoint.getKryo().register(PlayerRemoveAction.class);
        endPoint.getKryo().register(CreatureRespawnAction.class);
        endPoint.getKryo().register(SkillTryPerformAction.class);
        endPoint.getKryo().register(CreatureMovingVectorSetAction.class);
        endPoint.getKryo().register(InventorySwapSlotItemsAction.class);
        endPoint.getKryo().register(InventoryWindowToggleAction.class);
        endPoint.getKryo().register(InventoryItemPutOnCursorAction.class);
        endPoint.getKryo().register(InventoryPutOnCursorCancelAction.class);
        endPoint.getKryo().register(InventoryAndEquipmentSwapSlotItemsAction.class);
        endPoint.getKryo().register(EquipmentItemPutOnCursorAction.class);
        endPoint.getKryo().register(LootPileDespawnAction.class);
        endPoint.getKryo().register(LootPileItemTryPickUpAction.class);
        endPoint.getKryo().register(LootPileSpawnAction.class);
        endPoint.getKryo().register(ItemDropOnGroundAction.class);
        endPoint.getKryo().register(SkillPickerMenuActivateAction.class);
        endPoint.getKryo().register(SkillPickerMenuSlotChangeAction.class);
        endPoint.getKryo().register(SkillPickerMenuDeactivateAction.class);
        endPoint.getKryo().register(CreatureHitByAbilityAction.class);
        endPoint.getKryo().register(CreatureHitByDamageOverTimeAction.class);
        endPoint.getKryo().register(CreatureChangeAimDirectionAction.class);
        endPoint.getKryo().register(InventoryItemUseAction.class);

        endPoint.getKryo().register(EquipmentSlotType.class);
        endPoint.getKryo().register(ItemTemplate.class);
        endPoint.getKryo().register(Item.class);

        endPoint.getKryo().register(PlayerConfig.class);
        endPoint.getKryo().register(ActionsHolder.class);
        endPoint.getKryo().register(GameStateData.class);
        endPoint.getKryo().register(GameStateBroadcast.class);

    }
}
