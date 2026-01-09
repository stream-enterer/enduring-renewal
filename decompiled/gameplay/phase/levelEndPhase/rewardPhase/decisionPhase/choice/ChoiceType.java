package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice;

import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.lang.Words;
import java.util.ArrayList;
import java.util.List;

public class ChoiceType {
   ChoiceType.ChoiceStyle cs;
   int v;
   transient List<Choosable> currentChoices = new ArrayList<>();

   public ChoiceType(String s) {
      String[] parts = s.split("#");
      this.cs = ChoiceType.ChoiceStyle.valueOf(parts[0]);
      this.v = Integer.parseInt(parts[1]);
   }

   public String toSaveString() {
      return this.cs + "#" + this.v;
   }

   public ChoiceType(ChoiceType.ChoiceStyle choiceStyle, int value) {
      this.cs = choiceStyle;
      this.v = value;
   }

   public boolean toggleChoice(Choosable c) {
      if (this.currentChoices.contains(c)) {
         this.currentChoices.remove(c);
         Sounds.playSound(Sounds.pop);
      } else {
         this.currentChoices.add(c);
         Sounds.playSound(Sounds.pip);
      }

      return this.checkValid(false);
   }

   public void clearChoices() {
      this.currentChoices.clear();
   }

   public boolean checkValid(boolean confirm) {
      switch (this.cs) {
         case Number:
            return this.currentChoices.size() == this.v;
         case UpToNumber:
            return this.currentChoices.size() <= this.v && confirm;
         case PointBuy:
            if (this.currentChoices.isEmpty()) {
               return false;
            } else {
               int total = 0;

               for (Choosable c : this.currentChoices) {
                  total += c.getTier();
               }

               if (this.v > 0 && total < this.v * 0.7F) {
                  return false;
               } else {
                  if (this.v < 0 && total < this.v * 1.5F) {
                     return false;
                  }

                  return total <= this.v && !this.currentChoices.isEmpty();
               }
            }
         case Optional:
            return true;
         default:
            throw new RuntimeException("uhoh invalid choicestyle: " + this.cs);
      }
   }

   public String getDescription(List<Choosable> options) {
      if (options.size() == 0) {
         return "wtf/?";
      } else {
         String choiceName = options.get(0).describe();

         for (Choosable c : options) {
            if (!ChoosableUtils.isMeta(c)) {
               String s = c.describe();
               if (!s.equalsIgnoreCase(choiceName)) {
                  choiceName = Words.plural("thing", this.v);
                  break;
               }
            }
         }

         switch (this.cs) {
            case Number:
               String amtString = this.v == 1 ? (Words.startsWithVowel(choiceName) ? "an" : "a") : "" + this.v;
               return "Choose " + amtString + " " + Words.plural(choiceName, this.v);
            case UpToNumber:
               return "Choose up to " + this.v + " " + Words.plural(choiceName, this.v);
            case PointBuy:
               return "Choose " + Words.plural(choiceName, true) + " with a combined value of " + Words.getTierString(this.v, true);
            case Optional:
               return null;
            default:
               throw new RuntimeException("uhoh invalid choicestyle: " + this.cs);
         }
      }
   }

   public static enum ChoiceStyle {
      Number,
      PointBuy,
      UpToNumber,
      Optional;
   }
}
