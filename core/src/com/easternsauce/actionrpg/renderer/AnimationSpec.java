package com.easternsauce.actionrpg.renderer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AnimationSpec {
    Integer frameWidth;
    Integer frameHeight;
    Float realWidth;
    Float realHeight;
    Float frameDuration;
    Integer frameCount;
    String atlasRegionName;
    Boolean isLooping;
}
