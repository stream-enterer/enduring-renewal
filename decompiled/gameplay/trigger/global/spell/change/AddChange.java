package com.tann.dice.gameplay.trigger.global.spell.change;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;

public class AddChange extends IntegerChange {
   final int delta;

   public AddChange(int delta) {
      this.delta = delta;
   }

   @Override
   public int affect(int src) {
      return src + this.delta;
   }

   @Override
   public String describe() {
      return Tann.delta(this.delta);
   }

   @Override
   public Actor makeActor(TextureRegion region) {
      return new TextWriter("[white][img] " + (this.delta > 0 ? "[red][plus]" : "[green][minus]") + "[p]" + Math.abs(this.delta), region);
   }
}
