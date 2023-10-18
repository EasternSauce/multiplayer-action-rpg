package com.easternsauce.actionrpg.model.id;

import lombok.Getter;

public abstract class EntityId implements Comparable<EntityId> {
    @Getter
    private String value;

    @Override
    public int compareTo(EntityId o) {
        return getValue().compareTo(o.getValue());
    }
}
