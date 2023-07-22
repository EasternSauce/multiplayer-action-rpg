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
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.MOB_CROSSBOW_SHOT, 22f, 100f),
            EnemySkillUseEntry.of(SkillType.MOB_POISONOUS_MIXTURE, 18f, 40f)
        ))
    );
    public static EnemyTemplate skeleton = EnemyTemplate.of(EnemyType.SKELETON,
        200f,
        4f,
        10.5f,
        randomDropSet,
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.MOB_SWORD_SLASH, 5f, 100f),
            EnemySkillUseEntry.of(SkillType.SHIELD_GUARD, 20f, 5f)
        ))
    );
    public static EnemyTemplate mage = EnemyTemplate.of(EnemyType.MAGE,
        120f,
        20f,
        11f,
        randomDropSet,
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.MOB_MAGIC_ORB, 20f, 100f),
            EnemySkillUseEntry.of(SkillType.MAGE_TELEPORT_COMBO, 20f, 25f)

        ))
    );
    public static EnemyTemplate minos = EnemyTemplate.of(EnemyType.MINOS,
        2300f,
        6f,
        16f,
        randomDropSet,
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.BOSS_SWORD_SLASH, 9f, 200f),
            EnemySkillUseEntry.of(SkillType.BOSS_SWORD_SPIN, 6f, 100f)
        ))
    );
    public static EnemyTemplate serpent = EnemyTemplate.of(EnemyType.SERPENT,
        1500f,
        7f,
        14f,
        randomDropSet,
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.ICE_SPEAR_RAMPAGE, 15f, 50f),
            EnemySkillUseEntry.of(SkillType.MOB_RING_OF_FIRE, 7f, 150f)
        ))
    );
    public static EnemyTemplate sludge = EnemyTemplate.of(EnemyType.SLUDGE,
        400f,
        12f,
        6f,
        randomDropSet,
        new ConcurrentSkipListSet<>(Arrays.asList(EnemySkillUseEntry.of(SkillType.DIG_TUNNEL, 22f, 100f),
            EnemySkillUseEntry.of(SkillType.SUMMON_GHOSTS, 10f, 60f)
        ))
    );

    @Getter
    private EnemyType enemyType;
    @Getter
    private Float maxLife;
    @Getter
    private Float walkUpRange;
    @Getter
    private Float speed;
    @Getter
    private Set<DropTableEntry> dropTable;
    @Getter
    private Set<EnemySkillUseEntry> enemySkillUseEntries;
}
