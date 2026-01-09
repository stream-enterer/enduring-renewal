package com.tann.dice.gameplay.content.ent.group;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.global.chance.RarityUtils;
import com.tann.dice.util.ChanceHaver;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum PartyLayoutType implements ChanceHaver {
   Basic(RarityUtils.IGNORED_RARITY, HeroCol.orange, HeroCol.yellow, HeroCol.grey, HeroCol.red, HeroCol.blue),
   Greens(1.0F, HeroCol.green, HeroCol.green, HeroCol.grey, HeroCol.orange, HeroCol.yellow),
   Force(1.0F, HeroCol.yellow, HeroCol.yellow, HeroCol.orange, HeroCol.grey, HeroCol.grey),
   Magical(0.8F, HeroCol.red, HeroCol.red, HeroCol.blue, HeroCol.green, HeroCol.orange),
   Flex(0.7F, HeroCol.green, HeroCol.yellow, null, HeroCol.blue, HeroCol.red),
   Mountain(0.3F, HeroCol.blue, HeroCol.blue, HeroCol.grey, HeroCol.grey, HeroCol.green),
   Defensive(0.2F, HeroCol.red, HeroCol.red, HeroCol.grey, HeroCol.grey, HeroCol.yellow),
   RNG(0.2F, null, null, null, null, null),
   Strays(0.1F, HeroCol.grey, HeroCol.orange, HeroCol.orange, null, null),
   Cisab(0.1F, HeroCol.blue, HeroCol.red, HeroCol.grey, HeroCol.yellow, HeroCol.orange),
   Rush(0.05F, HeroCol.blue, HeroCol.blue, HeroCol.orange, HeroCol.grey, HeroCol.yellow),
   Surrounded(null, null, HeroCol.red, null, null),
   DMLS(HeroCol.red, HeroCol.red, HeroCol.grey, HeroCol.red, HeroCol.red),
   Sunset(HeroCol.blue, HeroCol.orange, HeroCol.yellow, null, HeroCol.green),
   CornCob(HeroCol.yellow, HeroCol.yellow, HeroCol.yellow, HeroCol.yellow, HeroCol.yellow),
   Beach(HeroCol.blue, HeroCol.blue, HeroCol.yellow, HeroCol.yellow, HeroCol.yellow),
   Sword(HeroCol.orange, HeroCol.orange, HeroCol.grey, HeroCol.grey, HeroCol.grey),
   LampPost(null, HeroCol.yellow, HeroCol.grey, HeroCol.grey, HeroCol.grey),
   Strata(0.3F, HeroCol.green, HeroCol.green, HeroCol.orange, HeroCol.orange, HeroCol.grey),
   Bacsi(HeroCol.orange, HeroCol.yellow, HeroCol.blue, HeroCol.grey, HeroCol.red),
   Casia(HeroCol.blue, HeroCol.yellow, HeroCol.grey, HeroCol.red, HeroCol.yellow),
   Col(HeroCol.yellow, HeroCol.yellow, HeroCol.yellow, HeroCol.blue, HeroCol.red),
   Nor(HeroCol.red, null, HeroCol.blue, null, HeroCol.red),
   Fries(PAR(), HeroCol.yellow, HeroCol.yellow, HeroCol.red, HeroCol.red, HeroCol.red),
   Mango(PAR(), HeroCol.green, HeroCol.yellow, HeroCol.orange, HeroCol.orange, HeroCol.red),
   Watermelon(PAR(), HeroCol.green, HeroCol.red, HeroCol.red, HeroCol.red, HeroCol.green),
   Gold(PAR(), HeroCol.grey, HeroCol.yellow, HeroCol.yellow, HeroCol.yellow, HeroCol.grey),
   Casters(PAR(), HeroCol.blue, HeroCol.red, HeroCol.blue, HeroCol.red, HeroCol.blue),
   Fire(PAR(), HeroCol.red, HeroCol.red, HeroCol.orange, HeroCol.orange, HeroCol.yellow),
   Raspberry(PAR(), HeroCol.red, HeroCol.red, HeroCol.red, HeroCol.red, HeroCol.red),
   Wall(PAR(), HeroCol.grey, HeroCol.red, HeroCol.grey, HeroCol.red, HeroCol.grey),
   Bbbbb(PAR(), HeroCol.orange, HeroCol.orange, HeroCol.orange, HeroCol.orange, HeroCol.orange),
   Core(PAR(), HeroCol.green, HeroCol.grey, HeroCol.red, HeroCol.grey, HeroCol.green),
   Sharp(PAR(), HeroCol.orange, HeroCol.yellow, HeroCol.orange, HeroCol.yellow, HeroCol.orange),
   Tricky(PAR(), HeroCol.orange, HeroCol.orange, HeroCol.blue, HeroCol.green, HeroCol.green),
   Basss(PAR(), HeroCol.orange, HeroCol.yellow, HeroCol.grey, HeroCol.grey, HeroCol.grey),
   Storm(PAR(), HeroCol.grey, HeroCol.grey, null, HeroCol.blue, HeroCol.blue),
   Sunny(PAR(), HeroCol.blue, HeroCol.yellow, HeroCol.blue, HeroCol.green, HeroCol.green),
   Guarded(PAR(), HeroCol.grey, HeroCol.grey, HeroCol.green, HeroCol.grey, HeroCol.grey),
   Blueberyy(PAR(), HeroCol.blue, HeroCol.blue, HeroCol.blue, HeroCol.blue, HeroCol.blue),
   Checkers(PAR(), null, HeroCol.grey, null, HeroCol.grey, null),
   Peas(PAR(), HeroCol.green, HeroCol.green, HeroCol.green, HeroCol.green, HeroCol.green),
   Tree(PAR(), HeroCol.green, HeroCol.green, HeroCol.orange, HeroCol.orange, HeroCol.orange),
   Flower(PAR(), HeroCol.red, HeroCol.yellow, HeroCol.red, HeroCol.green, HeroCol.green),
   Brazil(PAR(), HeroCol.green, HeroCol.yellow, HeroCol.blue, HeroCol.yellow, HeroCol.green);

   private final float chance;
   private final HeroCol[] cols;

   private PartyLayoutType(float chance, HeroCol... cols) {
      this.chance = chance;
      this.cols = cols;
   }

   private PartyLayoutType(HeroCol... cols) {
      this(GAR(), cols);
   }

   public static float GAR() {
      return 0.0012F;
   }

   public static float PAR() {
      return 0.001F;
   }

   public static PartyLayoutType guessLayout(List<HeroType> types) {
      for (PartyLayoutType value : values()) {
         if (types.size() == value.cols.length) {
            boolean good = true;

            for (int i = 0; i < value.cols.length; i++) {
               if (types.get(i).heroCol != value.cols[i]) {
                  good = false;
                  break;
               }
            }

            if (good) {
               return value;
            }
         }
      }

      return RNG;
   }

   private static List<PartyLayoutType> makeAllUnlocked() {
      PartyLayoutType[] vals = values();
      List<PartyLayoutType> result = new ArrayList<>();

      for (int i = 0; i < vals.length; i++) {
         if (!vals[i].isLockedMeta()) {
            result.add(vals[i]);
         }
      }

      return result;
   }

   public static PartyLayoutType[] exceptBasic() {
      List<PartyLayoutType> rs = makeAllUnlocked();
      rs.remove(Basic);
      return rs.toArray(new PartyLayoutType[0]);
   }

   public HeroCol[] getColsInstance() {
      HeroCol[] result = new HeroCol[this.cols.length];

      for (int i = 0; i < this.cols.length; i++) {
         result[i] = this.cols[i];
         if (result[i] == null) {
            result[i] = HeroCol.randomUnlockedBasic();
         }
      }

      return result;
   }

   public Actor visualise() {
      int ww = Math.min(54, com.tann.dice.Main.width / 4);
      return this.addRarityIfNecessary(new Pixl(3, 4).forceWidth(ww).border(Colours.grey).text(this.name())).row().rowedActors(makeRectactors(this.cols)).pix();
   }

   public Pixl addRarityIfNecessary(Pixl p) {
      if (OptionLib.SHOW_RARITY.c()) {
         if (this.getChance() == RarityUtils.IGNORED_RARITY) {
            p.row().text("[grey]-------");
         } else {
            p.row().text("[grey]r: " + this.getChance());
         }
      }

      return p;
   }

   public static List<? extends Actor> makeRectactors(HeroCol[] cols) {
      List<Actor> result = new ArrayList<>();

      for (HeroCol col : cols) {
         Rectactor r = makeRectactor(col);
         if (col == null) {
            result.add(Tann.actorWithText(r, "[light]?", Colours.dark));
         } else {
            result.add(r);
         }
      }

      return result;
   }

   private static Color getColSafe(HeroCol hc) {
      return hc == null ? Colours.light : hc.col;
   }

   private static Rectactor makeRectactor(HeroCol heroCol) {
      Color col = getColSafe(heroCol);
      int rectSize = 10;
      return new Rectactor(10, 10, col, col);
   }

   @Override
   public float getChance() {
      return this.chance;
   }

   public int length() {
      return this.cols.length;
   }

   public static PartyLayoutType random() {
      return Tann.randomChanced(values());
   }

   public long getBannedCollisionBits(boolean spellRequiresTwo) {
      return Party.getBannedCollisionBits(Arrays.asList(this.cols), spellRequiresTwo);
   }

   public boolean isLockedMeta() {
      for (int i = 0; i < this.cols.length; i++) {
         if (UnUtil.isLocked(this.cols[i])) {
            return true;
         }
      }

      return false;
   }

   public Actor visualiseTiny() {
      int s = 1;
      List<Actor> actors = new ArrayList<>();

      for (HeroCol col : this.cols) {
         actors.add(new Rectactor(1, 1, getColSafe(col)));
      }

      return new Pixl().rowedActors(actors).pix();
   }
}
