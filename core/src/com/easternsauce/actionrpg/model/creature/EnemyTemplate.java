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
    private EnemyType enemyType;
    private Float attackDistance;
    private SkillType mainAttackSkill;
    private Set<DropTableEntry> dropTable;

    private static Set<DropTableEntry> randomDropSet = new ConcurrentSkipListSet<>(Arrays.asList(DropTableEntry.leatherArmorDrop,
                                                                                                 DropTableEntry.specialLeatherArmorDrop,
                                                                                                 DropTableEntry.hideGlovesDrop,
                                                                                                 DropTableEntry.specialHideGlovesDrop,
                                                                                                 DropTableEntry.ringmailGreavesDrop,
                                                                                                 DropTableEntry.specialRingmailGreavesDrop,
                                                                                                 DropTableEntry.ironSwordDrop,
                                                                                                 DropTableEntry.specialIronSwordDrop,
                                                                                                 DropTableEntry.boomerangDrop,
                                                                                                 DropTableEntry.shieldDrop));

    public static EnemyTemplate archer = EnemyTemplate.of(EnemyType.ARCHER, 15f, SkillType.MOB_CROSSBOW_SHOT, randomDropSet);

    public static EnemyTemplate skeleton = EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH, randomDropSet);
    public static EnemyTemplate mage = EnemyTemplate.of(EnemyType.MAGE, 15f, SkillType.MOB_MAGIC_ORB, randomDropSet);
}
