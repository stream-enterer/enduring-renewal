package com.tann.dice.gameplay.phase.gameplay;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.choice.CDChoice;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import java.util.ArrayList;
import java.util.List;

public class SurrenderPhase extends Phase {
   ChoiceDialog cd;

   @Override
   public void activate() {
      Sounds.playSound(Sounds.surr, 1.0F, Tann.random(0.8F, 1.2F));
      DungeonScreen ds = DungeonScreen.get();
      List<EntState> states = ds.getFightLog().getSnapshot(FightLog.Temporality.Present).getStates(false, false);
      this.cd = new ChoiceDialog(getFleeString(states), new ArrayList<>(), new CDChoice(ChoiceDialog.ChoiceNames.YesNo.decline, new Runnable() {
         @Override
         public void run() {
            SurrenderPhase.this.decline();
         }
      }), new CDChoice("[purple]?", new Runnable() {
         @Override
         public void run() {
            SurrenderPhase.this.showExpl();
         }
      }), new CDChoice(ChoiceDialog.ChoiceNames.YesNo.accept, new Runnable() {
         @Override
         public void run() {
            Sounds.playSound(Sounds.flee);
            SurrenderPhase.this.accept();
         }
      }));
      ds.push(this.cd, true, false, false, 0.7F);
      Tann.center(this.cd);
   }

   private void showExpl() {
      Sounds.playSound(Sounds.paper);
      Actor a = new Pixl(3, 3)
         .border(Colours.purple)
         .text("[text]Enemies will try to flee if they think the fight is hopeless. There is no gameplay benefit either way.", 120)
         .row(4)
         .actor(OptionLib.AUTO_FLEE.makeCogActor())
         .pix();
      com.tann.dice.Main.getCurrentScreen().push(a, 0.5F);
      Tann.center(a);
   }

   public void accept() {
      this.popit();
      DungeonScreen ds = DungeonScreen.get();
      ds.getDungeonContext().getStatsManager().surrenderLog(true);
      FightLog f = ds.getFightLog();
      f.enemiesSurrendered();
   }

   private void popit() {
      if (this.cd != null) {
         com.tann.dice.Main.getCurrentScreen().popAllLight();
         com.tann.dice.Main.getCurrentScreen().pop(this.cd);
      }
   }

   private void decline() {
      this.popit();
      DungeonScreen ds = DungeonScreen.get();
      ds.getDungeonContext().getStatsManager().surrenderLog(false);
      ds.getFightLog().getSnapshot(FightLog.Temporality.Present).refusedSurrender();
      PhaseManager p = PhaseManager.get();
      p.forceNext(new EnemyRollingPhase());
      p.popPhase(SurrenderPhase.class);
   }

   @Override
   public void deactivate() {
   }

   @Override
   public boolean isDuringCombat() {
      return true;
   }

   @Override
   public boolean requiresSerialisation() {
      return false;
   }

   private static String getFleeString(List<EntState> states) {
      String fleeText = "";
      String escapeText = "";
      if (com.tann.dice.Main.self().translator.shouldTranslate()) {
         String fleeAction = getFleeAction(states);
         if (states.size() == 1) {
            if (fleeAction.equals("roll away")) {
               fleeText = "The monster is trying to roll away.";
            } else {
               fleeText = "The monster is trying to flee.";
            }

            escapeText = "Will you let it escape?";
         } else {
            if (fleeAction.equals("roll away")) {
               fleeText = "The monsters are trying to roll away.";
            } else {
               fleeText = "The monster is trying to flee.";
            }

            escapeText = "Will you let them escape?";
         }
      } else {
         fleeText = describeGroupOfEnemies(states) + " " + Words.plural("is", states.size() > 1) + " trying to " + getFleeAction(states);
         escapeText = "Will you let them escape?";
      }

      return "[notranslateall]" + com.tann.dice.Main.t(fleeText) + "[n][text]" + com.tann.dice.Main.t(escapeText);
   }

   private static String getFleeAction(List<EntState> states) {
      boolean allRoll = true;

      for (EntState es : states) {
         EntType et = es.getEnt().entType;
         boolean eggy = et.getName(false).contains(" Egg");
         boolean barrelsome = et == MonsterTypeLib.byName("barrel");
         allRoll &= eggy || barrelsome;
      }

      return allRoll ? "roll away" : "flee";
   }

   public static String describeGroupOfEnemies(List<EntState> states) {
      if (states.size() == 0) {
         return "[sin]bug????[sin]";
      } else if (states.size() > 1) {
         return "The monsters";
      } else {
         MonsterType m = (MonsterType)states.get(0).getEnt().entType;
         return m.isUnique() ? states.get(0).getEnt().getName(true) : "The " + states.get(0).getEnt().getName(true).toLowerCase();
      }
   }

   private static boolean allSameType(List<EntState> states) {
      EntType prev = null;

      for (EntState es : states) {
         if (prev != null && es.getEnt().entType != prev) {
            return false;
         }

         prev = es.getEnt().entType;
      }

      return true;
   }

   @Override
   public boolean canSave() {
      return false;
   }
}
