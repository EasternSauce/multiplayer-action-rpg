package com.easternsauce.actionrpg.game;

import com.easternsauce.actionrpg.game.command.EnemySpawnCommand;
import com.easternsauce.actionrpg.game.util.EnemySpawnUtils;
import com.easternsauce.actionrpg.model.action.LootPileSpawnAction;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.area.AreaGateId;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.EnemySpawn;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.item.ItemTemplate;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.esotericsoftware.kryonet.Server;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
public class InitialStateLoader {
    public void setupInitialState(CoreGame game, Server server) {
        AreaId areaId = AreaId.of("area1");

        Map<SkillType, Integer> grantedSkills = new ConcurrentSkipListMap<>();
        grantedSkills.put(
            SkillType.DASH,
            1
        );
        Item leatherArmor = Item
            .of()
            .setTemplate(ItemTemplate.templates.get("leatherArmor"))
            .setQualityModifier(0.9f)
            .setGrantedSkills(grantedSkills);
        Item crossbow = Item.of().setTemplate(ItemTemplate.templates.get("crossbow")).setQualityModifier(0.8f);

        game.getGameState().scheduleServerSideAction(LootPileSpawnAction.of(
            AreaId.of("area3"),
            Vector2.of(
                12,
                12
            ),
            new ConcurrentSkipListSet<>(Arrays.asList(
                leatherArmor,
                crossbow
            ))
        ));

        AreaGateId area1ToArea3 = AreaGateId.of("area1ToArea3_" + (int) (Math.random() * 10000000));
        AreaGateId area3ToArea1 = AreaGateId.of("area3ToArea1_" + (int) (Math.random() * 10000000));
        AreaGateId area1ToArea2 = AreaGateId.of("area1ToArea2_" + (int) (Math.random() * 10000000));
        AreaGateId area2ToArea1 = AreaGateId.of("area2ToArea1_" + (int) (Math.random() * 10000000));

        game.getGameState().getAreaGates().clear();

        game.getGameState().getAreaGates().put(
            area1ToArea3,
            AreaGate.of(
                area1ToArea3,
                1.5f,
                1.5f,
                Vector2.of(
                    199.5f,
                    15f
                ),
                AreaId.of("area1"),
                area3ToArea1
            )
        );
        game.getGameState().getAreaGates().put(
            area3ToArea1,
            AreaGate.of(
                area3ToArea1,
                1.5f,
                1.5f,
                Vector2.of(
                    17f,
                    2.5f
                ),
                AreaId.of("area3"),
                area1ToArea3
            )
        );
        game.getGameState().getAreaGates().put(
            area1ToArea2,
            AreaGate.of(
                area1ToArea2,
                1.5f,
                1.5f,
                Vector2.of(
                    2f,
                    63f
                ),
                AreaId.of("area1"),
                area2ToArea1
            )
        );
        game.getGameState().getAreaGates().put(
            area2ToArea1,
            AreaGate.of(
                area2ToArea1,
                1.5f,
                1.5f,
                Vector2.of(
                    58f,
                    9f
                ),
                AreaId.of("area2"),
                area1ToArea2
            )
        );

        List<EnemySpawn> enemySpawns1 = EnemySpawnUtils.area1EnemySpawns();

        enemySpawns1.forEach(enemySpawn -> {
            CreatureId enemyId = CreatureId.of("Enemy_" + (int) (Math.random() * 10000000));
            game.getEntityManager().spawnEnemy(
                enemyId,
                areaId,
                enemySpawn,
                game
            );
            server.sendToAllTCP(EnemySpawnCommand.of(
                enemyId,
                areaId,
                enemySpawn
            )); // TODO: use actions instead
        });

        List<EnemySpawn> enemySpawns3 = EnemySpawnUtils.area3EnemySpawns();
        enemySpawns3.forEach(enemySpawn -> {
            CreatureId enemyId = CreatureId.of("Enemy_" + (int) (Math.random() * 10000000));
            game.getEntityManager().spawnEnemy(
                enemyId,
                AreaId.of("area3"),
                enemySpawn,
                game
            );
            server.sendToAllTCP(EnemySpawnCommand.of(
                enemyId,
                AreaId.of("area3"),
                enemySpawn
            ));
        });
    }
}
