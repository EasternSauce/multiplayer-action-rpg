package com.mygdx.game.model.creature;

import com.mygdx.game.model.skill.SkillType;
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
    EnemyType enemyType;
    Float attackDistance;
    SkillType mainAttackSkill;
    Set<DropTableEntry> dropTable;

    public static EnemyTemplate
            archer =
            EnemyTemplate.of(EnemyType.ARCHER, 15f, SkillType.CROSSBOW_BOLT, new ConcurrentSkipListSet<>(
                    Arrays.asList(
                            DropTableEntry.leatherArmorDrop,
                            DropTableEntry.specialHideGlovesDrop,
                            DropTableEntry.ringmailGreavesDrop,
                            DropTableEntry.boomerangDrop)));

    public static EnemyTemplate
            skeleton =
            EnemyTemplate.of(EnemyType.SKELETON, 3f, SkillType.SWORD_SLASH, new ConcurrentSkipListSet<>(Arrays.asList(
                    DropTableEntry.leatherArmorDrop,
                    DropTableEntry.hideGlovesDrop,
                    DropTableEntry.ringmailGreavesDrop,
                    DropTableEntry.ironSwordDrop,
                    DropTableEntry.shieldDrop)));
    public static EnemyTemplate
            mage =
            EnemyTemplate.of(EnemyType.MAGE, 15f, SkillType.SLOW_MAGIC_ORB, new ConcurrentSkipListSet<>(Arrays.asList(
                    DropTableEntry.specialLeatherArmorDrop,
                    DropTableEntry.hideGlovesDrop,
                    DropTableEntry.ringmailGreavesDrop)));
}
