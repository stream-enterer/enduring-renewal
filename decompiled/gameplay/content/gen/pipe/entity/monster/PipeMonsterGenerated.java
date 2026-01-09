package com.tann.dice.gameplay.content.gen.pipe.entity.monster;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.bill.MTBill;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.modifier.generation.GenUtils;
import com.tann.dice.util.Tann;
import com.tann.dice.util.WhiskerRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class PipeMonsterGenerated extends PipeRegexNamed<MonsterType> {
   private static final String PREF = "rmon.";

   public PipeMonsterGenerated() {
      super(prnS("rmon."), UP_TO_FIFTEEN_HEX);
   }

   protected MonsterType internalMake(String[] groups) {
      long seed = GenUtils.hex(groups[0]);
      return seed < 0L ? null : makeMonst(seed);
   }

   public static MonsterType makeMonstExt() {
      return makeMonstExt((long)(Math.random() * 10000.0));
   }

   public static MonsterType makeMonstExt(long seed) {
      return makeMonst(seed);
   }

   private static MonsterType makeMonst(long seed) {
      WhiskerRandom r = new WhiskerRandom(seed);
      EntSize size = Tann.random(EntSize.values(), r);
      if (size == EntSize.huge) {
         size = Tann.random(EntSize.values(), r);
      }

      float variance = 0.2F;
      int hp = (int)((size.getPixels() - 6) * (r.nextFloat() * variance * 2.0F + (1.0F - variance)));
      List<EntSide> sides = new ArrayList<>();

      for (int i = 0; i < 3; i++) {
         EntSide side = getRandomSide(r, size);
         sides.add(side);
         sides.add(side);
      }

      Collections.sort(sides, new Comparator<EntSide>() {
         public int compare(EntSide o1, EntSide o2) {
            return o2.getBaseEffect().getValue() - o1.getBaseEffect().getValue();
         }
      });
      MTBill mtb = new MTBill(size).hp(hp).name("rmon." + GenUtils.hex(seed));
      mtb.texture("special/rm");
      mtb.sides(sides.toArray(new EntSide[0]));
      Trait t = randomTrait(r);
      if (t != null && !t.personal.metaOnly()) {
         mtb.trait(t);
      }

      return mtb.bEntType();
   }

   private static Trait randomTrait(Random r) {
      MonsterType mt = randomMonster(r, null);

      for (Trait t : mt.traits) {
         if (t.visible) {
            return t;
         }
      }

      return null;
   }

   private static EntSide getRandomSide(Random r, EntSize size) {
      MonsterType mt = randomMonster(r, size);
      return Tann.random(mt.sides, r).copy();
   }

   private static MonsterType randomMonster(Random r, EntSize size) {
      return size == null ? Tann.random(MonsterTypeLib.getMasterCopy(), r) : Tann.random(MonsterTypeLib.getWithSize(size), r);
   }

   public MonsterType example() {
      return makeMonst((long)(Math.random() * Math.pow(16.0, 3.0)));
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return true;
   }

   protected MonsterType generateInternal(boolean wild) {
      return this.example();
   }
}
