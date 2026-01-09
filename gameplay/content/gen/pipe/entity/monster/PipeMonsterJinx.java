package com.tann.dice.gameplay.content.gen.pipe.entity.monster;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobBig;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.bill.MTBill;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.personal.specificMonster.Jinx;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class PipeMonsterJinx extends PipeRegexNamed<MonsterType> {
   private static final PRNPart PREF = new PRNPref("Jinx");
   public static final String[] CURSE_NAMES = new String[]{
      "3 less max mana",
      "death shield^2",
      "slippery dice^2",
      "heavy weapons",
      "exposed edges",
      "monster left^4",
      "Bones Bones",
      "Reduced Defence",
      "Shield Response"
   };

   public PipeMonsterJinx() {
      super(PREF, MODIFIER);
   }

   public static boolean isJinx(EntType entType) {
      return entType.getName(false).startsWith(PREF.toString());
   }

   protected MonsterType internalMake(String[] groups) {
      String tag = groups[0];
      return Tann.isInt(tag) ? this.makeIndexed(Integer.parseInt(tag)) : this.make(ModifierLib.byName(tag));
   }

   private MonsterType make(Modifier toAdd) {
      if (toAdd != null && !toAdd.isMissingno()) {
         String name = PREF + toAdd.getName();
         return new MTBill(EntSize.big)
            .name(name)
            .hp(8)
            .death(Sounds.deathPew)
            .max(1)
            .texture("special/jinx")
            .sides(
               EntSidesBlobBig.claw.val(3),
               EntSidesBlobBig.claw.val(3),
               EntSidesBlobBig.batSwarm.val(2),
               EntSidesBlobBig.batSwarm.val(2),
               EntSidesBlobBig.summonSkeleton.val(2),
               EntSidesBlobBig.summonSkeleton.val(2)
            )
            .trait(new Trait(new Jinx(toAdd), true))
            .bEntType();
      } else {
         return null;
      }
   }

   public MonsterType example() {
      return this.randomIndexed();
   }

   public MonsterType randomIndexed() {
      return this.makeIndexed((int)(Math.random() * CURSE_NAMES.length));
   }

   public MonsterType makeIndexed(int curseIndex) {
      return this.make(getJinxCurses(curseIndex));
   }

   public static Modifier getJinxCurses(int curseIndex) {
      return curseIndex < 0 ? null : ModifierLib.byName(CURSE_NAMES[curseIndex % CURSE_NAMES.length]);
   }

   public List<MonsterType> makeAllJinx() {
      List<MonsterType> result = new ArrayList<>();

      for (int i = 0; i < CURSE_NAMES.length; i++) {
         result.add(this.makeIndexed(i));
      }

      return result;
   }

   public static List<Modifier> makeAllCurses() {
      return ModifierLib.getByNames(CURSE_NAMES);
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return wild;
   }

   @Override
   public float getRarity(boolean wild) {
      return 0.5F;
   }

   protected MonsterType generateInternal(boolean wild) {
      return this.example();
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
