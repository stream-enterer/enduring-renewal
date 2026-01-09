package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItemGenerated;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.GenericView;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectSideEffect;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.GenericStateCondition;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;
import java.util.Random;

public abstract class AffectSideCondition {
   public abstract boolean validFor(EntSideState var1, EntState var2, int var3);

   public abstract String describe();

   public boolean isPlural() {
      return false;
   }

   public EffectDraw getAddDraw() {
      return null;
   }

   public int indexValid(EntSideState sideState, EntState owner) {
      return -1;
   }

   public boolean isAfterSides() {
      return false;
   }

   public boolean needsGraphic() {
      return false;
   }

   public boolean hasSideImage() {
      return false;
   }

   public boolean showInPanel() {
      return false;
   }

   public String getImageName() {
      return null;
   }

   public Actor getPrecon() {
      return null;
   }

   public boolean overrideDoesntNeedPrecon() {
      return false;
   }

   public boolean needsArrow() {
      return false;
   }

   public GenericView getActor() {
      return null;
   }

   public boolean describeFirst() {
      return false;
   }

   public boolean sayOtherForSharpWitEtc() {
      return false;
   }

   public long getCollisionBits(Boolean player) {
      return 0L;
   }

   public static AffectSideCondition makeRandom(Random r) {
      Keyword rk = AffectSideEffect.ra(Keyword.values(), r);
      switch (r.nextInt(10)) {
         case 0:
            return new EvennessCondition(r.nextBoolean());
         case 1:
            return new HasKeyword(rk);
         case 2:
            return new HighestCondition(r.nextBoolean());
         case 3:
            return new TextureCondition(EntSidesLib.random(new Random(PipeItemGenerated.ritemxSeed)), "these");
         case 4:
            return new NotCondition(makeRandom(r));
         case 5:
            return new OrMoreCondition(r.nextInt(10));
         case 6:
            return new PrimeCondition();
         case 7:
            return new SpecificSidesCondition(AffectSideEffect.ra(SpecificSidesType.values(), r));
         case 8:
            return new StateConditionSideCondition(new GenericStateCondition(AffectSideEffect.ra(StateConditionType.values(), r)));
         case 9:
            return new HasValue(r.nextBoolean());
         default:
            return new ExactlyCondition(r.nextInt(5) - 1);
      }
   }

   public String hyphenTag() {
      return null;
   }
}
