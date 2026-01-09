package com.tann.dice.gameplay.fightLog.event.entState;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.screens.dungeon.panels.combatEffects.heal.PanelHighlightActor;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.util.Colours;

public class PanelHighlightEvent extends StateEvent {
   public static PanelHighlightEvent heal = new PanelHighlightEvent(0.4F, Colours.red);
   public static PanelHighlightEvent redirect = new PanelHighlightEvent(0.4F, Colours.grey);
   public static PanelHighlightEvent stoneSkin = new PanelHighlightEvent(0.5F, Colours.grey);
   public static PanelHighlightEvent scale = new PanelHighlightEvent(0.5F, Colours.grey);
   public static PanelHighlightEvent painMirror = new PanelHighlightEvent(0.35F, Colours.red);
   public static PanelHighlightEvent corruptMana = new PanelHighlightEvent(0.35F, Colours.purple);
   public static PanelHighlightEvent plague = new PanelHighlightEvent(1.0F, Colours.purple);
   public static PanelHighlightEvent witchHex = new PanelHighlightEvent(0.5F, Colours.orange);
   public static PanelHighlightEvent resurrect = new PanelHighlightEvent(0.45F, Colours.purple);
   final float duration;
   final Color color;

   public PanelHighlightEvent(float duration, Color color) {
      this.duration = duration;
      this.color = color;
   }

   @Override
   public void act(EntPanelCombat panel) {
      new PanelHighlightActor(this.color, this.duration, panel);
   }
}
