package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.DungeonValueChoosable;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.ModifierType;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MessagePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MissingnoPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.AndChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.EnumChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.LevelupHeroChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.MissingnoChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.OrChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosableRange;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.ReplaceChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.SkipChoosable;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChoosableUtils {
   private static final MissingnoChoosable DEF = new MissingnoChoosable();

   public static String describeList(List<Choosable> options) {
      if (options != null && !options.isEmpty()) {
         List<TP<ModifierType, String>> presets = new ArrayList<>();
         presets.add(new TP<>(ModifierType.Blessing, "[green]blessings[cu]"));
         presets.add(new TP<>(ModifierType.Curse, "[purple]curses[cu]"));
         presets.add(new TP<>(ModifierType.Tweak, "[text]modifiers[cu]"));
         boolean[] valids = new boolean[presets.size()];
         Arrays.fill(valids, true);

         for (Choosable choo : options) {
            for (int i = 0; i < presets.size(); i++) {
               valids[i] &= choo instanceof Modifier && ((Modifier)choo).getMType() == presets.get(i).a;
            }
         }

         for (int i = 0; i < valids.length; i++) {
            if (valids[i]) {
               return (String)presets.get(i).b;
            }
         }

         return allMods(options) ? "modifiers" : "things";
      } else {
         return "invalid list";
      }
   }

   private static boolean allMods(List<Choosable> in) {
      if (in.size() == 0) {
         return false;
      } else {
         for (int i = 0; i < in.size(); i++) {
            for (Choosable oo : in) {
               if (!(oo instanceof Modifier)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public static boolean isDefinitelySingle(Choosable c1) {
      return c1 instanceof LevelupHeroChoosable || c1 instanceof Item || c1 instanceof Modifier;
   }

   public static List<Item> fetchItems(List<Choosable> input) {
      List<Item> result = new ArrayList<>();

      for (Choosable choosable : input) {
         if (choosable instanceof Item) {
            result.add((Item)choosable);
         }
      }

      return result;
   }

   public static boolean shouldBracket(Choosable ch) {
      return ch instanceof AndChoosable || ch instanceof OrChoosable;
   }

   public static boolean isMeta(Choosable ch) {
      return ch instanceof AndChoosable || ch instanceof OrChoosable || ch instanceof RandomTieredChoosable;
   }

   public static boolean collides(Choosable a, long b) {
      return collides(a.getCollisionBits(), b);
   }

   public static boolean collides(Choosable a, Choosable b) {
      if (a instanceof Modifier && b instanceof Modifier) {
         String ae = ((Modifier)a).getEssence();
         if (ae != null && ae.equalsIgnoreCase(((Modifier)b).getEssence())) {
            return true;
         }
      }

      return collides(a.getCollisionBits(), b.getCollisionBits());
   }

   public static boolean collides(long a, long b) {
      return (a & b) != 0L;
   }

   public static Choosable deserialise(String n) {
      return deserialise(n, DEF);
   }

   public static Choosable deserialise(String n, Choosable def) {
      if (n.isEmpty()) {
         return new MissingnoChoosable();
      } else {
         String data = "";
         if (n.length() > 1) {
            data = n.substring(1);
         }

         ChoosableType ct = ChoosableType.fromTag(n.charAt(0));

         try {
            switch (ct) {
               case Levelup:
                  return new LevelupHeroChoosable(HeroTypeUtils.byName(data));
               case Item:
                  return ItemLib.byName(data);
               case Hero:
                  return HeroTypeUtils.byName(data);
               case Modifier:
                  return ModifierLib.byName(data);
               case Random:
                  return RandomTieredChoosable.byName(data);
               case RandomRange:
                  return RandomTieredChoosableRange.byName(data);
               case Or:
                  return OrChoosable.byName(data);
               case And:
                  return AndChoosable.byName(data);
               case Enu:
                  return EnumChoosable.valueOf(data);
               case Value:
                  return new DungeonValueChoosable(data);
               case Replace:
                  return ReplaceChoosable.make(data);
               case Skip:
                  return (Choosable)(SkipChoosable.validData(data) ? new SkipChoosable() : def);
               case MISSINGNO:
                  return (Choosable)(data.isEmpty() ? new MissingnoChoosable() : def);
               default:
                  TannLog.error("invalid choosable: " + n + ", returning " + def);
                  return def;
            }
         } catch (Exception var5) {
            if (TestRunner.isTesting()) {
               TannLog.error("error to choose: " + var5);
               var5.printStackTrace();
               throw var5;
            } else {
               return def;
            }
         }
      }
   }

   public static String serialiseList(List<Choosable> options) {
      return serialiseList(options, "@3");
   }

   public static String serialiseList(List<Choosable> options, String sep) {
      List<String> result = new ArrayList<>();

      for (Choosable option : options) {
         result.add(fullSerialise(option));
      }

      return Tann.commaList(result, sep, sep);
   }

   public static String fullSerialise(Choosable choosable) {
      return choosable.getType().getTag() + choosable.getSaveString();
   }

   public static List<Choosable> deserialiseList(String data) {
      return deserialiseList(data, "@3");
   }

   public static List<Choosable> deserialiseList(String data, String sep) {
      String[] parts = data.split(sep);
      List<Choosable> result = new ArrayList<>();

      for (String part : parts) {
         result.add(deserialise(part));
      }

      return result;
   }

   public static boolean isMissingno(Choosable ch) {
      if (ch instanceof EntType) {
         return ((EntType)ch).isMissingno();
      } else if (ch instanceof Modifier) {
         return ((Modifier)ch).isMissingno();
      } else if (ch instanceof Item) {
         return ((Item)ch).isMissingno();
      } else if (ch instanceof LevelupHeroChoosable) {
         return ((LevelupHeroChoosable)ch).getHeroType().isMissingno();
      } else if (ch instanceof MissingnoChoosable) {
         return true;
      } else if (!(ch instanceof ReplaceChoosable)) {
         if (ch instanceof OrChoosable) {
            OrChoosable och = (OrChoosable)ch;
            return isMissingno(och.getAll());
         } else {
            return false;
         }
      } else {
         ReplaceChoosable rch = (ReplaceChoosable)ch;
         return isMissingno(rch.lose) || isMissingno(rch.gain);
      }
   }

   public static boolean isMissingno(List<Choosable> all) {
      for (int i = 0; i < all.size(); i++) {
         if (isMissingno(all.get(i))) {
            return true;
         }
      }

      return false;
   }

   public static boolean anyCollides(List<Choosable> result) {
      for (int i = result.size() - 1; i >= 0; i--) {
         for (int i1 = i - 1; i1 >= 0; i1--) {
            if (collides(result.get(i), result.get(i1))) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean checkedOnChoose(Choosable ch, DungeonContext dc, String ctx) {
      return checkedOnChoose(Arrays.asList(ch), dc, ctx);
   }

   public static boolean checkedOnChoose(Choosable[] ch, DungeonContext dc, String ctx) {
      return checkedOnChoose(Arrays.asList(ch), dc, ctx);
   }

   public static boolean checkedOnChoose(List<Choosable> ch, DungeonContext dc, String ctx) {
      return checkedOnChoose(ch, dc, ctx, 0);
   }

   public static boolean checkedOnChoose(List<Choosable> ch, DungeonContext dc, String ctx, int index) {
      try {
         for (int i = 0; i < ch.size(); i++) {
            Choosable choosable = ch.get(i);
            choosable.onChoose(dc, index);
         }

         return true;
      } catch (Exception var6) {
         if (DungeonScreen.get() != null && PhaseManager.get() != null) {
            PhaseManager.get().pushPhaseNext(new MessagePhase(var6, ctx));
         }

         var6.printStackTrace();
         return false;
      }
   }

   public static boolean skipRandomReveal(Choosable c) {
      return c instanceof Modifier ? ((Modifier)c).skipNotifyRandomReveal() : false;
   }

   public static Choosable randomStringChoosable() {
      int attempts = 1000000;

      for (int i = 0; i < attempts; i++) {
         String s = randomValidChoosableString(2 + (int)(Math.random() * 10.0));
         Choosable c = deserialise(s, null);
         if (c != null && !isMissingno(c) && !(c instanceof LevelupHeroChoosable)) {
            System.out.println("chosen: " + s);
            return c;
         }
      }

      return new MissingnoChoosable();
   }

   public static Actor brokenActor(String ctx) {
      return new TextWriter("Broken ch actor: " + ctx);
   }

   public static String randomValidChoosableString(int ln) {
      StringBuilder rs = new StringBuilder();

      for (int i = 0; i < ln; i++) {
         rs.append(randomChoosableLetter());
      }

      return rs.toString();
   }

   private static char randomChoosableLetter() {
      switch (Tann.randomInt(4)) {
         case 0:
            return Tann.randomLetter();
         case 1:
            return (char)(65 + (int)(Math.random() * 26.0));
         case 2:
            return (char)(48 + (int)(Math.random() * 10.0));
         case 3:
            return Tann.randomChar("|!$£%^&*()_+-=[]{};'#:@~,./<>?");
         case 4:
            return Tann.randomChar(Tann.random(new String[]{"@4", "@3", "~", "@1", "@2", "&"}));
         default:
            return '|';
      }
   }

   public static boolean isMissingno(Object t) {
      if (t == null) {
         return true;
      } else if (t instanceof Choosable) {
         return isMissingno((Choosable)t);
      } else if (t instanceof MonsterType) {
         return ((MonsterType)t).isMissingno();
      } else if (t instanceof Phase) {
         return t instanceof MissingnoPhase;
      } else {
         TannLog.log("ismiss " + t.getClass().getSimpleName());
         return false;
      }
   }
}
