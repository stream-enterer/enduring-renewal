package com.tann.dice.gameplay.fightLog.event.entState;

import com.tann.dice.gameplay.fightLog.event.snapshot.SnapshotEvent;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;

public class LinkEvent extends StateEvent {
   final SnapshotEvent sse;

   public LinkEvent(SnapshotEvent sse) {
      this.sse = sse;
   }

   @Override
   public void act(EntPanelCombat panel) {
      this.sse.act(DungeonScreen.get().abilityHolder);
   }
}
