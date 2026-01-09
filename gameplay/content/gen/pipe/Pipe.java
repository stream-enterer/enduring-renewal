package com.tann.dice.gameplay.content.gen.pipe;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonster;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItem;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeCache;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeMod;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.screens.dungeon.panels.book.page.stuffPage.PipeType;
import com.tann.dice.screens.generalPanels.TextUrl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.bsRandom.Supplier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

public abstract class Pipe<T> implements Supplier<T> {
   public static final String ALL_THIS = "[red]TextMod[cu]";
   private static final int MAX_LN = 20000;
   public static Pattern DANGEROUS_NONMODIFIER_PIPE_CHARS = Pattern.compile("[^,]*");
   static int recursion = -1;
   static final int MAX_PIPE_RECURSION = 7000;
   static final int slowMax = 2;
   private static final String UNDOC = "undocumented";

   public T get(String name) {
      if (this.nameBad(name)) {
         return null;
      } else {
         return this.nameValid(name) ? this.make(name) : null;
      }
   }

   protected boolean nameBad(String name) {
      return name.length() > 20000;
   }

   protected abstract T make(String var1);

   protected abstract boolean nameValid(String var1);

   public static void setupChecks() {
      recursion = 0;
      com.tann.dice.Main.self().translator.disable();
   }

   public static void disableChecks() {
      recursion = -1;
      com.tann.dice.Main.self().translator.enable();
   }

   public static boolean aboveMaxRecursion() {
      return recursion >= 7000;
   }

   private static <T> T checkPipes(List<Pipe<T>> pipes, String val) {
      if (recursion != -1) {
         if (OptionLib.PRINT_PIPE.c()) {
            TannLog.log(recursion + ":" + val);
         }

         recursion++;
         if (aboveMaxRecursion()) {
            TannLog.error("pipe recursion limit");
            return null;
         }
      }

      for (int i = 0; i < pipes.size(); i++) {
         try {
            T t = pipes.get(i).get(val);
            if (t != null) {
               return t;
            }
         } catch (Exception var4) {
         }
      }

      return null;
   }

   @Nonnull
   public static <T> T checkPipes(List<Pipe<T>> pipes, String val, PipeCache<T> cache, T missingno) {
      if (cache.willNull(val)) {
         return missingno;
      } else if (val.contains(".") && PipeUtils.unbalancedBrackets(val)) {
         return missingno;
      } else {
         T t = checkPipes(pipes, val);
         if (t == null) {
            if (!aboveMaxRecursion()) {
               cache.cacheNull(val);
            }

            return missingno;
         } else {
            if (pipes.get(0).get(val) == null) {
               cache.cache(t);
            }

            return t;
         }
      }
   }

   public abstract T example();

   public List<T> examples(int amt) {
      if (this.isSlow()) {
         amt = Math.min(2, amt);
      }

      List<T> result = new ArrayList<>();

      for (int i = 0; i < amt * 10; i++) {
         T t = this.example();
         if (t != null) {
            result.add(t);
            if (result.size() == amt) {
               break;
            }
         }
      }

      return result;
   }

   protected static boolean bad(String... check) {
      for (int i = 0; i < check.length; i++) {
         if (bad(check[i])) {
            return true;
         }
      }

      return false;
   }

   protected static boolean bad(String check) {
      return check == null || check.isEmpty();
   }

   protected static boolean badInt(String check) {
      return !Tann.isInt(check);
   }

   public static boolean badInt(String... check) {
      for (int i = 0; i < check.length; i++) {
         if (badInt(check[i])) {
            return true;
         }
      }

      return false;
   }

   @Override
   public T supply() {
      return this.example();
   }

   public boolean isTransformative() {
      return false;
   }

   public boolean canGenerate(boolean wild) {
      return false;
   }

   public final T generate(boolean wild) {
      return !this.canGenerate(wild) ? null : this.generateInternal(wild);
   }

   protected T generateInternal(boolean wild) {
      return null;
   }

   public float getRarity(boolean wild) {
      return 1.0F;
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }

   public static List<Pipe> makeAllPipes() {
      List<Pipe> result = new ArrayList<>();
      result.addAll(PipeMod.pipes);
      result.addAll(PipeItem.pipes);
      result.addAll(PipeMonster.pipes);
      result.addAll(PipeHero.pipes);
      return result;
   }

   public boolean isSlow() {
      return false;
   }

   public String document() {
      return "undocumented";
   }

   public boolean isHiddenAPI() {
      return this.document() == "undocumented";
   }

   public boolean isComplexAPI() {
      return false;
   }

   public PipeType getPipeType() {
      Object o = null;

      for (int i = 0; i < 20; i++) {
         o = this.example();
         if (o != null) {
            break;
         }
      }

      if (o instanceof Modifier) {
         return PipeType.Modifier;
      } else if (o instanceof Item) {
         return PipeType.Item;
      } else if (o instanceof MonsterType) {
         return PipeType.Monster;
      } else if (o instanceof HeroType) {
         return PipeType.Hero;
      } else {
         String nm = this.getClass().getSimpleName();
         if (nm.startsWith("PipeHero")) {
            return PipeType.Hero;
         } else if (nm.startsWith("PipeMonster")) {
            return PipeType.Monster;
         } else if (nm.startsWith("PipeMod")) {
            return PipeType.Modifier;
         } else {
            return nm.startsWith("PipeItem") ? PipeType.Item : null;
         }
      }
   }

   public boolean isTexturey() {
      return false;
   }

   public boolean isRawTexture() {
      return this.document().contains("tx");
   }

   public String getIdTag() {
      return this.document();
   }

   public boolean showHigher() {
      return false;
   }

   public Actor getExtraActor() {
      return this.isRawTexture() ? TextUrl.make("[pink]tx[cu] tool", "https://tann.fun/things/dice-img") : null;
   }

   public static void clearCache() {
      PipeMod.clearCache();
      PipeHero.clearCache();
      PipeMonster.clearCache();
      PipeItem.clearCache();
   }
}
