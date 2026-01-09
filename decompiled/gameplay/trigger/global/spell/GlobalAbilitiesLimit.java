package com.tann.dice.gameplay.trigger.global.spell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;

public class GlobalAbilitiesLimit extends Global {
   final int limit;

   public GlobalAbilitiesLimit(int limit) {
      this.limit = limit;
   }

   @Override
   public String describeForSelfBuff() {
      return this.limit == 0 ? "You cannot use abilities" : "Maximum of " + this.limit + " " + Words.plural("ability", this.limit) + " uses per turn";
   }

   @Override
   public boolean canUseAbility(Ability ability, Snapshot snapshot) {
      return snapshot.getNumAbilitiesUsed() < this.limit;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.SPELL;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      int gap = 3;
      return this.limit == 0
         ? Tann.combineActors(new ImageActor(Images.eq_triggerSpell), new ImageActor(Images.ui_cross, Colours.red))
         : new Pixl().image(Images.eq_triggerSpell).gap(3).text("[red]x" + this.limit).pix();
   }

   @Override
   public String hyphenTag() {
      return this.limit + "";
   }
}
