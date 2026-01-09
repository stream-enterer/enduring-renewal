package com.tann.dice.gameplay.trigger.global.speech.statSnap;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.command.DieCommand;
import com.tann.dice.gameplay.fightLog.event.entState.ChatStateEvent;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.trigger.global.speech.GlobalSpeech;
import com.tann.dice.util.Tann;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class GlobalSpeechEnvy extends GlobalSpeechStatSnapshot {
   final ChatStateEvent cse;

   protected GlobalSpeechEnvy(ChatStateEvent cse) {
      this.cse = cse;
   }

   public static Collection<? extends GlobalSpeech> makeEnvies() {
      return Arrays.asList(
         new GlobalSpeechEnvy(new ChatStateEvent(0.1F, "Impressive!", "Nice healing", "I could have done that...", "[i]takes notes[i]", "...")) {
            @Override
            protected boolean enviousCommand(Hero h, StatSnapshot ss) {
               if (h.getHeroCol() == HeroCol.red) {
                  return false;
               } else {
                  float THRESHOLD = (float)Math.pow(h.getLevel(), 1.7F) + 3.0F;
                  int totalHpPre = 0;
                  int totalHpPost = 0;

                  for (EntState es : ss.beforeCommand.getStates(true, false)) {
                     totalHpPre += es.getHp();
                  }

                  for (EntState es : ss.afterCommand.getStates(true, false)) {
                     totalHpPost += es.getHp();
                  }

                  return !(totalHpPost < totalHpPre + THRESHOLD);
               }
            }

            @Override
            protected EntState speechHero(List<EntState> heroes) {
               return getHero(heroes, HeroCol.red);
            }
         }, new GlobalSpeechEnvy(new ChatStateEvent(0.1F, "Impressive!", "How did you learn that?", "I could have done that...", "[i]takes notes[i]")) {
            @Override
            protected boolean enviousCommand(Hero h, StatSnapshot ss) {
               if (HeroTypeUtils.isSpelly(h.getHeroCol())) {
                  return false;
               } else {
                  float THRESHOLD = h.getLevel() + 2;
                  return !(ss.afterCommand.getTotalMana() < ss.beforeCommand.getTotalMana() + THRESHOLD);
               }
            }

            @Override
            protected EntState speechHero(List<EntState> heroes) {
               return getHero(heroes, Tann.pick(HeroCol.blue, HeroCol.red));
            }
         }
      );
   }

   protected abstract boolean enviousCommand(Hero var1, StatSnapshot var2);

   protected abstract EntState speechHero(List<EntState> var1);

   @Override
   protected void snapshot(StatSnapshot ss) {
      if (this.cse.chance()) {
         if (ss.origin instanceof DieCommand) {
            DieCommand dc = (DieCommand)ss.origin;
            Ent en = dc.getSource();
            if (en != null && en.isPlayer()) {
               if (this.enviousCommand((Hero)en, ss)) {
                  EntState target = this.speechHero(ss.afterCommand.getStates(true, false));
                  if (target != null) {
                     target.addEvent(this.cse);
                  }
               }
            }
         }
      }
   }

   protected static EntState getHero(List<EntState> heroes, HeroCol col) {
      for (EntState e : heroes) {
         if (e.isPlayer() && ((Hero)e.getEnt()).getHeroCol() == col) {
            return e;
         }
      }

      return null;
   }
}
