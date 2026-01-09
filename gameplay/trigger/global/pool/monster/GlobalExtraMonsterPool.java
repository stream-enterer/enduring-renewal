package com.tann.dice.gameplay.trigger.global.pool.monster;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import java.util.List;

public class GlobalExtraMonsterPool extends Global {
   private final List<MonsterType> extras;

   public GlobalExtraMonsterPool(List<MonsterType> extras) {
      this.extras = extras;
   }

   @Override
   public void affectMonsterPool(List<MonsterType> results) {
      results.addAll(this.extras);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl(3, 3).border(Colours.yellow);
      p.text("[purple]Monster Pool +").row();
      if (this.extras.size() > 60) {
         big = false;
      }

      if (big) {
         for (MonsterType option : this.extras) {
            p.actor(option.makeUnlockActor(false), 100.0F);
         }
      } else {
         p.text(this.extras.size());
      }

      return p.pix();
   }

   @Override
   public String describeForSelfBuff() {
      return "Add " + this.extras.size() + " monsters to the pool";
   }
}
