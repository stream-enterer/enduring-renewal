package com.tann.dice.gameplay.content.ent.type;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.equipRestrict.EquipRestrictCol;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.TextWriter;

public enum HeroCol implements Unlockable {
   orange(Colours.orange),
   yellow(Colours.yellow),
   grey(Colours.grey),
   red(Colours.red),
   blue(Colours.blue),
   green(Colours.green),
   purple(Colours.purple),
   violet(Colours.pink),
   dark(Colours.dark),
   white(Colours.light),
   lime(Colours.fromHex("08d008")),
   cyan(Colours.fromHex("4ed6ec")),
   sea(Colours.fromHex("14397d")),
   amber(Colours.fromHex("ffbf00")),
   mahogany(Colours.fromHex("5e1602")),
   quish("ff4343"),
   zuish("f55c0b"),
   xuish("e8f123"),
   huish("a67060"),
   fuish("c8eca1"),
   tuish("233f23"),
   juish("def8ff"),
   kuish("9e78cf"),
   uuish("ffc4fc"),
   euish("000000"),
   iuish("a8a8a8");

   public final Color col;
   public final String colName;
   static final HeroCol[] basics = new HeroCol[]{orange, yellow, grey, red, blue, green};

   private HeroCol(String hex) {
      this(Colours.fromHex(hex));
   }

   private HeroCol(Color col) {
      this.col = col;
      this.colName = this.name();
   }

   public static HeroCol byName(String name) {
      for (HeroCol value : values()) {
         if (value.colName.equalsIgnoreCase(name) || value.shortName().equalsIgnoreCase(name)) {
            return value;
         }
      }

      return null;
   }

   public static HeroCol randomUnlockedBasic() {
      HeroCol[] bs = basics();

      for (int attempts = 0; attempts < 10; attempts++) {
         HeroCol t = Tann.pick(bs);
         if (!UnUtil.isLocked(t)) {
            return t;
         }
      }

      return yellow;
   }

   public String colourTaggedName(boolean capitalise) {
      String tmp = this.colName;
      if (capitalise) {
         tmp = Words.capitaliseFirst(tmp);
      }

      return TextWriter.getTag(this.col) + tmp + "[cu]";
   }

   public String shortName() {
      return this == green ? "n" : this.colName.charAt(0) + "";
   }

   public static HeroCol[] basics() {
      return basics;
   }

   public long getCollisionBit() {
      switch (this) {
         case orange:
            return Collision.COL_ORANGE;
         case yellow:
            return Collision.COL_YELLOW;
         case grey:
            return Collision.COL_GREY;
         case red:
            return Collision.COL_RED;
         case blue:
            return Collision.COL_BLUE;
         case green:
            return Collision.COL_GREEN;
         case violet:
            return Collision.COL_PINK;
         default:
            TannLog.error("invalid colbit " + this);
            return 0L;
      }
   }

   public boolean isSpelly() {
      return this == blue || this == red;
   }

   @Override
   public Actor makeUnlockActor(boolean big) {
      return new TextWriter(this.colourTaggedName(false), 5000, this.col, 3);
   }

   @Override
   public TextureRegion getAchievementIcon() {
      return EquipRestrictCol.getImage(this);
   }

   @Override
   public String getAchievementIconString() {
      return "c";
   }

   public boolean isBasic() {
      return Tann.contains(basics(), this);
   }
}
