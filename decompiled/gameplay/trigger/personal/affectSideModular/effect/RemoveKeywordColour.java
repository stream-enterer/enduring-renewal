package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoveKeywordColour extends AffectSideEffect {
   final List<Color> cols;

   public RemoveKeywordColour(Color... cols) {
      this.cols = Arrays.asList(cols);
   }

   @Override
   public String getToFrom() {
      return "from";
   }

   @Override
   public String describe() {
      return "Remove " + Tann.commaList(this.makeNameList(this.cols)) + " keywords";
   }

   private List<String> makeNameList(List<Color> cols) {
      List<String> result = new ArrayList<>();

      for (int i = 0; i < cols.size(); i++) {
         Color c = cols.get(i);
         result.add(TextWriter.getTag(c) + TextWriter.getNameForColour(c) + "[cu]");
      }

      return result;
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      Eff e = sideState.getCalculatedEffect();
      List<Keyword> ks = e.getKeywords();
      boolean removed = false;

      for (int i = ks.size() - 1; i >= 0; i--) {
         Keyword k = e.getKeywords().get(i);
         if (this.cols.contains(k.getColour())) {
            removed = true;
            e.removeKeyword(k);
         }
      }

      if (removed) {
         e.getBonusKeywords().add(Keyword.removed);
      }
   }

   @Override
   public Actor getOverrideActor(List<AffectSideCondition> conditions) {
      Pixl p = new Pixl(3);

      for (int i = 0; i < this.cols.size(); i++) {
         Color c = this.cols.get(i);
         p.actor(Tann.combineActors(new ImageActor(Images.ui_cross, Colours.grey), new TextWriter(TextWriter.getTag(c) + TextWriter.getNameForColour(c))));
         if (i < this.cols.size() - 1) {
            p.gap(3);
         }
      }

      return p.pix();
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }
}
