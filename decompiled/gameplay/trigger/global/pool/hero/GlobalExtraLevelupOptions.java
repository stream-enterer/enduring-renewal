package com.tann.dice.gameplay.trigger.global.pool.hero;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import java.util.ArrayList;
import java.util.List;

public class GlobalExtraLevelupOptions extends Global {
   private final List<HeroType> extras;

   public GlobalExtraLevelupOptions(List<HeroType> extras) {
      this.extras = extras;
   }

   @Override
   public List<HeroType> getExtraLevelupOptions(HeroCol col, Integer newTier) {
      List<HeroType> result = new ArrayList<>();

      for (HeroType option : this.extras) {
         if ((col == null || option.heroCol == col) && (newTier == null || option.level == newTier)) {
            result.add(option);
         }
      }

      return result;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl(3, 3).border(Colours.yellow);
      p.text("Hero Pool +").row();
      if (this.extras.size() > 20) {
         big = false;
      }

      if (big) {
         for (HeroType option : this.extras) {
            p.actor(option.makeUnlockActor(false), 100.0F);
         }
      } else {
         p.text(this.extras.size());
      }

      return p.pix();
   }

   @Override
   public String describeForSelfBuff() {
      return "Add " + this.extras.size() + " heroes to the pool";
   }
}
