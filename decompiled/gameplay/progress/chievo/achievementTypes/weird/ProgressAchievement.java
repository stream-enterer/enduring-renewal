package com.tann.dice.gameplay.progress.chievo.achievementTypes.weird;

import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Feature;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.util.lang.Words;
import java.util.Arrays;
import java.util.List;

public class ProgressAchievement extends MetaAchievement {
   final transient int num;

   public ProgressAchievement(int num, Unlockable... unlockables) {
      super(nameFor(num), descFor(num), unlockables);
      this.num = num;
   }

   private static String descFor(int num) {
      return "Complete any " + num + " " + Words.plural("achievement", num);
   }

   public static String nameFor(int num) {
      return num + " achievements";
   }

   public static List<ProgressAchievement> makeAll() {
      return Arrays.asList(
         new ProgressAchievement(5, Feature.PARTY_LAYOUT_CHOICE),
         new ProgressAchievement(10, HeroTypeUtils.getAltT1s(1)),
         new ProgressAchievement(15, Feature.EVENTS_SIMPLE),
         new ProgressAchievement(20, HeroTypeUtils.getAltT1s(2)),
         new ProgressAchievement(25, MonsterTypeLib.byNames("log", "shade", "carrier", "golem", "fountain", "warchief", "blind", "chomp", "sarcophagus", "bee")),
         new ProgressAchievement(30, HeroTypeLib.byNames("sinew", "myco", "meddler", "ghast", "wanderer", "keeper", "ace", "agent", "pilgrim")),
         new ProgressAchievement(35, Mode.CUSTOM, Mode.PASTE, Mode.WISH, OptionLib.CUSTOM_REARRANGE, OptionLib.TINY_PASTE, OptionLib.LEVELUP_HORUS_ONLY),
         new ProgressAchievement(40, HeroTypeUtils.fullTacticUnlock()),
         new ProgressAchievement(
            45,
            ItemLib.byeNames(
               "memory",
               "tankard",
               "sorcery notes",
               "compass",
               "Necromancer Tome",
               "Ice Cube",
               "Learn Remedy",
               "Juice",
               "Incense",
               "Corruption",
               "Golden Cup",
               "Ash",
               "egg basket",
               "Syringe",
               "Three of a Kind",
               "Duvet",
               "Cocoon",
               "Chakram",
               "Learn Hack",
               "Apple",
               "duelling pistol",
               "whiskers",
               "erythrocyte",
               "void",
               "door",
               "sling",
               "leaden handle",
               "sceptre",
               "lich eye",
               "eucalyptus",
               "gizmo",
               "crescent shield",
               "lens",
               "scales",
               "infused herbs",
               "collar",
               "origami",
               "first aid kit",
               "leather gloves",
               "bonesaw",
               "abacus",
               "tincture",
               "spinach",
               "buckler"
            )
         ),
         new ProgressAchievement(50, HeroCol.green),
         new ProgressAchievement(60, Feature.NORMAL_TWEAKS, OptionLib.WILD_MODIFIERS),
         new ProgressAchievement(
            70,
            ItemLib.byeNames(
               "polearm",
               "doll",
               "pendulum",
               "coin",
               "clumsy shoes",
               "needle",
               "bandana",
               "pulley",
               "emerald satchel",
               "static tome",
               "square wheel",
               "clef",
               "clumsy hammer",
               "wild seeds",
               "soup",
               "inner strength",
               "alembic",
               "doom blade",
               "full moon",
               "aegis",
               "eggshell",
               "shuriken",
               "triple shuriken",
               "wedding rings",
               "honeycomb",
               "cart",
               "decree",
               "tentacle",
               "infiniheal",
               "jump",
               "telescope",
               "stream",
               "emerald mirror",
               "puzzle box",
               "urn",
               "arrow",
               "whey",
               "liqueur",
               "iron heart",
               "foil",
               "stake",
               "flickering blade",
               "glass blade",
               "bowl",
               "Tower Shield",
               "Botany",
               "Refactor",
               "golden thread",
               "nunchaku",
               "diving suit",
               "mana bomb",
               "flawed diamond",
               "powerstone",
               "demon horn",
               "ladder",
               "blindfold",
               "karma",
               "wax seal",
               "crystallise",
               "Learn Heat",
               "justice",
               "siphon",
               "viscera",
               "wand of wand"
            )
         ),
         new ProgressAchievement(75, Feature.EVENTS_COMPLEX, OptionLib.MYRIAD_OFFERS, OptionLib.PRE_RANDOMISE, OptionLib.INCREASE_PERCENTAGE),
         new ProgressAchievement(80, Mode.INSTANT, Mode.EMPTY, Mode.PICK, Mode.SAVES),
         new ProgressAchievement(90, MonsterTypeLib.byName("Madness")),
         new ProgressAchievement(100),
         new ProgressAchievement(101),
         new ProgressAchievement(102),
         new ProgressAchievement(110, ModifierLib.getAllStartingWith("Mortal").toArray(new Modifier[0])),
         new ProgressAchievement(120, ModifierLib.getAllStartingWith("Divine").toArray(new Modifier[0])),
         new ProgressAchievement(130),
         new ProgressAchievement(140),
         new ProgressAchievement(150),
         new ProgressAchievement(160),
         new ProgressAchievement(170),
         new ProgressAchievement(180),
         new ProgressAchievement(200)
      );
   }

   @Override
   public boolean onAchieveOther(List<Achievement> completedAchievements) {
      return completedAchievements.size() >= this.num;
   }
}
