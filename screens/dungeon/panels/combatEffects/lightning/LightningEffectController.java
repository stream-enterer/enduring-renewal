package com.tann.dice.screens.dungeon.panels.combatEffects.lightning;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectController;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.LightningActor;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class LightningEffectController extends CombatEffectController {
   Ent source;
   List<Ent> targets;
   final int NUM_LINES;
   static final float SPAWN_TIME = 0.2F;
   static final float FADE_TIME = 0.3F;
   static final float VARIANCE = 6.0F;
   static final float SEG_LENGTH = 20.0F;

   public LightningEffectController(Ent source, List<Ent> targets, int num_lines) {
      this.source = source;
      this.targets = targets;
      this.NUM_LINES = num_lines;
   }

   @Override
   protected void start() {
      Sounds.playSound(Sounds.lightning);
      List<Ent> chain = new ArrayList<>(this.targets);
      chain.add(0, this.source);
      Vector2 end = null;

      for (int chainIndex = 0; chainIndex < chain.size() - 1; chainIndex++) {
         Ent zapSource = chain.get(chainIndex);
         Ent zapTarget = chain.get(chainIndex + 1);
         Vector2 start;
         if (end == null) {
            if (zapSource != null) {
               EntPanelCombat sourcePan = zapSource.getEntPanel();
               start = Tann.getAbsoluteCoordinates(sourcePan).add(sourcePan.getWidth() - 15.0F, sourcePan.getHeight() / 2.0F).cpy();
            } else {
               start = new Vector2(0.0F, com.tann.dice.Main.height / 2);
            }
         } else {
            start = end;
         }

         EntPanelCombat targetPan = zapTarget.getEntPanel();
         end = Tann.getAbsoluteCoordinates(targetPan).add(15.0F, targetPan.getHeight() / 2.0F).cpy();

         for (int i = 0; i < this.NUM_LINES; i++) {
            LightningActor la = new LightningActor(start.x, start.y, end.x, end.y, 20.0F, 6.0F);
            DungeonScreen.get().addActor(la);
            la.setColor(Colours.withAlpha(Tann.half() ? Colours.light : Colours.blue, 0.0F));
            float delayAmount = (float)i / this.NUM_LINES * 0.2F;
            la.addAction(Actions.delay(delayAmount, Actions.sequence(Actions.alpha(1.0F), Tann.fadeAndRemove(0.3F))));
         }
      }
   }

   @Override
   protected float getImpactDuration() {
      return 0.1F * OptionUtils.unkAnim();
   }

   @Override
   protected float getExtraDuration() {
      return 0.4F * OptionUtils.unkAnim();
   }
}
