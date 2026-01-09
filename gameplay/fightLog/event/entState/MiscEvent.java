package com.tann.dice.gameplay.fightLog.event.entState;

import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;

public class MiscEvent extends StateEvent {
   public static final MiscEvent spike = new MiscEvent();

   @Override
   public void act(EntPanelCombat panel) {
      panel.showSpikes();
   }
}
