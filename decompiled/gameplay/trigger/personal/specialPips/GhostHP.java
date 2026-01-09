package com.tann.dice.gameplay.trigger.personal.specialPips;

import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.fightLog.event.entState.LinkEvent;
import com.tann.dice.gameplay.fightLog.event.entState.PanelHighlightEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.SoundSnapshotEvent;
import com.tann.dice.gameplay.trigger.personal.immunity.DamageImmunity;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import com.tann.dice.util.Colours;
import java.util.Arrays;

public class GhostHP extends TriggerHP {
   public GhostHP(PipLoc pipLoc) {
      super(
         new EffBill().self().buff(new Buff(1, new DamageImmunity(true, true))).bEff(),
         Arrays.asList(PanelHighlightEvent.witchHex, new LinkEvent(SoundSnapshotEvent.stealth)),
         Colours.grey,
         pipLoc
      );
   }

   @Override
   public boolean lateTrigger() {
      return true;
   }
}
