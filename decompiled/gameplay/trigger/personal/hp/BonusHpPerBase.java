package com.tann.dice.gameplay.trigger.personal.hp;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.modifier.modBal.ModTierUtils;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.HpGrid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BonusHpPerBase extends Personal {
   final int bonus;
   final int per;

   public BonusHpPerBase(int per) {
      this(1, per);
   }

   public BonusHpPerBase(int bonus, int per) {
      this.bonus = bonus;
      this.per = per;
   }

   public static void debugInfo() {
      List<TP<Integer, String>> result = new ArrayList<>();

      for (int x = 1; x < 10; x++) {
         for (int y = 1; y < 10; y++) {
            float val = ModTierUtils.extraMonsterHP(ModTierUtils.getBonusMonsterHpRatio(x, y));
            if (ModTierUtils.validForTier(val)) {
               result.add(new TP<>(Math.round(val), x + "/" + y));
            }
         }
      }

      Collections.sort(result, new Comparator<TP<Integer, String>>() {
         public int compare(TP<Integer, String> o1, TP<Integer, String> o2) {
            return o2.a - o1.a;
         }
      });
      System.out.println(result);

      for (TP<Integer, String> integerStringTP : result) {
         System.out.println(integerStringTP.b);
      }
   }

   @Override
   public String describeForSelfBuff() {
      return Tann.delta(this.bonus) + " hp for each " + this.per + " hp";
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl plus = new Pixl(0);

      for (int i = 0; i < this.bonus; i++) {
         plus.image(Images.hp, Colours.red);
         if (i < this.bonus - 1) {
            plus.gap(1);
         }
      }

      Actor a = new Pixl().image(Images.plus, Colours.light).gap(1).actor(plus.pix()).gap(2).text("/").gap(2).actor(HpGrid.make(this.per, this.per)).pix();
      if (OptionLib.MOD_CALC.c()) {
         float estVal = ModTierUtils.extraMonsterHP(ModTierUtils.getBonusMonsterHpRatio(this.bonus, this.per));
         a = new Pixl(2).actor(a).row().text(Tann.floatFormat(estVal)).pix();
      }

      return a;
   }

   @Override
   public int getBonusMaxHp(int maxHp, EntState state) {
      int thisPer = state.getMaxHp() / this.per;
      return this.bonus * thisPer;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.hpFor(player);
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }

   @Override
   public float getPriority() {
      return -10.0F;
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }

   @Override
   public String hyphenTag() {
      return this.bonus + "/" + this.per;
   }
}
