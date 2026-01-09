package com.tann.dice.gameplay.trigger.global.changeHero.effects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.trigger.global.scaffolding.HeroPosition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;

public class SetHero extends ChangeHeroEffect {
   final HeroType become;

   public SetHero(HeroType become) {
      this.become = become;
   }

   @Override
   public void affectHero(DungeonContext dc, Hero h) {
      h.levelUpTo(this.become, dc);
   }

   @Override
   public String describeForSelfBuff(HeroPosition heroPosition) {
      String hn = this.become.getName(true);
      return heroPosition == null ? "Become " + hn : "The " + heroPosition.describe().toLowerCase() + " becomes " + hn;
   }

   @Override
   public Actor makePanelActor(boolean big) {
      return new Pixl().image(Images.equalsBig, Colours.light).gap(2).image(this.become.portrait).pix();
   }
}
