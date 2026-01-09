package com.tann.dice.gameplay.save.antiCheese;

import com.badlogic.gdx.utils.SerializationException;
import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.gameplay.save.SaveStateData;
import java.util.ArrayList;
import java.util.List;

public class AnticheeseData {
   String save;
   public static final int BASE_REROLLS = 1;
   int rerollsSinceLastLoss;
   int rerollsAvailable = 1;

   public AnticheeseData(String save) {
      this.save = save;
   }

   public AnticheeseData() {
   }

   public void reroll() {
      this.rerollsSinceLastLoss++;
      this.rerollsAvailable--;
      this.save = null;
   }

   public boolean canReroll() {
      return this.rerollsAvailable > 0;
   }

   public boolean rerollCountsAsLoss() {
      return this.rerollsSinceLastLoss >= 1;
   }

   public void reachedLevelThree() {
      this.save = null;
      this.rerollsAvailable = 1;
   }

   public static void reachedLevelTen() {
      for (AnticheeseData acd : com.tann.dice.Main.getSettings().getAllSavedAnticheese()) {
         acd.reachedLevelTenOtherMode();
      }

      com.tann.dice.Main.getSettings().save();
   }

   public void reachedLevelTenOtherMode() {
      this.save = null;
      this.rerollsAvailable = 1;
   }

   public void defeated() {
      this.rerollsSinceLastLoss = 0;
   }

   public SaveStateData getSaveState() {
      if (this.save == null) {
         return null;
      } else {
         try {
            return (SaveStateData)com.tann.dice.Main.getJson().fromJson(SaveStateData.class, this.save);
         } catch (SerializationException var2) {
            return null;
         }
      }
   }

   public void setSaveState(String save) {
      this.save = save;
   }

   public AntiCheeseRerollInfo getRerollInfo() {
      SaveStateData ssd = this.getSaveState();
      if (ssd == null) {
         return AntiCheeseRerollInfo.makeBlank();
      } else {
         SaveState ss = ssd.toState();
         List<HeroType> types = HeroTypeUtils.fromHeroes(ss.dungeonContext.getParty().getHeroes());
         List<Modifier> modifiers = new ArrayList<>();

         for (String pd : ss.phases) {
            Phase p = Phase.deserialise(pd);
            if (p instanceof ChoicePhase) {
               ChoicePhase cp = (ChoicePhase)p;

               for (Choosable choo : cp.getOptions()) {
                  if (choo instanceof Modifier) {
                     modifiers.add((Modifier)choo);
                  }
               }
            }
         }

         return new AntiCheeseRerollInfo(types, modifiers, PartyLayoutType.guessLayout(types));
      }
   }

   @Override
   public String toString() {
      return this.save + ":" + this.rerollsSinceLastLoss + ":" + this.rerollsAvailable;
   }
}
