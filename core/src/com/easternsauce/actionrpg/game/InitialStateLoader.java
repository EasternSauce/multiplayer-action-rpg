package com.easternsauce.actionrpg.game;

import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyTemplate;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPoint;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPointInfo;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.NoArgsConstructor;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(staticName = "of")
public class InitialStateLoader {
  public void setupInitialState(CoreGame game) {
    //        Map<SkillType, Integer> grantedSkills = new OrderedMap<>();
    //        Map<SkillType, Integer> grantedSkills2 = new OrderedMap<>();
    //        grantedSkills.put(SkillType.METEOR, 1);
    //        grantedSkills2.put(SkillType.VOLATILE_BUBBLE, 1);
    //        Item leatherArmor = Item
    //                .of()
    //                .setTemplate(ItemTemplate.templates.get("leatherArmor"))
    //                .setQualityModifier(0.9f)
    //                .setGrantedSkills(grantedSkills);
    //        Item hideGloves = Item
    //                .of()
    //                .setTemplate(ItemTemplate.templates.get("hideGloves"))
    //                .setQualityModifier(0.9f)
    //                .setGrantedSkills(grantedSkills2);
    //        Item crossbow = Item.of().setTemplate(ItemTemplate.templates.get("crossbow")).setQualityModifier(0.8f);
    //
    //        game.getGameState().scheduleServerSideAction(LootPileSpawnAction.of(EntityId.of("Area1"),
    //                Vector2.of(12, 12),
    //                new ConcurrentSkipListSet<>(Arrays.asList(leatherArmor, hideGloves, crossbow))
    //        ));

    EntityId<AreaGate> area1ToArea3 = EntityId.of("Area1ToArea3_" + (int) (Math.random() * 10000000));
    EntityId<AreaGate> area3ToArea1 = EntityId.of("Area3ToArea1_" + (int) (Math.random() * 10000000));
    EntityId<AreaGate> area1ToArea2 = EntityId.of("Area1ToArea2_" + (int) (Math.random() * 10000000));
    EntityId<AreaGate> area2ToArea1 = EntityId.of("Area2ToArea1_" + (int) (Math.random() * 10000000));
    EntityId<AreaGate> area3ToArea4 = EntityId.of("Area3ToArea4_" + (int) (Math.random() * 10000000));
    EntityId<AreaGate> area4ToArea3 = EntityId.of("Area3ToArea4_" + (int) (Math.random() * 10000000));

    game.getGameState().getAreaGates().clear();

    game.getGameState().getAreaGates().put(area1ToArea3,
      AreaGate.of(area1ToArea3, 1.5f, 1.5f, Vector2.of(199.5f, 15f), EntityId.of("Area1"), area3ToArea1));
    game.getGameState().getAreaGates().put(area3ToArea1,
      AreaGate.of(area3ToArea1, 1.5f, 1.5f, Vector2.of(17f, 2.5f), EntityId.of("Area3"), area1ToArea3));
    game.getGameState().getAreaGates()
      .put(area1ToArea2, AreaGate.of(area1ToArea2, 1.5f, 1.5f, Vector2.of(2f, 63f), EntityId.of("Area1"), area2ToArea1));
    game.getGameState().getAreaGates()
      .put(area2ToArea1, AreaGate.of(area2ToArea1, 1.5f, 1.5f, Vector2.of(58f, 9f), EntityId.of("Area2"), area1ToArea2));
    game.getGameState().getAreaGates()
      .put(area3ToArea4,
        AreaGate.of(area3ToArea4, 1.5f, 1.5f, Vector2.of(162.86261f, 138.66278f), EntityId.of("Area3"), area4ToArea3));
    game.getGameState().getAreaGates()
      .put(area4ToArea3,
        AreaGate.of(area3ToArea4, 1.5f, 1.5f, Vector2.of(50.926105f, 92.75383f), EntityId.of("Area4"), area3ToArea4));

    game.getGameState().getCheckpoints().clear();

    game.getGameState().getCheckpoints()
      .put(EntityId.of("Area1Checkpoint1"),
        Checkpoint.of(EntityId.of("Area1Checkpoint1"), EntityId.of("Area1"), Vector2.of(44.680607f, 14.780628f)));

    game.getGameState().getCheckpoints()
      .put(EntityId.of("Area2Checkpoint1"),
        Checkpoint.of(EntityId.of("Area2Checkpoint1"), EntityId.of("Area2"), Vector2.of(25.510015f, 15.345517f)));

    game.getGameState().getCheckpoints()
      .put(EntityId.of("Area3Checkpoint1"),
        Checkpoint.of(EntityId.of("Area3Checkpoint1"), EntityId.of("Area3"), Vector2.of(52.294518f, 18.740194f)));

    game.getGameState().getCheckpoints()
      .put(EntityId.of("Area3Checkpoint2"),
        Checkpoint.of(EntityId.of("Area3Checkpoint2"), EntityId.of("Area3"), Vector2.of(85.67888f, 189.58704f)));

    Map<EntityId<EnemyRallyPoint>, EnemyRallyPoint> enemyRallyPoints = game.getGameState().getEnemyRallyPoints();

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(72.04751f, 25.991135f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.skeleton, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 1);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(34.088314f, 51.699497f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.skeleton, 100),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 1);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(5.743255f, 33.62826f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 100)
        //                new AbstractMap.SimpleEntry<>(EnemyTemplate.spider, 100)
      ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 1);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(18.355146f, 102.16021f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 300)
        //                new AbstractMap.SimpleEntry<>(EnemyTemplate.spider, 100)
      ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 2);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(53.2744f, 84.06082f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.skeleton, 300),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 100),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.wolf, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 2);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(71.45329f, 69.61391f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.skeleton, 100),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 300))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 2);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(54.74892f, 114.00807f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.skeleton, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 300),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 4);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(50.533447f, 150.28108f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.skeleton, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 3);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(3.3964655f, 127.868256f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.skeleton, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 200), new AbstractMap.SimpleEntry<>(EnemyTemplate.wolf, 100),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 50))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 3);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(22.922045f, 180.38924f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.skeleton, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 50))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 5);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(72.00597f, 185.74562f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.skeleton, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 200), new AbstractMap.SimpleEntry<>(EnemyTemplate.wolf, 100),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 5);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(120.66566f, 61.488186f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.skeleton, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 200))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 2);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(107.045654f, 109.50901f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.skeleton, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 200))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 5);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(90.433754f, 149.47726f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.skeleton, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 5);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(143.88635f, 144.49992f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.wolf, 100),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 40))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 5);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(194.33566f, 161.41022f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.wolf, 70), new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 40))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 4);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(180.13132f, 189.53963f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 3);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(194.17567f, 130.81833f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.spider, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 1);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(188.66309f, 94.52115f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 70))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 2);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(149.25409f, 59.35056f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.skeleton, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 2);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(189.71574f, 50.233543f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.rat, 200))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 1);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(151.91226f, 4.116901f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.sludge, 200))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 1);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area1"), Vector2.of(134.38565f, 33.810173f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.rat, 200))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 2);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area3"), Vector2.of(45.149673f, 42.696243f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.rat, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.wolf, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 1);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area3"), Vector2.of(9.14936f, 85.46421f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.rat, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 100), new AbstractMap.SimpleEntry<>(EnemyTemplate.wolf, 70))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 2);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area3"), Vector2.of(77.41629f, 63.43089f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.rat, 200),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 2);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area3"), Vector2.of(8.524707f, 50.28812f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.minos, 150),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.rat, 200))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 2);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area3"), Vector2.of(96.41956f, 25.716f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.minos, 150),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.rat, 200), new AbstractMap.SimpleEntry<>(EnemyTemplate.sludge, 50),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.spider, 100),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 80),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.mage, 200), new AbstractMap.SimpleEntry<>(EnemyTemplate.wolf, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 5);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area3"), Vector2.of(53.30063f, 115.123146f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.minos, 50),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.rat, 300), new AbstractMap.SimpleEntry<>(EnemyTemplate.spider, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 4);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area3"), Vector2.of(46.327713f, 152.19821f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.minos, 50),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 50), new AbstractMap.SimpleEntry<>(EnemyTemplate.rat, 300),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.sludge, 100),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.spider, 100),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.wolf, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 4);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area3"), Vector2.of(94.59606f, 141.75322f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.rat, 100),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.sludge, 300),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.spider, 100),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.wolf, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 2);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area3"), Vector2.of(123.96952f, 176.52777f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.rat, 100),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.sludge, 200))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 2);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area3"), Vector2.of(164.68907f, 138.92996f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.sludge, 100),
          new AbstractMap.SimpleEntry<>(EnemyTemplate.archer, 100))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 3);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area3"), Vector2.of(186.80959f, 142.67236f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.sludge, 100),
        new AbstractMap.SimpleEntry<>(EnemyTemplate.rat, 100),
        new AbstractMap.SimpleEntry<>(EnemyTemplate.skeleton, 100),
        new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 70)

      ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 3);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area3"), Vector2.of(158.88972f, 184.15317f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.spider, 100),
        new AbstractMap.SimpleEntry<>(EnemyTemplate.minos, 100),
        new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 100)

      ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 3);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area3"), Vector2.of(164.75897f, 85.33934f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.spider, 100),
        new AbstractMap.SimpleEntry<>(EnemyTemplate.minos, 100),
        new AbstractMap.SimpleEntry<>(EnemyTemplate.serpent, 100),
        new AbstractMap.SimpleEntry<>(EnemyTemplate.sludge, 100), new AbstractMap.SimpleEntry<>(EnemyTemplate.rat, 300)

      ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 5);

    addEnemyRallyPoint(enemyRallyPoints, EntityId.of("Area4"), Vector2.of(53.219776f, 52.394897f),
      Stream.of(new AbstractMap.SimpleEntry<>(EnemyTemplate.taurus, 100)

      ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)), 1);

  }

  private static void addEnemyRallyPoint(Map<EntityId<EnemyRallyPoint>, EnemyRallyPoint> enemyRallyPoints, EntityId<Area> areaId, Vector2 pos, Map<EnemyTemplate, Integer> enemyTemplateWeights, int enemiesTotal) {
    EntityId<EnemyRallyPoint> rallyPointId = EntityId.of("EnemyRallyPoint_" + (int) (Math.random() * 10000000));

    EnemyRallyPointInfo rallyPointInfo = EnemyRallyPointInfo.of(pos, areaId, enemyTemplateWeights, enemiesTotal);

    EnemyRallyPoint enemyRallyPoint = EnemyRallyPoint.of(rallyPointId, rallyPointInfo);

    enemyRallyPoints.put(rallyPointId, enemyRallyPoint);
  }
}
