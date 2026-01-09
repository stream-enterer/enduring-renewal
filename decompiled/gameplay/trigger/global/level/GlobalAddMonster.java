package com.tann.dice.gameplay.trigger.global.level;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.lang.Words;
import java.util.Arrays;
import java.util.List;

public class GlobalAddMonster extends Global {
   final MonsterType[] types;
   final boolean multiSame;

   public GlobalAddMonster(MonsterType... types) {
      this.types = types;
      this.multiSame = allSame(types) && types.length > 2;
   }

   private static boolean allSame(Object[] array) {
      for (int i = 1; i < array.length; i++) {
         if (array[0] != array[i]) {
            return false;
         }
      }

      return true;
   }

   public GlobalAddMonster(MonsterType type, int amt) {
      this(ma(amt, type));
   }

   private static MonsterType[] ma(int amt, MonsterType type) {
      MonsterType[] types = new MonsterType[amt];
      Arrays.fill(types, type);
      return types;
   }

   @Override
   public String describeForSelfBuff() {
      String start = "Add";
      String end = "to each fight";
      if (this.multiSame) {
         return start + " " + this.types.length + "x " + this.types[0].getName(true) + " " + end;
      } else {
         String result = start + " ";

         for (int i = 0; i < this.types.length; i++) {
            String monName = this.types[i].getName(true).toLowerCase();
            result = result + Words.singular(monName);
            if (i < this.types.length - 2) {
               result = result + ", ";
            } else if (i < this.types.length - 1) {
               result = result + " and ";
            }
         }

         return result + " " + end;
      }
   }

   @Override
   public void affectStartMonsters(List<Monster> monsters) {
      for (MonsterType t : this.types) {
         monsters.add(t.makeEnt());
      }
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return this.makeBaseActor(big);
   }

   protected Actor makeBaseActor(boolean big) {
      int gap = 2;
      if (big) {
         Pixl p = new Pixl(0);
         if (this.multiSame) {
            EntPanelInventory dp = new EntPanelInventory(this.types[0].makeEnt());
            dp.removeDice();
            Actor a = dp.getFullActor();
            p.actor(a, com.tann.dice.Main.width / 2);
         } else {
            for (MonsterType t : this.types) {
               EntPanelInventory dp = new EntPanelInventory(t.makeEnt());
               dp.removeDice();
               Actor a = dp.getFullActor();
               p.actor(a, com.tann.dice.Main.width / 2);
            }
         }

         return p.pix();
      } else {
         Pixl p = new Pixl(0);
         p.text(Words.plusString(true)).gap(2);
         if (this.multiSame) {
            p.text(this.types.length + "x ").actor(this.makePort(this.types[0].portrait));
         } else {
            for (MonsterType t : this.types) {
               p.actor(this.makePort(t.portrait));
            }
         }

         return p.pix();
      }
   }

   private Actor makePort(TextureRegion portrait) {
      return new ImageActor(portrait, true);
   }

   @Override
   public void onPick(DungeonContext context) {
      this.resetFightLogOnPick();
   }

   @Override
   public boolean skipTest() {
      return true;
   }

   public static Modifier makeGenerated(MonsterType mt) {
      boolean allowUnique = true;
      if (mt.isMissingno()) {
         return null;
      } else {
         if (mt.isUnique()) {
         }

         float typeStrengthMul = 0.365F;
         float strengthPow = 1.37F;
         float aprroxStr = mt.getSummonValueForModifier();
         float tier;
         if (aprroxStr > 0.0F) {
            tier = (float)(Math.pow(aprroxStr, 1.37F) * 0.365F * 1.0);
         } else {
            tier = (mt.getAvgEffectTier() + mt.getEffectiveHp()) * 1.0F;
         }

         String mn = mt.getName();
         String name = "add." + mn;
         return new Modifier(-tier, name, new GlobalAddMonster(mt, 1));
      }
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return MonsterTypeLib.getCollision(this.types);
   }
}
