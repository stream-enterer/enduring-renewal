package com.tann.dice.gameplay.trigger.global.changeHero.effects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.scaffolding.HeroPosition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;

public class KillHero extends ChangeHeroEffect {
   @Override
   public void affectHero(DungeonContext dc, Hero h) {
      dc.getParty().kill(h, dc);
   }

   @Override
   public String describeForSelfBuff(HeroPosition heroPosition) {
      return "No " + heroPosition.describe().toLowerCase();
   }

   @Override
   public Actor makePanelActor(boolean big) {
      return new ImageActor(Images.eq_skullWhite, Colours.light);
   }

   @Override
   public boolean needsReset() {
      return true;
   }

   @Override
   public long getCollisionBit(HeroPosition heroPosition) {
      long result = Collision.NUM_HEROES;
      if (Tann.contains(heroPosition.getRawPosition(), 4)
         || Tann.contains(heroPosition.getRawPosition(), 3)
         || Tann.contains(heroPosition.getRawPosition(), -1)
         || Tann.contains(heroPosition.getRawPosition(), -2)) {
         result |= Collision.SPELL;
      }

      return result;
   }
}
