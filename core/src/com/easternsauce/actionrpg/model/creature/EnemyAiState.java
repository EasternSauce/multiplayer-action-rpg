package com.easternsauce.actionrpg.model.creature;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum EnemyAiState {

    RESTING,
    ALERTED,
    AGGRESSIVE,

    KEEPING_DISTANCE
}
