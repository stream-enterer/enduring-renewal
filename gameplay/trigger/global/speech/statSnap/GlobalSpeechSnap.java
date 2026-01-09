package com.tann.dice.gameplay.trigger.global.speech.statSnap;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.event.entState.ChatStateEvent;
import com.tann.dice.gameplay.progress.StatSnapshot;
import java.util.List;

public class GlobalSpeechSnap extends GlobalSpeechStatSnapshot {
   static final ChatStateEvent cse = new ChatStateEvent(0.15F, "Snap!", "Jinx", "Same same", "Me too", "Ditto");

   @Override
   protected void snapshot(StatSnapshot ss) {
      if (cse.chance()) {
         List<EntState> heroStates = ss.beforeCommand.getStates(true, false);
         Ent source = ss.origin.getSource();
         if (source != null) {
            if (source.isPlayer()) {
               EntState es = ss.beforeCommand.getState(source);
               if (es != null) {
                  Eff e = es.getCurrentSideState().getCalculatedEffect();
                  int myHash = e.hashEff();
                  if (!e.isBasic() || e.getValue() >= 5 || ChatStateEvent.cseChance(0.2F)) {
                     if (!e.hasKeyword(Keyword.duplicate) || ChatStateEvent.cseChance(0.05F)) {
                        for (int i = 0; i < heroStates.size(); i++) {
                           EntState a = heroStates.get(i);
                           if (!a.isUsed()) {
                              EntSideState ess = a.getCurrentSideState();
                              if (ess != null) {
                                 int iHash = ess.getCalculatedEffect().hashEff();
                                 if (a.getEnt() != source && iHash == myHash) {
                                    this.addSnap(ss.afterCommand.getState(a.getEnt()));
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void addSnap(EntState a) {
      if (a != null) {
         a.addEvent(cse);
      }
   }
}
