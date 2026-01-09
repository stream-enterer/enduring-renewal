package com.tann.dice.screens.dungeon.panels.combatEffects;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.gameplay.trigger.personal.Dodge;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import java.util.List;

public abstract class CombatEffectActor extends Group {
   public CombatEffectActor() {
      this.setTouchable(Touchable.disabled);
   }

   protected abstract void start(FightLog var1);

   protected abstract float getImpactDurationInternal();

   protected abstract float getExtraDurationInternal();

   public float getExtraDuration() {
      return this.getExtraDurationInternal() * OptionUtils.unkAnim();
   }

   public float getImpactDuration() {
      return this.getImpactDurationInternal() * OptionUtils.unkAnim();
   }

   public static boolean isBlocked(int damage, Ent target) {
      EntState state = target.getState(FightLog.Temporality.Visual);
      return state.getShields() >= damage;
   }

   public static boolean isDodged(Ent target) {
      EntState state = target.getState(FightLog.Temporality.Visual);
      return state.hasPersonal(Dodge.class);
   }

   public static float getTopMost(List<EntState> targets) {
      int topMost = 0;

      for (EntState state : targets) {
         if (!state.getEnt().getState(FightLog.Temporality.Visual).isDead()) {
            EntPanelCombat panel = state.getEnt().getEntPanel();
            if (panel.getParent() != null) {
               topMost = (int)Math.max((float)topMost, panel.getY() + panel.getHeight() + panel.getParent().getY());
            }
         }
      }

      return topMost;
   }

   public static float getBotMost(List<EntState> targets) {
      int bottomMost = 9999;

      for (EntState state : targets) {
         if (!state.getEnt().getState(FightLog.Temporality.Visual).isDead()) {
            EntPanelCombat panel = state.getEnt().getEntPanel();
            if (panel.getParent() != null) {
               bottomMost = (int)Math.min((float)bottomMost, panel.getY() + panel.getParent().getY());
            }
         }
      }

      return bottomMost;
   }
}
