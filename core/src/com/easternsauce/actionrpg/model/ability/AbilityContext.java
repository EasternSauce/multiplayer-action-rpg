package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullAreaId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class AbilityContext {
  private EntityId<Creature> creatureId = NullCreatureId.of();
  private EntityId<Area> areaId = NullAreaId.of();
  private SkillType skillType;
  private Vector2 dirVector;
}
