package com.tann.dice.gameplay.trigger.global.linked;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.gameplay.trigger.global.scaffolding.HeroPosition;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.Colours;

public class GlobalPositional extends GlobalLinked {
   final HeroPosition heroPosition;
   public final Personal personal;

   public GlobalPositional(HeroPosition heroPosition, Personal personal) {
      super(personal);
      this.personal = personal;
      this.heroPosition = heroPosition;
   }

   @Override
   public String describeForSelfBuff() {
      boolean plural = this.heroPosition.getRawPosition().length > 1;
      String end = this.personal.describeForSelfBuff();
      if (!plural) {
         end = end.replaceAll("start", "starts");
      }

      return this.heroPosition.describe() + ":[n]" + end;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return DipPanel.makeSidePanelGroup(big, this.heroPosition.makeActor(), this.personal, Colours.green);
   }

   @Override
   public Personal getLinkedPersonal(EntState entState) {
      if (!entState.getEnt().isPlayer()) {
         return super.getLinkedPersonal(entState);
      } else {
         Snapshot s = entState.getSnapshot();
         return s != null && this.heroPosition.getFromPosition(entState.getSnapshot().getEntities(entState.isPlayer(), null)).contains(entState.getEnt())
            ? this.personal
            : super.getLinkedPersonal(entState);
      }
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.personal.getCollisionBits(true) | this.heroPosition.getCollisionBit();
   }

   @Override
   public String hyphenTag() {
      return ModifierUtils.hyphenTag(this.personal.hyphenTag(), "" + this.heroPosition.getRawPosition().length);
   }

   @Override
   public GlobalLinked splice(Personal newCenter) {
      return new GlobalPositional(this.heroPosition, newCenter);
   }
}
