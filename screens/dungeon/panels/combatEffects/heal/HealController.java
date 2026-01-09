package com.tann.dice.screens.dungeon.panels.combatEffects.heal;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.command.TargetableCommand;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectController;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;

public class HealController extends CombatEffectController {
   private Eff eff;
   private TargetableCommand command;

   public HealController(TargetableCommand command, Eff eff) {
      this.command = command;
      this.eff = eff;
   }

   @Override
   protected void start() {
      if (this.eff.getBuff() == null) {
         Sounds.playSound(Sounds.heals);
      } else {
         Sounds.playSound(Sounds.regen);
      }

      for (Ent de : this.command.getAllTargets()) {
         new PanelHighlightActor(Colours.red, 0.5F, de.getEntPanel());
      }
   }

   @Override
   protected float getExtraDuration() {
      return PanelHighlightActor.FADE_DURATION * 0.6F * OptionUtils.unkAnim();
   }

   @Override
   protected float getImpactDuration() {
      return 0.0F;
   }
}
