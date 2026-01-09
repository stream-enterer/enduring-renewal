package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice;

import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import java.util.List;

public class SimpleChoicePhase extends ChoicePhase {
   public SimpleChoicePhase(String data) {
      this(grabTopText(data), ChoosableUtils.deserialiseList(grabChooz(data)));
   }

   private static String grabChooz(String data) {
      if (data.contains(";")) {
         String[] parts = data.split(";", 2);
         return parts[1];
      } else {
         return data;
      }
   }

   private static String grabTopText(String data) {
      if (data.contains(";")) {
         String[] parts = data.split(";");
         return parts[0];
      } else {
         return null;
      }
   }

   public SimpleChoicePhase(List<Choosable> options) {
      this(null, options);
   }

   public SimpleChoicePhase(String topText, List<Choosable> options) {
      super(new ChoiceType(ChoiceType.ChoiceStyle.Number, 1), options, topText);
   }

   @Override
   public String serialise() {
      return this.topMessage == null
         ? "!" + ChoosableUtils.serialiseList(this.options)
         : "!" + this.topMessage + ";" + ChoosableUtils.serialiseList(this.options);
   }
}
