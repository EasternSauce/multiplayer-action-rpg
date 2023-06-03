package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.item.ItemTemplate;
import com.easternsauce.actionrpg.model.skill.SkillType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class DropTableEntry implements Comparable<DropTableEntry> {
    Float dropChance;
    ItemTemplate template;
    Float grantedSkillChance;
    Map<SkillType, Integer> grantedSkillWeights;

    @Override
    public int compareTo(DropTableEntry o) {
        return this.toString().compareTo(o.toString());
    }

    private static Map<SkillType, Integer> magicGrantedSkillDrops = new ConcurrentSkipListMap<>();
    private static Map<SkillType, Integer> specialMagicGrantedSkillDrops = new ConcurrentSkipListMap<>();
    private static Map<SkillType, Integer> movementGrantedSkillDrops = new ConcurrentSkipListMap<>();

    private static Map<SkillType, Integer> rangedGrantedSkillDrops = new ConcurrentSkipListMap<>();

    private static Map<SkillType, Integer> shieldGrantedSkillDrops = new ConcurrentSkipListMap<>();
    private static Map<SkillType, Integer> ironSwordGrantedSkillDrops = new ConcurrentSkipListMap<>();

    static {
        magicGrantedSkillDrops.put(SkillType.FIREBALL, 100);
        magicGrantedSkillDrops.put(SkillType.LIGHTNING, 70);

        specialMagicGrantedSkillDrops.put(SkillType.FIREBALL, 100);
        specialMagicGrantedSkillDrops.put(SkillType.LIGHTNING, 80);
        specialMagicGrantedSkillDrops.put(SkillType.MAGIC_ORB, 70);
        specialMagicGrantedSkillDrops.put(SkillType.VOLATILE_BUBBLE, 70);

        movementGrantedSkillDrops.put(SkillType.TELEPORT, 100);

        rangedGrantedSkillDrops.put(SkillType.RICOCHET_BALLISTA, 100);
        rangedGrantedSkillDrops.put(SkillType.SUMMON_GHOSTS, 80);

        shieldGrantedSkillDrops.put(SkillType.SUMMON_GUARD, 100);

        ironSwordGrantedSkillDrops.put(SkillType.SWORD_SPIN, 100);

    }

    public static DropTableEntry leatherArmorDrop = DropTableEntry.of(0.05f,
                                                                      ItemTemplate.templates.get("leatherArmor"),
                                                                      1f,
                                                                      magicGrantedSkillDrops);

    public static DropTableEntry specialLeatherArmorDrop = DropTableEntry.of(1f,
                                                                             ItemTemplate.templates.get("leatherArmor"),
                                                                             1f,
                                                                             specialMagicGrantedSkillDrops);
    public static DropTableEntry hideGlovesDrop = DropTableEntry.of(0.05f,
                                                                    ItemTemplate.templates.get("hideGloves"),
                                                                    0f,
                                                                    new ConcurrentSkipListMap<>());

    public static DropTableEntry specialHideGlovesDrop = DropTableEntry.of(1f,
                                                                           ItemTemplate.templates.get("hideGloves"),
                                                                           1f,
                                                                           rangedGrantedSkillDrops);

    public static DropTableEntry ringmailGreavesDrop = DropTableEntry.of(0.05f,
                                                                         ItemTemplate.templates.get("ringmailGreaves"),
                                                                         0f,
                                                                         new ConcurrentSkipListMap<>());

    public static DropTableEntry specialRingmailGreavesDrop = DropTableEntry.of(1f,
                                                                                ItemTemplate.templates.get("ringmailGreaves"),
                                                                                1f,
                                                                                movementGrantedSkillDrops);

    public static DropTableEntry ironSwordDrop = DropTableEntry.of(0.05f,
                                                                   ItemTemplate.templates.get("ironSword"),
                                                                   0f,
                                                                   new ConcurrentSkipListMap<>());

    public static DropTableEntry specialIronSwordDrop = DropTableEntry.of(1f,
                                                                          ItemTemplate.templates.get("ironSword"),
                                                                          1f,
                                                                          ironSwordGrantedSkillDrops);

    public static DropTableEntry boomerangDrop = DropTableEntry.of(1f,
                                                                   ItemTemplate.templates.get("boomerang"),
                                                                   0f,
                                                                   new ConcurrentSkipListMap<>());

    public static DropTableEntry shieldDrop = DropTableEntry.of(1f,
                                                                ItemTemplate.templates.get("woodenShield"),
                                                                1f,
                                                                shieldGrantedSkillDrops);

    public static DropTableEntry lifePotionDrop = DropTableEntry.of(1f,
                                                                    ItemTemplate.templates.get("lifePotion"),
                                                                    0f,
                                                                    new ConcurrentSkipListMap<>());
    public static DropTableEntry manaPotionDrop = DropTableEntry.of(1f,
                                                                    ItemTemplate.templates.get("manaPotion"),
                                                                    0f,
                                                                    new ConcurrentSkipListMap<>());

}
