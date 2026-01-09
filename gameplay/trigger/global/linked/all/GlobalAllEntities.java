package com.tann.dice.gameplay.trigger.global.linked.all;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.global.linked.GlobalLinked;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.lang.Words;

public class GlobalAllEntities extends GlobalLinked {
   public final Personal personal;
   final Boolean player;

   public GlobalAllEntities(Personal personal) {
      this(null, personal);
   }

   public GlobalAllEntities(Boolean player, Personal personal) {
      super(personal);
      this.player = player;
      this.personal = personal;
   }

   @Override
   public String describeForSelfBuff() {
      String end = ":[cu] ";
      String start;
      if (this.player == null) {
         start = com.tann.dice.Main.t("[text]All heroes and monsters") + ":[cu] ";
      } else if (this.player) {
         start = com.tann.dice.Main.t("[green]All heroes") + ":[cu] ";
      } else {
         start = com.tann.dice.Main.t("[red]All monsters") + ":[cu] ";
      }

      return "[notranslate]" + start + Words.capitaliseFirst(com.tann.dice.Main.t(this.personal.describeForSelfBuff()).toLowerCase());
   }

   @Override
   public Personal getLinkedPersonal(EntState entState) {
      return (this.player == null || entState.getEnt().isPlayer() == this.player) && this.personal.canBeAddedTo(entState)
         ? this.personal
         : super.getLinkedPersonal(entState);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.personal.getCollisionBits(this.player);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Actor a = this.personal.makePanelActor(big);
      if (a == null) {
         return null;
      } else {
         Color border = colForPlayer(this.player);
         return new Pixl(0, 3).border(border).actor(a).pix();
      }
   }

   public static Color colForPlayer(Boolean player) {
      Color border = Colours.yellow;
      if (player != null) {
         border = player ? Colours.green : Colours.red;
      }

      return border;
   }

   public Boolean getPlayer() {
      return this.player;
   }
}
