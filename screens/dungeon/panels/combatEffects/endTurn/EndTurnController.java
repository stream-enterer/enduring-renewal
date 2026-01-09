package com.tann.dice.screens.dungeon.panels.combatEffects.endTurn;

import com.badlogic.gdx.math.Interpolation;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectController;
import com.tann.dice.screens.dungeon.panels.entPanel.heartsHolder.HPHolder;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.Flasher;

public class EndTurnController extends CombatEffectController {
   FightLog fightLog = DungeonScreen.get().getFightLog();

   @Override
   protected void start() {
      if (this.isPoison()) {
         Sounds.playSound(Sounds.poisonImpact);
      }

      if (this.isRegen()) {
         Sounds.playSound(Sounds.regenActivate);
      }

      for (EntState es : this.fightLog.getSnapshot(FightLog.Temporality.Future).getRegened()) {
         HPHolder hh = es.getEnt().getEntPanel().HPHolder;
         Flasher f = new Flasher(hh, Colours.withAlpha(Colours.red, 0.4F), this.getExtraDuration(), Interpolation.linear);
         hh.addActor(f);
      }

      for (EntState es : this.fightLog.getSnapshot(FightLog.Temporality.Future).getPoisoned()) {
         HPHolder hh = es.getEnt().getEntPanel().HPHolder;
         Flasher f = new Flasher(hh, Colours.withAlpha(Colours.green, 0.4F), this.getExtraDuration(), Interpolation.linear);
         hh.addActor(f);
      }
   }

   @Override
   protected float getImpactDuration() {
      return 0.0F;
   }

   @Override
   protected float getExtraDuration() {
      return this.gbed() * OptionUtils.unkAnim();
   }

   private float gbed() {
      if (this.hasMonster(MonsterTypeLib.byName("Rotten"))) {
         return 1.1F;
      } else if (this.hasMonster(MonsterTypeLib.byName("Bell"))) {
         return 0.8F;
      } else {
         return !this.isRegen() && !this.isPoison() ? 0.0F : 0.65F;
      }
   }

   private boolean hasMonster(MonsterType type) {
      for (EntState es : this.fightLog.getSnapshot(FightLog.Temporality.Present).getStates(false, false)) {
         if (es.getEnt().getEntType() == type) {
            return true;
         }
      }

      return false;
   }

   private boolean isPoison() {
      return this.fightLog.getSnapshot(FightLog.Temporality.Future).getPoisoned().size() > 0;
   }

   private boolean isRegen() {
      return this.fightLog.getSnapshot(FightLog.Temporality.Future).getRegened().size() > 0;
   }
}
