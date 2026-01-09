package com.tann.dice.gameplay.trigger.personal.linked;

import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.personal.Personal;
import java.util.Arrays;
import java.util.List;

public class OnMyTurn extends LinkedPersonal {
   final Personal linked;
   final List<Personal> list;
   final String imageName;

   public OnMyTurn(Personal linked, String imageName) {
      super(linked);
      this.linked = linked;
      this.list = Arrays.asList(linked);
      this.imageName = imageName;
   }

   @Override
   public String describeForSelfBuff() {
      return this.linked.describeForSelfBuff() + " during my turn";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return this.imageName;
   }

   @Override
   public List<Personal> getLinkedPersonals(Snapshot snapshot, EntState entState) {
      return snapshot.isPlayerTurn() == entState.isPlayer() ? this.list : super.getLinkedPersonals(snapshot, entState);
   }

   @Override
   public float getPriority() {
      return -10.0F;
   }

   @Override
   public Personal splice(Personal p) {
      return new OnMyTurn(p, this.imageName);
   }
}
