package com.tann.dice.gameplay.trigger.personal.linked.stateCondition;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.ui.HpGrid;

public class GenericStateCondition {
   private final StateConditionType stateConditionType;

   public GenericStateCondition(StateConditionType stateConditionType) {
      this.stateConditionType = stateConditionType;
   }

   public StateConditionType getStateConditionType() {
      return this.stateConditionType;
   }

   public boolean isValid(EntState es) {
      return this.stateConditionType.isValid(es);
   }

   public String describe() {
      switch (this.stateConditionType) {
         case HalfOrLessHP:
            return "If I am on half or less hp";
         default:
            return "Undescribed type: " + this.stateConditionType;
      }
   }

   public String describeShort() {
      return this.stateConditionType.describeShort();
   }

   public Actor getPrecon() {
      switch (this.stateConditionType) {
         case HalfOrLessHP:
            return new Pixl().text("[red]<=").gap(3).actor(HpGrid.make(5, 10)).pix();
         case FullHP:
            return HpGrid.make(10, 10);
         case GainedNoShields:
            return new EffBill().shield(1).bEff().getBasicImage("0");
         case Used:
            return new Pixl().text("[text]used").pix();
         case DiedLasFight:
            return new Pixl().text("[grey]<-").gap(1).image(Images.eq_skullWhite, Colours.light).pix();
         default:
            return new Pixl().text("[grey]" + this.stateConditionType).pix();
      }
   }

   public String getInvalidString(Eff eff) {
      return this.stateConditionType.getInvalidString(eff);
   }

   public long getCollision() {
      switch (this.stateConditionType) {
         case DiedLasFight:
         case Died:
            return Collision.death(true);
         default:
            return 0L;
      }
   }
}
