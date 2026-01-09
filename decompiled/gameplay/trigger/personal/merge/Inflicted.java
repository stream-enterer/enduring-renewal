package com.tann.dice.gameplay.trigger.personal.merge;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.Cleansed;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.tp.TP;
import java.util.Arrays;
import java.util.List;

public class Inflicted extends Merge {
   final Keyword inflict;

   public Inflicted(Keyword inflict) {
      this.inflict = inflict;
   }

   @Override
   public void affectSide(EntSideState sideState, EntState owner, int triggerIndex) {
      sideState.getCalculatedEffect().addKeyword(this.inflict);
   }

   public static String debuffString() {
      return "[red]Inflicted[cu]";
   }

   @Override
   protected TextureRegion overrideImage() {
      return KUtils.makePlaceholderCorner(this.inflict);
   }

   @Override
   public boolean showInDiePanel() {
      return true;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public Cleansed.CleanseType getCleanseType() {
      return Cleansed.CleanseType.Inflict;
   }

   @Override
   public String describeForSelfBuff() {
      return debuffString()
         + "[red]-[cu]"
         + this.inflict.getColourTaggedString()
         + " [grey](add "
         + this.inflict.getColourTaggedString()
         + " to all sides)[cu]";
   }

   @Override
   protected boolean removeGiveFromGiveText() {
      return true;
   }

   @Override
   public TP<Integer, Boolean> cleanseBy(int cleanseAmt) {
      return new TP<>(1, true);
   }

   @Override
   public void merge(Personal personal) {
   }

   @Override
   protected boolean canMergeInternal(Personal personal) {
      return personal instanceof Inflicted && this.inflict == ((Inflicted)personal).inflict;
   }

   @Override
   public List<Keyword> getReferencedKeywords() {
      return Arrays.asList(this.inflict);
   }

   @Override
   public float getPriority() {
      return 0.0F;
   }
}
