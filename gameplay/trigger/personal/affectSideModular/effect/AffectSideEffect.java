package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;
import java.util.List;
import java.util.Random;

public abstract class AffectSideEffect {
   public abstract String describe();

   public abstract void affect(EntSideState var1, EntState var2, int var3, AffectSides var4, int var5);

   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      EffectDraw notAlone = new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y) {
            batch.setColor(Colours.grey);
            Draw.drawCentered(batch, Images.question, (float)(x + 8), (float)(y + 8));
         }
      };
      notAlone.markNotAlone();
      return notAlone;
   }

   public boolean showInPanel() {
      return false;
   }

   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      return null;
   }

   public String getToFrom() {
      return null;
   }

   public boolean needsGraphic() {
      return false;
   }

   public String getImageName() {
      return null;
   }

   public Actor getOverrideActor(List<AffectSideCondition> conditions) {
      return null;
   }

   public float getEffectTier(int pips, int tier) {
      return Float.NaN;
   }

   public List<Keyword> getReferencedKeywords() {
      return null;
   }

   protected String getGeneralDescription(AffectSideEffect[] affectSideEffectList) {
      return "Change";
   }

   public long getCollisionBits(Boolean player) {
      return 0L;
   }

   public boolean isRecommended(EntState sourceState, EntState targetPresent, EntState targetFuture) {
      return true;
   }

   public static AffectSideEffect makeRandom(Random r) {
      switch (r.nextInt(22)) {
         case 1:
            return new BonusForIdentical();
         case 2:
            return new ChangeToAboveType(r.nextBoolean());
         case 3:
            return new ReturnToInnate();
         case 4:
            return new CopyBaseFromHeroAbove(r.nextBoolean());
         case 5:
            return new RemoveAllKeywords();
         case 6:
            return new SetToHighest();
         case 7:
            return new MultiplyEffect(r.nextInt(4));
         case 8:
            return new NextPrime();
         case 9:
            return new SetValue(r.nextInt(6) - 2);
         case 10:
            return new ReplaceWithBlank(ChoosableType.Modifier);
         case 11:
            return new AddAllKeywords(ra(KUtils.getKeywordColours(), r));
         case 12:
         case 13:
         case 14:
            return new FlatBonus(r.nextInt(4) - 1);
         case 15:
            return new AddKeyword(ra(Keyword.values(), r));
         default:
            return new ReplaceWith(EntSidesLib.random(r, true));
      }
   }

   private static <T> T ra(List<T> l, Random r) {
      return Tann.randomElement(l, r);
   }

   public static <T> T ra(T[] vals, Random r) {
      return Tann.random(vals, r);
   }

   public boolean isIndexed() {
      return false;
   }

   public AffectSideEffect genMult(int mult) {
      return null;
   }

   public boolean isMultiplable() {
      return false;
   }

   public String hyphenTag() {
      return null;
   }

   public boolean skipMultipliable() {
      return false;
   }

   public TextureRegion overrideImage() {
      return null;
   }
}
