package com.tann.dice.gameplay.trigger.global;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;

public class GlobalMaxMana extends Global {
   final int bonus;

   public GlobalMaxMana(int bonus) {
      this.bonus = bonus;
   }

   @Override
   public int affectMaxMana(int max) {
      return max + this.bonus;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl(2);
      p.text(Words.plusString(this.bonus));
      if (this.bonus < 10) {
         for (int i = 0; i < Math.abs(this.bonus); i++) {
            p.image(Images.manaBorder, this.bonus >= 0 ? Colours.blue : Colours.red);
         }
      } else {
         p.text(this.bonus).gap(1).image(Images.manaBorder, this.bonus >= 0 ? Colours.blue : Colours.red);
      }

      return p.pix();
   }

   @Override
   public String describeForSelfBuff() {
      return Tann.delta(this.bonus) + " max stored " + Words.manaString();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.SPELL;
   }
}
