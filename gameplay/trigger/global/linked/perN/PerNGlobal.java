package com.tann.dice.gameplay.trigger.global.linked.perN;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.linked.GlobalLinked;
import com.tann.dice.util.Pixl;
import java.util.ArrayList;
import java.util.List;

public abstract class PerNGlobal extends GlobalLinked {
   final Global linked;

   public PerNGlobal(Global linked) {
      super(linked);
      this.linked = linked;
   }

   @Override
   public final String describeForSelfBuff() {
      return "[notranslate]"
         + com.tann.dice.Main.t("For each " + this.describeN())
         + ": "
         + this.linked.describeForSelfBuff()
         + com.tann.dice.Main.tOnce(this.getAddendum());
   }

   protected String getAddendum() {
      return "";
   }

   protected abstract String describeN();

   @Override
   public final List<Global> getLinkedGlobalList(int level, DungeonContext context, int turn) {
      List<Global> result = new ArrayList<>();
      int amt = this.getAmt(level, context, turn);

      for (int i = 0; i < amt; i++) {
         result.add(this.linked);
      }

      return result;
   }

   protected abstract int getAmt(int var1, DungeonContext var2, int var3);

   @Override
   public final Actor makePanelActorI(boolean big) {
      return new Pixl().actor(this.linked.makePanelActor(big)).gap(2).text("[pink]x").gap(2).actor(this.getPerActor()).pix();
   }

   protected abstract Actor getPerActor();

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.ignored(this.linked.getCollisionBits() | Collision.SPECIFIC_LEVEL, Collision.GENERIC_ALL_SIDES_HERO);
   }
}
