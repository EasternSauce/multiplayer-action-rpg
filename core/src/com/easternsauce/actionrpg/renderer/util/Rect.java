package com.easternsauce.actionrpg.renderer.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class Rect {
  private Float x;
  private Float y;
  private Float width;
  private Float height;

  public boolean contains(float x, float y) {
    return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
  }
}
