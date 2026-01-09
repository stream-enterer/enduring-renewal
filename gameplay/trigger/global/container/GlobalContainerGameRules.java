package com.tann.dice.gameplay.trigger.global.container;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.GlobalFleeAt;
import com.tann.dice.gameplay.trigger.global.linked.GlobalNumberLimit;
import com.tann.dice.gameplay.trigger.global.speech.GlobalContainerSpeech;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class GlobalContainerGameRules extends Global {
   final Global lim = new GlobalNumberLimit();
   final Global flee = new GlobalFleeAt();
   final Global speech = new GlobalContainerSpeech();

   @Override
   public Personal getLinkedPersonal(EntState entState) {
      return this.lim.getLinkedPersonal(entState);
   }

   @Override
   public int affectFinalMana(int mana) {
      return this.lim.affectFinalMana(mana);
   }

   @Override
   public int affectFinalRerolls(int rerolls) {
      return this.lim.affectFinalRerolls(rerolls);
   }

   @Override
   public boolean flee(Snapshot snapshot) {
      return this.flee.flee(snapshot);
   }

   @Override
   public void statSnapshotCheck(StatSnapshot ss) {
      this.speech.statSnapshotCheck(ss);
   }

   @Override
   public void levelEndAfterShortWait(DungeonContext context) {
      this.speech.levelEndAfterShortWait(context);
   }
}
