package com.tann.dice.gameplay.content.gen.pipe.regex.meta;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.RenameHero;
import com.tann.dice.gameplay.trigger.personal.item.PersonalItemRename;
import java.util.List;

public class RenameUtils {
   public static HeroType make(HeroType ht, String heroName, String displayName) {
      if (ht.isMissingno()) {
         return null;
      } else {
         RenameHero rh = new RenameHero(displayName);
         return HeroTypeUtils.withPassive(ht, heroName, rh, null);
      }
   }

   public static MonsterType make(MonsterType mt, String monsterName, String displayName) {
      if (mt.isMissingno()) {
         return null;
      } else {
         RenameHero rh = new RenameHero(displayName);
         return HeroTypeUtils.withPassive(mt, monsterName, rh, null);
      }
   }

   private static ItBill copy(Item src, String name, TextureRegion img) {
      ItBill ib = new ItBill(src.getTier(), name, img);
      ib.prs(src.getPersonals());
      return ib;
   }

   public static ItBill copy(Item src, String name) {
      return copy(src, name, src.getImage());
   }

   public static boolean hasRename(ItBill ib) {
      List<Personal> personals = ib.getPersonals();

      for (int i = 0; i < personals.size(); i++) {
         Personal p = personals.get(i);
         if (p instanceof PersonalItemRename) {
            return true;
         }
      }

      return false;
   }

   public static void rename(ItBill ib, String name, boolean force) {
      if (force || !hasRename(ib)) {
         ib.prs(new PersonalItemRename(name));
      }
   }
}
