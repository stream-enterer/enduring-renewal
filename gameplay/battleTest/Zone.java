package com.tann.dice.gameplay.battleTest;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.statics.sound.music.MusicType;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Zone {
   Forest(0, "archer", "goblin", "boar", "rat", "wolf", "grandma", "thorn", "seed", "bee", "log", "chest"),
   Dungeon(
      1,
      "shade",
      "snake",
      "goblin",
      "quartz",
      "ogre",
      "slate",
      "slimer",
      "cyclops",
      "warchief",
      "dragon egg",
      "chest",
      "Illusion",
      "sudul",
      "bandit",
      "barrel",
      "militia",
      "fountain",
      "blind",
      "golem"
   ),
   Catacombs(
      2,
      "shade",
      "zombie",
      "z0mbie",
      "bones",
      "wizz",
      "fanatic",
      "sniper",
      "imp",
      "grave",
      "quartz",
      "ghost",
      "slimer",
      "spiker",
      "sudul",
      "chest",
      "Illusion",
      "banshee",
      "militia",
      "Carrier",
      "fountain",
      "golem"
   ),
   Lair(
      3,
      "chomp",
      "basilisk",
      "sniper",
      "demon",
      "ghost",
      "alpha",
      "blind",
      "ogre",
      "spiker",
      "slate",
      "troll",
      "caw",
      "snake",
      "cyclops",
      "warchief",
      "caw egg",
      "hydra",
      "gnoll",
      "dragon egg",
      "bandit",
      "thorn",
      "Carrier",
      "log",
      "chest"
   ),
   Pit(
      4,
      "chomp",
      "demon",
      "sniper",
      "zombie",
      "z0mbie",
      "wizz",
      "basilisk",
      "spiker",
      "troll",
      "caw",
      "hydra",
      "fanatic",
      "dragon egg",
      "wisp",
      "banshee",
      "barrel",
      "shade",
      "golem"
   ),
   Nightmare(
      5,
      "deathsigil",
      "decaysigil",
      "painsigil",
      "hexia.t.hexia.n.HeHexia",
      "(x2.Spiker).t.chomp.n.SpiChomp",
      "(x2.The Hand).t.Tarantus.n.Harandtula",
      "x2.dragon egg",
      "x2.snake",
      "x2.slate",
      "x3.thorn",
      "x3.bones",
      "x3.blind",
      "x4.basalt",
      "x4.lich",
      "x4.inevitable",
      "x5.basilisk",
      "x5.fanatic",
      "x5.dragon",
      "x6.sniper",
      "x6.wolf",
      "x7.carrier",
      "x7.magrat",
      "x7.slimelet",
      "x8.rat",
      "x8.bell",
      "x9.imp",
      "x9.troll"
   ),
   Snow(-1, "archer", "goblin", "boar", "rat", "wolf", "grandma", "thorn", "seed", "bee", "log"),
   Ice(-1, "archer", "goblin", "boar", "rat", "wolf", "grandma", "thorn", "seed", "bee", "log"),
   All(-1, MonsterTypeLib.stringArray(MonsterTypeLib.getAllValidMonsters()));

   public final int index;
   public final List<TextureRegion> background;
   public final TextureRegion minimap;
   public final List<MonsterType> validMonsters;

   private Zone(int index, String... types) {
      this.index = index;
      this.validMonsters = new ArrayList<>(MonsterTypeLib.listName(types));
      int sz = this.validMonsters.size();
      Tann.uniquify(this.validMonsters);
      if (sz != this.validMonsters.size()) {
         throw new RuntimeException("Duplicate leveltype monster?");
      } else {
         String n = "dungeon/tiling/" + this.name().toLowerCase();
         if (this.name().equalsIgnoreCase("all")) {
            n = "dungeon/tiling/nightmare";
         }

         this.background = new ArrayList<>(Tann.getRegionsStartingWith(com.tann.dice.Main.atlas_big, n));
         if (this.background.isEmpty()) {
            this.background.add(ImageUtils.loadExtBig("dungeon/tiling/missingno"));
         }

         TextureRegion mtmp = ImageUtils.loadExtNull("ui/minimap/" + this.name().toLowerCase());
         this.minimap = mtmp == null ? ImageUtils.loadExt("ui/minimap/missingno") : mtmp;
      }
   }

   public boolean isVertical() {
      return false;
   }

   public TextureRegion getTransition(Zone next) {
      return ImageUtils.loadExtBig("dungeon/tiling/transition/" + this.name().toLowerCase() + "-" + next.name().toLowerCase());
   }

   public static Zone guessFromLevel(int level) {
      return randomWithIndex(level / 4);
   }

   public static Zone randomWithIndex(int index) {
      return randomWithIndex(index, Tann.makeStdRandom());
   }

   public static Zone randomWithIndex(int index, Random r) {
      List<Zone> zones = new ArrayList<>(Arrays.asList(values()));
      Collections.shuffle(zones, r);

      for (int i = 0; i < zones.size(); i++) {
         Zone z = zones.get(i);
         if (z.index == index) {
            return z;
         }
      }

      return null;
   }

   public String getTextButtonString() {
      return this.name();
   }

   public MusicType getMusicType() {
      switch (this) {
         case Forest:
            return MusicType.Forest;
         case Dungeon:
            return MusicType.Dungeon;
         case Catacombs:
            return MusicType.Catacombs;
         case Lair:
            return MusicType.Lair;
         case Pit:
            return MusicType.Pit;
         default:
            return MusicType.Pit;
      }
   }

   public boolean isClassic() {
      switch (this) {
         case Forest:
         case Dungeon:
         case Catacombs:
         case Lair:
         case Pit:
            return true;
         default:
            return false;
      }
   }
}
