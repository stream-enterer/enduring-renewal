package com.tann.dice.gameplay.trigger.global.speech.statSnap;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.command.AbilityCommand;
import com.tann.dice.gameplay.fightLog.event.entState.ChatStateEvent;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.HeroDeath;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GlobalSpeechOtherDeath extends GlobalSpeechStatSnapshot {
   private static final ChatStateEvent cse = new ChatStateEvent(0.2F, "not again...", "stop feeding");
   final int THRESHOLD = 4;

   @Override
   protected void snapshot(StatSnapshot ss) {
      if (cse.chance()) {
         if (!(ss.origin instanceof AbilityCommand)) {
            List<? extends Ent> aliveBefore = ss.beforeCommand.getAliveEntities(true);
            List<? extends Ent> aliveAfter = ss.afterCommand.getAliveEntities(true);
            if (aliveAfter.size() < aliveBefore.size()) {
               List<Ent> died = new ArrayList<>(aliveBefore);
               died.removeAll(aliveAfter);
               if (died.size() == 1) {
                  Ent dead = died.get(0);
                  EntState deadState = ss.afterCommand.getState(dead);
                  if (deadState != null) {
                     int index = ss.context.getParty().getHeroes().indexOf((Hero)dead);
                     if (index != -1) {
                        Map<String, Stat> map = ss.context.getStatsManager().getStatsMap();
                        String heroDeathName = HeroDeath.getNameFromIndex(index);
                        Stat ds = map.get(heroDeathName);
                        if (ds != null) {
                           int val = ds.getValue() + deadState.getDeathsForStats();
                           if (val >= 4) {
                              List<EntState> aliveHeroes = ss.afterCommand.getStates(true, false);
                              if (!aliveHeroes.isEmpty()) {
                                 Tann.pick(aliveHeroes).addEvent(cse);
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
