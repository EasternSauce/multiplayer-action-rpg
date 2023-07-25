package com.easternsauce.actionrpg.game;

import com.easternsauce.actionrpg.model.action.LootPileSpawnAction;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.area.AreaGateId;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.EnemyTemplate;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPoint;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPointId;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPointInfo;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.item.ItemTemplate;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.NoArgsConstructor;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(staticName = "of")
public class InitialStateLoader {
    public void setupInitialState(CoreGame game) {
        Map<SkillType, Integer> grantedSkills = new ConcurrentSkipListMap<>();
        Map<SkillType, Integer> grantedSkills2 = new ConcurrentSkipListMap<>();
        grantedSkills.put(SkillType.EMERALD_SPIN, 1);
        grantedSkills2.put(SkillType.VOLATILE_BUBBLE, 1);
        Item leatherArmor = Item
            .of()
            .setTemplate(ItemTemplate.templates.get("leatherArmor"))
            .setQualityModifier(0.9f)
            .setGrantedSkills(grantedSkills);
        Item hideGloves = Item
            .of()
            .setTemplate(ItemTemplate.templates.get("hideGloves"))
            .setQualityModifier(0.9f)
            .setGrantedSkills(grantedSkills2);
        Item crossbow = Item.of().setTemplate(ItemTemplate.templates.get("crossbow")).setQualityModifier(0.8f);

        game.getGameState().scheduleServerSideAction(LootPileSpawnAction.of(AreaId.of("area3"),
            Vector2.of(12, 12),
            new ConcurrentSkipListSet<>(Arrays.asList(leatherArmor, hideGloves, crossbow))
        ));

        AreaGateId area1ToArea3 = AreaGateId.of("area1ToArea3_" + (int) (Math.random() * 10000000));
        AreaGateId area3ToArea1 = AreaGateId.of("area3ToArea1_" + (int) (Math.random() * 10000000));
        AreaGateId area1ToArea2 = AreaGateId.of("area1ToArea2_" + (int) (Math.random() * 10000000));
        AreaGateId area2ToArea1 = AreaGateId.of("area2ToArea1_" + (int) (Math.random() * 10000000));

        game.getGameState().getAreaGates().clear();

        game.getGameState().getAreaGates().put(area1ToArea3,
            AreaGate.of(area1ToArea3, 1.5f, 1.5f, Vector2.of(199.5f, 15f), AreaId.of("area1"), area3ToArea1)
        );
        game.getGameState().getAreaGates().put(area3ToArea1,
            AreaGate.of(area3ToArea1, 1.5f, 1.5f, Vector2.of(17f, 2.5f), AreaId.of("area3"), area1ToArea3)
        );
        game.getGameState().getAreaGates().put(area1ToArea2,
            AreaGate.of(area1ToArea2, 1.5f, 1.5f, Vector2.of(2f, 63f), AreaId.of("area1"), area2ToArea1)
        );
        game.getGameState().getAreaGates().put(area2ToArea1,
            AreaGate.of(area2ToArea1, 1.5f, 1.5f, Vector2.of(58f, 9f), AreaId.of("area2"), area1ToArea2)
        );

        Map<EnemyRallyPointId, EnemyRallyPoint> enemyRallyPoints = game.getGameState().getEnemyRallyPoints();

        EnemyRallyPointInfo rallyPointInfo = EnemyRallyPointInfo.of(Vector2.of(40.88874f, 38.543716f),
            AreaId.of("area3"),
            Stream.of(
                //                                new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 400),
                //                                new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 100),
                new AbstractMap.SimpleEntry<>(EnemyTemplate.spider, 100)
                //                new AbstractMap.SimpleEntry<>(EnemyTemplate.sludge, 100),
                //                                    new AbstractMap.SimpleEntry<>(EnemyTemplate.minos, 100)
            ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)),
            1
        );

        EnemyRallyPointId rallyPointId = EnemyRallyPointId.of("enemyrallypoint1"); // TODO: generate this id

        enemyRallyPoints.put(rallyPointId, EnemyRallyPoint.of(rallyPointId, rallyPointInfo));
    }
}
