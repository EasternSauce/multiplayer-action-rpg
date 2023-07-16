package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.skill.SkillType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class EnemyTemplate {
    private static final Set<DropTableEntry> randomDropSet = new ConcurrentSkipListSet<>(Arrays.asList(DropTableEntry.leatherArmorDrop,
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
        11f,
        randomDropSet,
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.MOB_CROSSBOW_SHOT, 100f),
            EnemySkillUseEntry.of(SkillType.POISONOUS_MIXTURE, 40f)
        ))
    );
    public static EnemyTemplate skeleton = EnemyTemplate.of(EnemyType.SKELETON,
        200f,
        4f,
        11f,
        randomDropSet,
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.MOB_SWORD_SLASH, 100f),
            EnemySkillUseEntry.of(SkillType.SHIELD_GUARD, 20f)
        ))
    );
    public static EnemyTemplate mage = EnemyTemplate.of(EnemyType.MAGE,
        120f,
        20f,
        11f,
        randomDropSet,
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.MOB_MAGIC_ORB, 100f),
            EnemySkillUseEntry.of(SkillType.MOB_VOLATILE_BUBBLE, 60f),
            EnemySkillUseEntry.of(SkillType.FIREBALL, 60f),
            EnemySkillUseEntry.of(SkillType.MAGE_TELEPORT_COMBO, 25f)

        ))
    );
    public static EnemyTemplate minos = EnemyTemplate.of(EnemyType.MINOS,
        1000f,
        8f,
        11f,
        randomDropSet,
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.RICOCHET_BALLISTA, 100f),
            EnemySkillUseEntry.of(SkillType.BOSS_SWORD_SPIN, 100f),
            EnemySkillUseEntry.of(SkillType.VOLATILE_BUBBLE, 60f),
            EnemySkillUseEntry.of(SkillType.FIREBALL, 60f)
        ))
    );
    public static EnemyTemplate serpent = EnemyTemplate.of(EnemyType.SERPENT,
        1000f,
        8f,
        20f,
        randomDropSet,
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.RICOCHET_BALLISTA, 100f),
            EnemySkillUseEntry.of(SkillType.ICE_SPEAR_RAMPAGE, 100f),
            EnemySkillUseEntry.of(SkillType.VOLATILE_BUBBLE, 60f),
            EnemySkillUseEntry.of(SkillType.FIREBALL, 60f)
        ))
    );

    @Getter
    private EnemyType enemyType;
    @Getter
    private Float maxLife;
    @Getter
    private Float attackDistance;
    @Getter
    private Float speed;
    @Getter
    private Set<DropTableEntry> dropTable;
    @Getter
    private Set<EnemySkillUseEntry> enemySkillUseEntries;
}
