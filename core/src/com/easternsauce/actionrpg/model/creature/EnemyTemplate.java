package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.skill.SkillType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class EnemyTemplate {
    private static Set<DropTableEntry> randomDropSet = new ConcurrentSkipListSet<>(Arrays.asList(DropTableEntry.leatherArmorDrop,
        DropTableEntry.specialLeatherArmorDrop,
        DropTableEntry.hideGlovesDrop,
        DropTableEntry.specialHideGlovesDrop,
        DropTableEntry.ringmailGreavesDrop,
        DropTableEntry.specialRingmailGreavesDrop,
        DropTableEntry.ironSwordDrop,
        DropTableEntry.specialIronSwordDrop,
        DropTableEntry.boomerangDrop,
        DropTableEntry.shieldDrop,
        DropTableEntry.lifePotionDrop,
        DropTableEntry.manaPotionDrop
    ));
    public static EnemyTemplate archer = EnemyTemplate.of(EnemyType.ARCHER,
        150f,
        20f,
        randomDropSet,
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.MOB_CROSSBOW_SHOT, 100f),
            EnemySkillUseEntry.of(SkillType.POISONOUS_MIXTURE, 40f)
        ))
    );
    public static EnemyTemplate skeleton = EnemyTemplate.of(EnemyType.SKELETON,
        150f,
        4f,
        randomDropSet,
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.MOB_SWORD_SLASH, 100f),
            EnemySkillUseEntry.of(SkillType.SHIELD_GUARD, 20f)
        ))
    );
    public static EnemyTemplate mage = EnemyTemplate.of(EnemyType.MAGE,
        150f,
        20f,
        randomDropSet,
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.MOB_MAGIC_ORB, 100f),
            EnemySkillUseEntry.of(SkillType.MOB_VOLATILE_BUBBLE, 60f),
            EnemySkillUseEntry.of(SkillType.FIREBALL, 60f)
        ))
    );
    public static EnemyTemplate minos = EnemyTemplate.of(EnemyType.MINOS,
        1000f,
        8f,
        randomDropSet,
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.RICOCHET_BALLISTA, 100f),
            EnemySkillUseEntry.of(SkillType.BOSS_SWORD_SPIN, 100f),
            EnemySkillUseEntry.of(SkillType.VOLATILE_BUBBLE, 60f),
            EnemySkillUseEntry.of(SkillType.FIREBALL, 60f)
        ))
    );
    private EnemyType enemyType;
    private Float maxLife;
    private Float attackDistance;
    private Set<DropTableEntry> dropTable;
    private Set<EnemySkillUseEntry> enemySkillUseEntries;
}
