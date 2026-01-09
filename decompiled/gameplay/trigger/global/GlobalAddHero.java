package com.tann.dice.gameplay.trigger.global;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.lang.Words;

public class GlobalAddHero extends Global {
   final HeroType type;

   public GlobalAddHero(HeroType type) {
      this.type = type;
   }

   @Override
   public String describeForSelfBuff() {
      return "Add a " + Tann.makeEllipses(this.type.getName(true), TannFont.guessMaxTextLength(0.5F)) + " [text]to your party";
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      if (big) {
         return new EntPanelInventory(this.type.makeEnt(), false).withoutDice();
      } else {
         Pixl p = new Pixl(2).text(Words.plusString(true));
         p.image(this.type.portrait);
         return p.pix();
      }
   }

   @Override
   public boolean isOnPick() {
      return true;
   }

   @Override
   public void onPick(DungeonContext context) {
      ChoosableUtils.checkedOnChoose(this.type, context, "add hero global");
      super.onPick(context);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.NUM_HEROES;
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }
}
