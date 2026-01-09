package com.tann.dice.gameplay.trigger.global.spell.change;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.util.ui.TextWriter;

public class MultiplyChange extends IntegerChange {
   private final int multiple;

   public MultiplyChange(int multiple) {
      this.multiple = multiple;
   }

   @Override
   public int affect(int src) {
      return src * this.multiple;
   }

   @Override
   public String describe() {
      return "x" + this.multiple;
   }

   @Override
   public Actor makeActor(TextureRegion region) {
      return new TextWriter("[white][img] [red]x" + this.multiple, region);
   }
}
