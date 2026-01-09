package com.tann.dice.gameplay.fightLog.event.entState;

import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;

public abstract class StateEvent {
   public abstract void act(EntPanelCombat var1);

   public boolean chance() {
      return true;
   }
}
