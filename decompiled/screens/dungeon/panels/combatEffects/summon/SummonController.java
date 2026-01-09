package com.tann.dice.screens.dungeon.panels.combatEffects.summon;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectController;
import com.tann.dice.screens.dungeon.panels.combatEffects.heal.PanelHighlightActor;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;

public class SummonController extends CombatEffectController {
   final Color highlight;
   final float duration;
   final String[] sounds;
   Ent source;
   private float ratio = 0.25F;

   public SummonController(Ent source, MonsterType summon) {
      this.highlight = Colours.purple;
      this.source = source;
      String var3 = summon.getName(false);
      switch (var3) {
         case "Wolf":
            this.duration = 2.1F;
            this.ratio = 0.6F;
            this.sounds = Sounds.summonWolf;
            break;
         case "Bones":
            this.duration = 0.6F;
            this.sounds = Sounds.summonBones;
            break;
         case "Imp":
            this.duration = 0.7F;
            this.sounds = Sounds.summonImp;
            break;
         default:
            this.duration = 0.8F;
            this.sounds = Sounds.summonGeneric;
      }
   }

   @Override
   protected void start() {
      Sounds.playSound(this.sounds);
      if (this.source != null) {
         new PanelHighlightActor(this.highlight, this.duration, this.source.getEntPanel());
      }
   }

   @Override
   protected float getImpactDuration() {
      return this.duration * this.ratio * OptionUtils.unkAnim();
   }

   @Override
   protected float getExtraDuration() {
      return this.duration * (1.0F - this.ratio) * OptionUtils.unkAnim();
   }
}
