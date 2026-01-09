package com.tann.dice.gameplay.trigger.global;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;

public class GlobalAllowDeadHeroSpells extends Global {
   @Override
   public boolean allowDeadAbilities() {
      return true;
   }

   @Override
   public String describeForSelfBuff() {
      return "You can use abilities from defeated heroes";
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Actor ia = new ImageActor(Images.tr_manaSpellFull, Colours.blue);
      Group g = Tann.makeGroup(ia);
      ImageActor skull = new ImageActor(Images.skull, Colours.purple);
      g.addActor(skull);
      skull.setPosition((int)(g.getWidth() / 2.0F - skull.getWidth() / 2.0F), (int)((ia.getHeight() - skull.getHeight()) / 2.0F));
      return g;
   }
}
