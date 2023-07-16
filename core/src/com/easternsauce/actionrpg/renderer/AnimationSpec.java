package com.easternsauce.actionrpg.renderer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AnimationSpec {
    private Integer frameWidth;
    private Integer frameHeight;
    private Float realWidth;
    private Float realHeight;
    private Float frameDuration;
    private Integer frameCount;
    private String atlasRegionName;
    private Boolean isLooping;
}
