package com.mygdx.game.model.creature;

import com.mygdx.game.model.item.ItemTemplate;
import com.mygdx.game.model.skill.SkillType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

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

    static {
        magicGrantedSkillDrops.put(SkillType.FIREBALL, 100);
        magicGrantedSkillDrops.put(SkillType.LIGHTNING, 70);

        specialMagicGrantedSkillDrops.put(SkillType.FIREBALL, 100);
        specialMagicGrantedSkillDrops.put(SkillType.LIGHTNING, 80);
        specialMagicGrantedSkillDrops.put(SkillType.MAGIC_ORB, 70);
        specialMagicGrantedSkillDrops.put(SkillType.VOLATILE_BUBBLE, 70);

        movementGrantedSkillDrops.put(SkillType.TELEPORT, 100);

        rangedGrantedSkillDrops.put(SkillType.RICOCHET_BALLISTA, 100);

        shieldGrantedSkillDrops.put(SkillType.SUMMON_SHIELD, 100);

    }

    public static DropTableEntry
            leatherArmorDrop =
            DropTableEntry.of(0.25f,
                              ItemTemplate.templates.get("leatherArmor"),
                              0.5f,
                              magicGrantedSkillDrops);

    public static DropTableEntry
            specialLeatherArmorDrop =
            DropTableEntry.of(0.25f,
                              ItemTemplate.templates.get("leatherArmor"),
                              1f,
                              specialMagicGrantedSkillDrops);
    public static DropTableEntry
            hideGlovesDrop =
            DropTableEntry.of(0.2f,
                              ItemTemplate.templates.get("hideGloves"),
                              0.5f,
                              new ConcurrentSkipListMap<>());

    public static DropTableEntry
            specialHideGlovesDrop =
            DropTableEntry.of(0.25f,
                              ItemTemplate.templates.get("hideGloves"),
                              0.5f,
                              rangedGrantedSkillDrops);
    public static DropTableEntry
            ringmailGreavesDrop =
            DropTableEntry.of(0.25f,
                              ItemTemplate.templates.get("ringmailGreaves"),
                              0f,
                              new ConcurrentSkipListMap<>());

    public static DropTableEntry
            ironSwordDrop =
            DropTableEntry.of(0.1f,
                              ItemTemplate.templates.get("ironSword"),
                              0.5f,
                              movementGrantedSkillDrops);

    public static DropTableEntry
            boomerangDrop =
            DropTableEntry.of(0.1f,
                              ItemTemplate.templates.get("boomerang"),
                              0f,
                              new ConcurrentSkipListMap<>());

    public static DropTableEntry
            shieldDrop =
            DropTableEntry.of(0.1f,
                              ItemTemplate.templates.get("woodenShield"),
                              0f,
                              shieldGrantedSkillDrops);


}
