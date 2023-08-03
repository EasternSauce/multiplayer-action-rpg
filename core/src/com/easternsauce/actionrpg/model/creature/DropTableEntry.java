package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.item.ItemTemplate;
import com.easternsauce.actionrpg.model.skill.SkillType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class DropTableEntry implements Comparable<DropTableEntry> {
    public static DropTableEntry hideGlovesDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("hideGloves"),
        0f,
        new ConcurrentSkipListMap<>()
    );
    public static DropTableEntry ringmailGreavesDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("ringmailGreaves"),
        0f,
        new ConcurrentSkipListMap<>()
    );
    public static DropTableEntry ironSwordDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("ironSword"),
        0f,
        new ConcurrentSkipListMap<>()
    );
    public static DropTableEntry crossbowDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("crossbow"),
        0f,
        new ConcurrentSkipListMap<>()
    );
    public static DropTableEntry boomerangDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("boomerang"),
        0f,
        new ConcurrentSkipListMap<>()
    );
    public static DropTableEntry lifePotionDrop = DropTableEntry.of(0.9f,
        ItemTemplate.templates.get("lifePotion"),
        0f,
        new ConcurrentSkipListMap<>()
    );
    public static DropTableEntry unlikelyLifePotionDrop = DropTableEntry.of(0.5f,
        ItemTemplate.templates.get("lifePotion"),
        0f,
        new ConcurrentSkipListMap<>()
    );
    public static DropTableEntry manaPotionDrop = DropTableEntry.of(0.9f,
        ItemTemplate.templates.get("manaPotion"),
        0f,
        new ConcurrentSkipListMap<>()
    );
    public static DropTableEntry leatherArmorDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("leatherArmor"),
        0f,
        new ConcurrentSkipListMap<>()
    );
    public static DropTableEntry tierOneMagicLeatherArmorDrop = DropTableEntry.of(1f,
        ItemTemplate.templates.get("leatherArmor"),
        1f,
        Stream.of(new AbstractMap.SimpleEntry<>(SkillType.METEOR, 100),
            new AbstractMap.SimpleEntry<>(SkillType.EMERALD_SPIN, 100),
            new AbstractMap.SimpleEntry<>(SkillType.RING_OF_FIRE, 100),
            new AbstractMap.SimpleEntry<>(SkillType.POISONOUS_MIXTURE, 100),
            new AbstractMap.SimpleEntry<>(SkillType.TUNNEL_DIG, 100)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry tierTwoMagicLeatherArmorDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("leatherArmor"),
        1f,
        Stream.of(new AbstractMap.SimpleEntry<>(SkillType.FIREBALL, 100),
            new AbstractMap.SimpleEntry<>(SkillType.LIGHTNING, 80),
            new AbstractMap.SimpleEntry<>(SkillType.MAGIC_ORB, 70),
            new AbstractMap.SimpleEntry<>(SkillType.VOLATILE_BUBBLE, 70)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry bossTierTwoMagicLeatherArmorDrop = DropTableEntry.of(0.35f,
        ItemTemplate.templates.get("leatherArmor"),
        1f,
        Stream.of(new AbstractMap.SimpleEntry<>(SkillType.FIREBALL, 100),
            new AbstractMap.SimpleEntry<>(SkillType.LIGHTNING, 80),
            new AbstractMap.SimpleEntry<>(SkillType.MAGIC_ORB, 70),
            new AbstractMap.SimpleEntry<>(SkillType.VOLATILE_BUBBLE, 70)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry tierThreeMagicLeatherArmorDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("leatherArmor"),
        1f,
        Stream.of(new AbstractMap.SimpleEntry<>(SkillType.LIGHTNING, 80),
            new AbstractMap.SimpleEntry<>(SkillType.VOLATILE_BUBBLE, 70),
            new AbstractMap.SimpleEntry<>(SkillType.RING_OF_FIRE, 70),
            new AbstractMap.SimpleEntry<>(SkillType.TELEPORT, 70)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry bossTierThreeMagicLeatherArmorDrop = DropTableEntry.of(0.35f,
        ItemTemplate.templates.get("leatherArmor"),
        1f,
        Stream.of(new AbstractMap.SimpleEntry<>(SkillType.LIGHTNING, 80),
            new AbstractMap.SimpleEntry<>(SkillType.VOLATILE_BUBBLE, 70),
            new AbstractMap.SimpleEntry<>(SkillType.RING_OF_FIRE, 70),
            new AbstractMap.SimpleEntry<>(SkillType.TELEPORT, 70)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry tierOneMagicRingmailGreavesDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("ringmailGreaves"),
        1f,
        Stream
            .of(new AbstractMap.SimpleEntry<>(SkillType.TELEPORT, 100))
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry bossTierOneMagicRingmailGreavesDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("ringmailGreaves"),
        1f,
        Stream
            .of(new AbstractMap.SimpleEntry<>(SkillType.TELEPORT, 100))
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry tierTwoMagicRingmailGreavesDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("ringmailGreaves"),
        1f,
        Stream
            .of(new AbstractMap.SimpleEntry<>(SkillType.TELEPORT, 100),
                new AbstractMap.SimpleEntry<>(SkillType.DASH, 70)
            )
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry bossTierTwoMagicRingmailGreavesDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("ringmailGreaves"),
        1f,
        Stream
            .of(new AbstractMap.SimpleEntry<>(SkillType.TELEPORT, 100),
                new AbstractMap.SimpleEntry<>(SkillType.DASH, 70)
            )
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry tierOneMagicHideGlovesDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("hideGloves"),
        1f,
        Stream.of(new AbstractMap.SimpleEntry<>(SkillType.RICOCHET_BALLISTA, 100),
            new AbstractMap.SimpleEntry<>(SkillType.SUMMON_GHOSTS, 80)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry bossTierOneMagicHideGlovesDrop = DropTableEntry.of(0.35f,
        ItemTemplate.templates.get("hideGloves"),
        1f,
        Stream.of(new AbstractMap.SimpleEntry<>(SkillType.RICOCHET_BALLISTA, 100),
            new AbstractMap.SimpleEntry<>(SkillType.SUMMON_GHOSTS, 80)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry tierTwoMagicHideGlovesDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("hideGloves"),
        1f,
        Stream.of(new AbstractMap.SimpleEntry<>(SkillType.RICOCHET_BALLISTA, 100),
            new AbstractMap.SimpleEntry<>(SkillType.SUMMON_GHOSTS, 80),
            new AbstractMap.SimpleEntry<>(SkillType.EMERALD_SPIN, 80)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry bossTierTwoMagicHideGlovesDrop = DropTableEntry.of(0.35f,
        ItemTemplate.templates.get("hideGloves"),
        1f,
        Stream.of(new AbstractMap.SimpleEntry<>(SkillType.RICOCHET_BALLISTA, 100),
            new AbstractMap.SimpleEntry<>(SkillType.SUMMON_GHOSTS, 80),
            new AbstractMap.SimpleEntry<>(SkillType.EMERALD_SPIN, 80)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry tierThreeMagicHideGlovesDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("hideGloves"),
        1f,
        Stream.of(new AbstractMap.SimpleEntry<>(SkillType.EMERALD_SPIN, 80),
            new AbstractMap.SimpleEntry<>(SkillType.POISONOUS_MIXTURE, 80)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry bossTierThreeMagicHideGlovesDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("hideGloves"),
        1f,
        Stream.of(new AbstractMap.SimpleEntry<>(SkillType.EMERALD_SPIN, 80),
            new AbstractMap.SimpleEntry<>(SkillType.POISONOUS_MIXTURE, 80)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry shieldDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("woodenShield"),
        1f,
        Stream
            .of(new AbstractMap.SimpleEntry<>(SkillType.SHIELD_GUARD, 100))
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );
    public static DropTableEntry tierOneMagicIronSwordDrop = DropTableEntry.of(0.1f,
        ItemTemplate.templates.get("ironSword"),
        1f,
        Stream
            .of(new AbstractMap.SimpleEntry<>(SkillType.SWORD_SPIN, 100))
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );

    @Getter
    Float dropChance;
    @Getter
    ItemTemplate template;
    @Getter
    Float grantedSkillChance;
    @Getter
    Map<SkillType, Integer> grantedSkillWeights;

    @Override
    public int compareTo(DropTableEntry o) {
        return this.toString().compareTo(o.toString());
    }

}
