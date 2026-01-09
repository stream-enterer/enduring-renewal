package com.tann.dice.gameplay.trigger.global.linked;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;

public class GlobalSize extends GlobalLinked {
   final EntSize size;
   final Personal personal;
   final String prefix;

   public GlobalSize(EntSize size, Personal personal) {
      super(personal);
      this.size = size;
      this.personal = personal;
      switch (size) {
         case small:
            this.prefix = "All tiny enemies " + ex("archer");
            break;
         case reg:
            this.prefix = "All hero-sized enemies " + ex("wolf");
            break;
         case big:
            this.prefix = "All big enemies " + ex("troll");
            break;
         case huge:
            this.prefix = "All huge enemies " + ex("dragon");
            break;
         default:
            this.prefix = "All ??? enemies " + ex("hm");
      }
   }

   private static String ex(String monster) {
      return "[grey](such as " + monster + ")[cu]";
   }

   @Override
   public Personal getLinkedPersonal(EntState entState) {
      return !entState.getEnt().isPlayer() && this.size == entState.getEnt().getSize() ? this.personal : super.getLinkedPersonal(entState);
   }

   @Override
   public String describeForSelfBuff() {
      String gains = this.personal.describeForSelfBuff();
      if (gains == null) {
         gains = this.personal.describeForSelfBuff();
      }

      return this.prefix + ":[n]" + gains;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      String sizeName = this.size + "";
      if (this.size == EntSize.reg) {
         if (com.tann.dice.Main.self().translator.shouldTranslate()) {
            sizeName = "regular";
         } else {
            sizeName = "hero-[n]sized";
         }
      }

      return DipPanel.makeSidePanelGroup(big, new TextWriter("[purple]" + sizeName), this.personal, Colours.red);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.personal.getCollisionBits(false);
   }

   @Override
   public GlobalLinked splice(Personal newCenter) {
      return new GlobalSize(this.size, newCenter);
   }
}
