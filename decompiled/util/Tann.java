package com.tann.dice.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonster;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItem;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeMod;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.panels.Explanel.Explanel;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.BorderText;
import com.tann.dice.util.ui.ScrollPane;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.action.PixAction;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

public class Tann {
   public static int DG_WIDTH = 1280;
   public static int DG_HEIGHT = 760;
   public static Vector2 DG_VECTOR = new Vector2(DG_WIDTH, DG_HEIGHT);
   public static final boolean[] BOTH = new boolean[]{false, true};
   public static final boolean[] BOTH_R = new boolean[]{true, false};
   public static final Boolean[] ALL = new Boolean[]{false, true, null};
   private static final Vector2 tmp = new Vector2();
   public static final float TAU = (float) (Math.PI * 2);
   public static final int INFINITY = 999;
   static HashSet<Object> setTmp = new HashSet<>();
   public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
   static DecimalFormat df = new DecimalFormat("#.##");
   static DecimalFormat pf = new DecimalFormat("#.##%");
   private static int rng_counter = 0;
   public static final Interpolation triangle = new Interpolation() {
      public float apply(float a) {
         return (float)(1.0 - 2.0 * Math.abs(a - 0.5));
      }
   };
   static Vector2 radialTmp = new Vector2();
   public static final int MAX_ERROR_CONTENT = 5000;
   public static final int SCROLL_PANE_BAR_WIDTH = 6;

   public static Vector2 getAbsoluteCoordinates(Actor a) {
      return getAbsoluteCoordinates(a, null);
   }

   public static Vector2 getAbsoluteCoordinates(Actor a, Tann.TannPosition position) {
      tmp.set(0.0F, 0.0F);
      calculateLocalIgnoringScreens(a, tmp);
      if (position == null) {
         return tmp;
      } else {
         switch (position) {
            case Left:
               tmp.add(0.0F, a.getHeight() / 2.0F);
               break;
            case Right:
               tmp.add(a.getWidth(), a.getHeight() / 2.0F);
               break;
            case Top:
               tmp.add(a.getWidth() / 2.0F, a.getHeight());
               break;
            case Bot:
               tmp.add(a.getWidth() / 2.0F, 0.0F);
               break;
            case Center:
               tmp.add(a.getWidth() / 2.0F, a.getHeight() / 2.0F);
         }

         return tmp;
      }
   }

   private static void calculateLocalIgnoringScreens(Actor a, Vector2 tmp) {
      tmp.add(a.getX(), a.getY());
      Group parent = a.getParent();
      if (parent != null && !(parent instanceof Screen)) {
         calculateLocalIgnoringScreens(parent, tmp);
      }
   }

   public static float dist(float x, float y, float x1, float y1) {
      float xDiff = x1 - x;
      float yDiff = y1 - y;
      return (float)Math.sqrt(xDiff * xDiff + yDiff * yDiff);
   }

   public static int between(float a, float b) {
      return (int)(a + (b - a) / 2.0F);
   }

   public static void delayOneFrame(Runnable r) {
      delay(1.0E-4F, r);
   }

   public static void delay(float delay, Runnable runnable) {
      TimerUtil.delay(delay, runnable);
   }

   public static boolean inArray(Object object, Object[] array) {
      return Arrays.asList(array).contains(object);
   }

   public static <T> boolean anySharedItems(List<T> a, List<T> b) {
      for (int i = 0; i < a.size(); i++) {
         T t = a.get(i);
         if (b.contains(t)) {
            return true;
         }
      }

      return false;
   }

   public static <T> List<T> getSharedItems(List<T> a, List<T> b) {
      List<T> result = new ArrayList<>();

      for (int i = 0; i < a.size(); i++) {
         T t = a.get(i);
         if (b.contains(t)) {
            result.add(t);
         }
      }

      return result;
   }

   public static boolean anySharedItems(Object[] array1, Object[] array2) {
      List<Object> listOne = Arrays.asList(array1);

      for (Object o : array2) {
         if (listOne.contains(o)) {
            return true;
         }
      }

      return false;
   }

   public static boolean anySharedItems(int[] array1, int[] array2) {
      for (int i : array2) {
         if (contains(array1, i)) {
            return true;
         }
      }

      return false;
   }

   public static List<AtlasRegion> getRegionsStartingWith(String prefix) {
      return getRegionsStartingWith(com.tann.dice.Main.atlas, prefix);
   }

   public static List<AtlasRegion> getRegionsStartingWith(TextureAtlas atlas, String prefix) {
      List<AtlasRegion> results = new ArrayList<>();
      if (atlas == null) {
         return results;
      } else {
         ArrayIterator var3 = atlas.getRegions().iterator();

         while (var3.hasNext()) {
            AtlasRegion ar = (AtlasRegion)var3.next();
            if (ar.name.startsWith(prefix)) {
               results.add(ar);
            }
         }

         return results;
      }
   }

   public static <T> List<T> iterandom(List<T> list) {
      List copy = new ArrayList<>(list);
      Collections.shuffle(copy);
      return copy;
   }

   public static void center(Actor child) {
      Actor parent = child.getParent();
      child.setPosition((int)(parent.getWidth() / 2.0F - child.getWidth() / 2.0F), (int)(parent.getHeight() / 2.0F - child.getHeight() / 2.0F));
      if (child instanceof Explanel) {
         child.setY(child.getY() + ((Explanel)child).getExtraBelowExtent() / 2);
      }
   }

   public static void randomPos(Actor child) {
      Actor parent = child.getParent();
      child.setPosition((int)random(0.0F, parent.getWidth() - child.getWidth()), (int)random(0.0F, parent.getHeight() - child.getHeight()));
   }

   public static void swap(Object[] array, int i1, int i2) {
      Object o = array[i1];
      array[i1] = array[i2];
      array[i2] = o;
   }

   public static void printAllPositions(Actor a) {
      while (a != null) {
         System.out.println(pad(a.getClass().getSimpleName(), 30) + a.getX() + ":" + a.getY());
         a = a.getParent();
      }
   }

   public static String pad(String str, int length) {
      return pad(str, length, ' ', false);
   }

   public static String pad(String str, int length, char padChar, boolean before) {
      while (str.length() < length) {
         if (before) {
            str = padChar + str;
         } else {
            str = str + padChar;
         }
      }

      return str;
   }

   public static void insertAction(Actor a, Action action) {
      if (action instanceof MoveByAction || action instanceof MoveToAction) {
         removeMoveActions(a);
      }

      a.getActions().insert(0, action);
      action.setActor(a);
   }

   public static void removeMoveActions(Actor actor) {
      for (int i = actor.getActions().size - 1; i >= 0; i--) {
         Action a = (Action)actor.getActions().get(i);
         if (a instanceof MoveToAction || a instanceof MoveByAction) {
            actor.getActions().removeValue(a, true);
         }
      }
   }

   public static <T> void addAll(List<T> list, T... items) {
      list.addAll(Arrays.asList(items));
   }

   public static <T> boolean contains(T[] array, T val) {
      for (int i = 0; i < array.length; i++) {
         T t = array[i];
         if (t == val || t != null && t.equals(val)) {
            return true;
         }
      }

      return false;
   }

   public static boolean contains(int[] array, int val) {
      return indexOf(array, val) != -1;
   }

   public static boolean contains(char[] array, char val) {
      for (int j = 0; j < array.length; j++) {
         char i = array[j];
         if (i == val) {
            return true;
         }
      }

      return false;
   }

   public static Group makeGroup(int width, int height) {
      Group g = new Group();
      g.setTransform(false);
      g.setSize(width, height);
      return g;
   }

   public static Group makeGroup() {
      return makeGroup(0, 0);
   }

   public static Group makeGroup(Actor a) {
      Group g = makeGroup((int)a.getWidth(), (int)a.getHeight());
      g.addActor(a);
      return g;
   }

   public static void drawPatch(Batch batch, Actor actor, NinePatch ninePatch, Color bg, Color border, int gap) {
      drawPatch(batch, (int)actor.getX(), (int)actor.getY(), (int)actor.getWidth(), (int)actor.getHeight(), ninePatch, bg, border, gap);
   }

   public static void drawPatch(Batch batch, float x, float y, float width, float height, NinePatch ninePatch, Color bg, Color border, int gap) {
      batch.setColor(bg);
      Draw.fillRectangle(batch, x + gap, y + gap, width - gap * 2, height - gap * 2);
      batch.setColor(border);
      ninePatch.draw(batch, x, y, width, height);
   }

   public static float length(float xDist, float yDist) {
      return (float)Math.sqrt(xDist * xDist + yDist * yDist);
   }

   public static Action fadeAndRemove(float time) {
      return Actions.sequence(Actions.fadeOut(time), Actions.removeActor());
   }

   public static float getX() {
      return Gdx.input.getX() / com.tann.dice.Main.scale;
   }

   public static float getY() {
      return com.tann.dice.Main.height - Gdx.input.getY() / com.tann.dice.Main.scale;
   }

   public static void setPosition(Actor actor, Vector2 position) {
      actor.setPosition(position.x, position.y);
   }

   public static Action moveTo(Vector2 vector, float duration, Interpolation interpolation) {
      return Actions.moveTo(vector.x, vector.y, duration, interpolation);
   }

   public static Action moveBy(Vector2 vector, float duration, Interpolation interpolation) {
      return Actions.moveBy(vector.x, vector.y, duration, interpolation);
   }

   public static boolean equals(Object a, Object b) {
      return a == b || a != null && a.equals(b);
   }

   public static void assertTrue(boolean b) {
      assertTrue("", b);
   }

   public static <T> void assertEquals(String message, T expected, T actual) {
      if (!equals(expected, actual)) {
         throw new RuntimeException(message + " expected " + expected + " and got " + actual);
      }
   }

   public static void assertTrue(String message, boolean b) {
      if (!b) {
         throw new RuntimeException(message);
      }
   }

   public static void throwEx(String message) {
      assertTrue(message, false);
   }

   public static int indexOf(int[] array, int item) {
      for (int i = 0; i < array.length; i++) {
         if (item == array[i]) {
            return i;
         }
      }

      return -1;
   }

   public static <T> int indexOf(T[] array, T item) {
      for (int i = 0; i < array.length; i++) {
         if (item == array[i]) {
            return i;
         }
      }

      return -1;
   }

   public static int indexOf(String[] array, String item) {
      for (int i = 0; i < array.length; i++) {
         if (item.equals(array[i])) {
            return i;
         }
      }

      return -1;
   }

   public static <T> int indexOfEq(T[] array, T item) {
      for (int i = 0; i < array.length; i++) {
         if (item.equals(array[i])) {
            return i;
         }
      }

      return -1;
   }

   public static <T> void shuffle(T[] ts) {
      Random r = new Random();

      for (int i = 0; i < ts.length; i++) {
         int rand = r.nextInt(ts.length);
         T tmp = ts[i];
         ts[i] = ts[rand];
         ts[rand] = tmp;
      }
   }

   public static void shuffle(int[] levels) {
      Random r = new Random();

      for (int i = 0; i < levels.length; i++) {
         int rand = r.nextInt(levels.length);
         int tmp = levels[i];
         levels[i] = levels[rand];
         levels[rand] = tmp;
      }
   }

   public static Color max(Color max, Color c) {
      return new Color(Math.max(max.r, c.r), Math.max(max.g, c.g), Math.max(max.b, c.b), Math.max(max.a, c.a));
   }

   public static Color min(Color min, Color c) {
      return new Color(Math.min(min.r, c.r), Math.min(min.g, c.g), Math.min(min.b, c.b), Math.min(min.a, c.a));
   }

   public static void finishAllActions(Actor actor) {
      ArrayIterator var1 = actor.getActions().iterator();

      while (var1.hasNext()) {
         Action a = (Action)var1.next();
         a.act(100.0F);
      }
   }

   public static void setAlpha(Actor a, float alpha) {
      Color col = a.getColor();
      a.setColor(col.r, col.g, col.b, alpha);
   }

   public static Group noTransformGroup() {
      Group g = new Group();
      g.setTransform(false);
      return g;
   }

   public static <T> float countInArray(T entry, T[] array) {
      int count = 0;

      for (T t : array) {
         if (t == entry) {
            count++;
         }
      }

      return count;
   }

   public static <T> int countInList(T item, List<T> list) {
      int count = 0;

      for (int i = 0; i < list.size(); i++) {
         T t = list.get(i);
         if (t == item) {
            count++;
         }
      }

      return count;
   }

   public static <T> void addMultiple(List<T> list, T toAdd, int amt, boolean end) {
      for (int i = 0; i < amt; i++) {
         list.add(end ? 0 : list.size(), toAdd);
      }
   }

   public static int countDistinct(List<? extends Object> types) {
      setTmp.clear();
      setTmp.addAll(types);
      return setTmp.size();
   }

   public static <T> boolean hasOnly(List<T> list, T onlyItem) {
      return list.size() == 1 && list.contains(onlyItem);
   }

   public static String delta(int delta) {
      return (delta < 0 ? "" : "+") + delta;
   }

   public static String delta(float delta) {
      return (delta < 0.0F ? "" : "+") + delta;
   }

   public static String getTimeDescription(String submitted_time) {
      try {
         Date d = format.parse(submitted_time);
         long delta = System.currentTimeMillis() - d.getTime();
         long deltaSeconds = delta / 1000L;
         long deltaDays = deltaSeconds / 60L / 60L / 24L;
         if (deltaDays == 0L) {
            return "today";
         } else if (deltaDays < 30L) {
            return deltaDays + " " + Words.plural("day", (int)deltaDays) + " ago";
         } else {
            long deltaMonths = deltaDays / 30L;
            if (deltaMonths < 24L) {
               return deltaMonths + " " + Words.plural("month", (int)deltaMonths) + " ago";
            } else {
               long deltaYears = deltaMonths / 12L;
               return deltaYears + " " + Words.plural("year", (int)deltaYears) + " ago";
            }
         }
      } catch (ParseException var12) {
         var12.printStackTrace();
         return "???";
      }
   }

   public static boolean isMissingno(Unlockable unlockable) {
      return unlockable == PipeItem.getMissingno()
         || unlockable == PipeHero.getMissingno()
         || unlockable == PipeMonster.getMissingno()
         || unlockable == PipeMod.getMissingno();
   }

   public static boolean isMissingnoObject(Object object) {
      return object instanceof Unlockable ? isMissingno((Unlockable)object) : object == null;
   }

   public static void clearDupes(List list) {
      for (int i = list.size() - 1; i >= 0; i--) {
         Object o = list.get(i);
         if (list.indexOf(o) != i) {
            list.remove(i);
         }
      }
   }

   public static int nextPrimeAfter(int value) {
      for (int i = value + 1; i < 999; i++) {
         if (isPrime(i)) {
            return i;
         }
      }

      return 999;
   }

   public static boolean hasParent(Actor actor, Class<? extends Group> parentClass) {
      while (actor.hasParent()) {
         actor = actor.getParent();
         if (parentClass.isInstance(actor)) {
            return true;
         }
      }

      return false;
   }

   public static boolean overlapsSibling(Actor a) {
      if (!a.hasParent()) {
         return false;
      } else {
         SnapshotArray<Actor> sibs = a.getParent().getChildren();
         ArrayIterator var2 = sibs.iterator();

         while (var2.hasNext()) {
            Actor sib = (Actor)var2.next();
            if (a != sib && overlaps(a, sib)) {
               return true;
            }
         }

         return false;
      }
   }

   public static <T extends Actor> T findByClass(Stage stage, Class<T> clazz) {
      return stage == null ? null : findByClass(stage.getRoot(), clazz);
   }

   public static <T extends Actor> T findByClass(Group group, Class<T> clazz) {
      if (group == null) {
         return null;
      } else {
         ArrayIterator var2 = group.getChildren().iterator();

         while (var2.hasNext()) {
            Actor a = (Actor)var2.next();
            if (clazz.isInstance(a)) {
               return (T)a;
            }

            if (a instanceof Group) {
               T t = findByClass((Group)a, clazz);
               if (t != null) {
                  return t;
               }
            }
         }

         return null;
      }
   }

   private static boolean overlaps(Actor a, Actor b) {
      float ax0 = a.getX();
      float ax1 = a.getX() + a.getWidth();
      float ay0 = a.getY();
      float ay1 = a.getY() + a.getHeight();
      float bx0 = b.getX();
      float bx1 = b.getX() + b.getWidth();
      float by0 = b.getY();
      float by1 = b.getY() + b.getHeight();
      return !(ax1 < bx0) && !(ax0 > bx1) && !(ay1 < by0) && !(ay0 > by1);
   }

   public static String floatFormat(float input) {
      return df.format(input);
   }

   public static String percentFormat(float input) {
      return pf.format(input);
   }

   public static <T> T randomElement(List<T> list, Random r) {
      return list.size() == 0 ? null : list.get(r.nextInt(list.size()));
   }

   public static Actor imageWithText(TextureRegion tr, String s) {
      return imageWithText(tr, s, null);
   }

   public static Actor imageWithText(TextureRegion tr, String s, Color border) {
      return actorWithText(new ImageActor(tr), s, border);
   }

   public static Actor actorWithText(Actor a, String s, Color border) {
      return combineActors(a, new BorderText(s, border == null ? Colours.dark : border));
   }

   public static Actor combineImages(TextureRegion tr, TextureRegion tr2) {
      return combineActors(new ImageActor(tr), new ImageActor(tr2));
   }

   public static Group combineActors(Actor a, Actor b) {
      Group g = makeGroup((int)Math.max(a.getWidth(), b.getWidth()), (int)Math.max(a.getHeight(), b.getHeight()));
      g.addActor(a);
      g.addActor(b);
      center(a);
      center(b);
      return g;
   }

   public static <T> List<T> arrayToList(Array<T> entriesArray) {
      return new ArrayList<>(Arrays.asList((T[])entriesArray.toArray()));
   }

   public static List<Integer> intArrayToList(int[] input) {
      List<Integer> result = new ArrayList<>();

      for (int i : input) {
         result.add(i);
      }

      return result;
   }

   public static void uniquify(List result) {
      for (int i = result.size() - 1; i >= 0; i--) {
         if (result.indexOf(result.get(i)) != i) {
            result.remove(i);
         }
      }
   }

   public static int[] from(int a, int b) {
      int[] result = new int[b - a];

      for (int i = 0; i < result.length; i++) {
         result[i] = a + i;
      }

      return result;
   }

   public static int randomInt(int bound) {
      return (int)(Math.random() * bound);
   }

   public static int randomInt(int min, int maxInclusive) {
      return min + (int)(Math.random() * (maxInclusive + 1 - min));
   }

   public static boolean chance(float chance) {
      return Math.random() < chance;
   }

   public static void addListenerFirst(Actor a, EventListener listener) {
      a.getListeners().insert(0, listener);
   }

   public static <T> Map<T, Integer> countMap(List<T> list) {
      Map<T, Integer> result = new HashMap<>();

      for (T t : list) {
         if (result.get(t) == null) {
            result.put(t, 1);
         } else {
            result.put(t, result.get(t) + 1);
         }
      }

      return result;
   }

   public static int totalHeight(List<Actor> acs) {
      int total = 0;

      for (Actor a : acs) {
         total = (int)(total + a.getHeight());
      }

      return total;
   }

   public static int totalWidth(List<Actor> acs) {
      int total = 0;

      for (Actor a : acs) {
         total = (int)(total + a.getWidth());
      }

      return total;
   }

   public static <T> T randomExcept(T[] values, T exclude) {
      int start = (int)(Math.random() * values.length);

      for (int i = 0; i < values.length; i++) {
         int index = (start + i) % values.length;
         if (values[index] != exclude) {
            return values[index];
         }
      }

      return null;
   }

   public static int[] ascending(int amt) {
      int[] result = new int[amt];
      int i = 0;

      while (i < amt) {
         result[i] = i++;
      }

      return result;
   }

   public static void selfPop(final ScrollPane sp) {
      sp.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            com.tann.dice.Main.getCurrentScreen().pop(sp);
            Sounds.playSound(Sounds.pop);
            return super.action(button, pointer, x, y);
         }
      });
   }

   public static <T> List<T> combineLists(List<T> globals, List<T> globals1) {
      List<T> ll = new ArrayList<>();
      ll.addAll(globals);
      ll.addAll(globals1);
      return ll;
   }

   public static Random makeStdRandom() {
      return makeStdRandom(System.currentTimeMillis() + rng_counter++);
   }

   public static Random makeStdRandom(long seed) {
      return new WhiskerRandom(seed);
   }

   public static List<Character> toCharList(String rep) {
      List<Character> result = new ArrayList<>();

      for (char c : rep.toCharArray()) {
         result.add(c);
      }

      return result;
   }

   public static List<Modifier> minList(List<Modifier> result, int sz) {
      return result.size() <= sz ? result : result.subList(0, sz);
   }

   public static <T> T ith(int i, T... ts) {
      return ts[i];
   }

   public static String makeEllipses(String name) {
      return makeEllipses(name, TannFont.guessMaxTextLength(0.2F));
   }

   public static String makeEllipses(String s, int maxLn) {
      if (s.length() <= maxLn) {
         return s;
      } else {
         return maxLn < 3 ? "??" : s.substring(0, maxLn - 3) + "...";
      }
   }

   public static int plusOrMinus() {
      return half() ? 1 : -1;
   }

   public static float zeroToOne(float chance) {
      return Math.max(0.0F, Math.min(1.0F, chance));
   }

   public static void removeNulls(List result) {
      for (int i = result.size() - 1; i >= 0; i--) {
         if (result.get(i) == null) {
            result.remove(i);
         }
      }
   }

   public static boolean further(int test, int than) {
      return than > 0 ? test > than : test < than;
   }

   public static void become(Group g, Actor a) {
      g.clearChildren();
      g.addActor(a);
      g.setSize(a.getWidth(), a.getHeight());
   }

   public static List<String> prefixAll(List<String> src, String prefix) {
      List<String> result = new ArrayList<>();

      for (String descriptionsLine : src) {
         result.add(prefix + descriptionsLine);
      }

      return result;
   }

   public static void assertBads(List bads) {
      String extra = bads.size() == 1 ? "" : "(" + bads.size() + ") ";
      assertTrue("should be no bads: " + extra + bads, bads.isEmpty());
   }

   public static Actor layoutMinArea(List<Actor> actors, int gap) {
      return layoutMinArea(actors, gap, (int)(com.tann.dice.Main.width * 0.9F), (int)(com.tann.dice.Main.height * 0.9F));
   }

   public static Actor makeScrollpaneIfNecessaryHori(Actor a) {
      if (a.getWidth() < com.tann.dice.Main.width * 0.99F) {
         return a;
      } else {
         ScrollPane sp = makeScrollpane(a);
         sp.setHeight(a.getHeight() + 6.0F);
         return sp;
      }
   }

   public static Actor makeScrollpaneIfNecessary(Actor a) {
      if (a.getHeight() < com.tann.dice.Main.height * 0.9F) {
         return a;
      } else {
         ScrollPane sp = makeScrollpane(a);
         sp.setWidth(a.getWidth() + 6.0F);
         return sp;
      }
   }

   public static boolean formsStraight(List<Integer> vals) {
      int oldSize = vals.size();
      uniquify(vals);
      if (oldSize != vals.size()) {
         return false;
      } else if (vals.size() <= 1) {
         return true;
      } else {
         boolean greater = vals.get(1) > vals.get(0);
         int xd = greater ? 1 : -1;

         for (int i = 1; i < vals.size(); i++) {
            if (!Objects.equals(vals.get(i), vals.get(i - 1) + xd)) {
               return false;
            }
         }

         return true;
      }
   }

   public static float bound(float min, float f, float max) {
      return Math.max(min, Math.min(max, f));
   }

   public static String arrayToString(String[] groups, String sep) {
      return commaList(Arrays.asList(groups), sep, sep);
   }

   public static boolean allSame(List l) {
      if (l.isEmpty()) {
         return true;
      } else {
         for (int i = 1; i < l.size(); i++) {
            if (l.get(0) != l.get(i)) {
               return false;
            }
         }

         return true;
      }
   }

   public static float splang(float a, float l, float u) {
      float dist = (a - l) / (u - l);
      return Math.min(1.0F, Math.max(0.0F, dist));
   }

   public static <T> T fallback(T maybeNull, T missingno) {
      return maybeNull == null ? missingno : maybeNull;
   }

   public static int randomRound(float f) {
      return f < 0.0F ? -randomRound(-f) : (int)f + (chance(f % 1.0F) ? 1 : 0);
   }

   public static <T> void addIfNotNull(List<T> list, T item) {
      if (item != null) {
         list.add(item);
      }
   }

   public static int getDigit(int keycode) {
      int digit = keycode - 8;
      if (digit < 0 || digit > 9) {
         digit = keycode - 145;
      }

      return digit >= 0 && digit <= 9 ? digit : -1;
   }

   public static float softLimit(float value, float softLimit, float hardLimit) {
      if (value <= softLimit) {
         return value;
      } else {
         float remainder = hardLimit - softLimit;
         float aboveness = (float)Math.log(1.0F + (value - softLimit));
         float remainderFactor = (float)(1.0 - 1.0 / Math.pow(2.0, aboveness));
         return softLimit + remainder * remainderFactor;
      }
   }

   public static Vector2 randomUnitVector() {
      return new Vector2(1.0F, 0.0F).rotateDeg((float)(Math.random() * 999.0));
   }

   public static <T> T middle(List<T> ts) {
      return ts.get((ts.size() - 1) / 2);
   }

   public static String join(String separator, List<String> parts) {
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < parts.size(); i++) {
         if (i > 0) {
            sb.append(separator);
         }

         sb.append(parts.get(i));
      }

      return sb.toString();
   }

   public static boolean notNullOrEmpty(List l) {
      return l != null && !l.isEmpty();
   }

   public static boolean unequalSquareBRackets(String sub) {
      return countCharsInString('[', sub) != countCharsInString(']', sub);
   }

   public static <T, T2> void addToListMap(Map<T, List<T2>> heroLevelToMaxHp, T key, T2 val) {
      if (heroLevelToMaxHp.get(key) == null) {
         heroLevelToMaxHp.put(key, new ArrayList<>());
      }

      heroLevelToMaxHp.get(key).add(val);
   }

   public static int sum(List<Integer> lst) {
      int total = 0;

      for (int i = 0; i < lst.size(); i++) {
         total += lst.get(i);
      }

      return total;
   }

   public static void thread(Runnable runnable) {
      new Thread(runnable).start();
   }

   private static Vector2 getPos(Actor a, Tann.TannPosition tannPosition, int bonusDistance) {
      switch (tannPosition) {
         case Left:
            tmp.set(-(a.getWidth() + bonusDistance), a.getY());
            break;
         case Right:
            tmp.set(com.tann.dice.Main.width + bonusDistance, a.getY());
            break;
         case Top:
            tmp.set(a.getX(), com.tann.dice.Main.height + bonusDistance);
            break;
         case Bot:
            tmp.set(a.getX(), -(a.getHeight() + bonusDistance));
      }

      return tmp;
   }

   private static Vector2 getPos(Actor a, Tann.TannPosition tannPosition) {
      return getPos(a, tannPosition, 0);
   }

   public static void slideAway(Actor a, Tann.TannPosition tannPosition, boolean remove) {
      slideAway(a, tannPosition, 0, remove);
   }

   public static void slideAway(Actor a, Tann.TannPosition tannPosition, int bonusDistance, boolean remove) {
      a.clearActions();
      Vector2 pos = getPos(a, tannPosition, bonusDistance);
      Action moveTo = PixAction.moveTo((int)pos.x, (int)pos.y, 0.3F, Chrono.i);
      if (remove) {
         a.addAction(Actions.sequence(moveTo, Actions.removeActor()));
      } else {
         a.addAction(moveTo);
      }
   }

   public static Action slideFromTopToCenter(Actor a) {
      int dist = (int)(com.tann.dice.Main.height - a.getHeight()) / 2;
      return slideIn(a, Tann.TannPosition.Top, dist, 0.4F);
   }

   public static Action slideIn(Actor a, Tann.TannPosition tannPosition, int distance) {
      return slideIn(a, tannPosition, distance, 0.4F);
   }

   public static Action slideIn(Actor a, Tann.TannPosition tannPosition, int distance, float duration) {
      a.clearActions();
      Vector2 pos = getPos(a, tannPosition);
      a.setPosition(pos.x, pos.y);
      switch (tannPosition) {
         case Left:
            pos.x = pos.x + (distance + a.getWidth());
            break;
         case Right:
            pos.x = pos.x - (distance + a.getWidth());
            break;
         case Top:
            pos.y = pos.y - (distance + a.getHeight());
            break;
         case Bot:
            pos.y = pos.y + (distance + a.getHeight());
      }

      Action result = PixAction.moveTo((int)pos.x, (int)pos.y, duration, Chrono.i);
      a.addAction(result);
      return result;
   }

   public static float random() {
      return (float)Math.random();
   }

   public static <T> T random(T[] values, Random r) {
      return values[r.nextInt(values.length)];
   }

   public static <T> T random(List<T> values, Random r) {
      return values.get(r.nextInt(values.size()));
   }

   public static float random(float max) {
      return random(0.0F, max);
   }

   public static float random(float min, float max) {
      return min + random() * (max - min);
   }

   public static double random(double min, double max) {
      return min + random() * (max - min);
   }

   public static <T> T random(T[] array) {
      return array[(int)(Math.random() * array.length)];
   }

   public static <T> T random(List<T> list) {
      return list.get((int)(Math.random() * list.size()));
   }

   public static <T> T random(Map<String, T> map) {
      return map.get(random(map.keySet()));
   }

   public static <T> T random(Set<T> set) {
      int index = (int)(random() * set.size());
      Iterator<T> iter = set.iterator();

      for (int i = 0; i < index; i++) {
         iter.next();
      }

      return iter.next();
   }

   public static <E> List<E> pickNRandomElements(List<E> list, int n) {
      if (n > list.size()) {
         throw new ArrayIndexOutOfBoundsException("Trying to get " + n + " element from a list of size " + list.size());
      } else {
         List<E> result = new ArrayList<>(list);
         Collections.shuffle(result);

         while (result.size() > n) {
            result.remove(0);
         }

         return result;
      }
   }

   public static boolean half() {
      return random() < 0.5;
   }

   public static boolean third() {
      return random() < 0.33333334F;
   }

   public static boolean quarter() {
      return random() < 0.25;
   }

   public static <T> T pick(List<T> choices) {
      return choices.get((int)(Math.random() * choices.size()));
   }

   public static <T> T pick(T... choices) {
      return random(choices);
   }

   public static char randomLetter() {
      return (char)(97 + (int)(Math.random() * 26.0));
   }

   public static String randomString(int ln) {
      StringBuilder rs = new StringBuilder();

      for (int i = 0; i < ln; i++) {
         rs.append(randomLetter());
      }

      return rs.toString();
   }

   public static <T> T pick(T a, T b) {
      return Math.random() > 0.5 ? a : b;
   }

   public static <T> T pick(T a, T b, T c) {
      return Math.random() > 0.6667F ? c : pick(a, b);
   }

   public static <T> T pick(T a, T b, T c, T d) {
      return Math.random() > 0.75 ? d : pick(a, b, c);
   }

   public static <T> T pick(T a, T b, T c, T d, T e) {
      return Math.random() > 0.8 ? e : pick(a, b, c, d);
   }

   public static boolean isPrime(int num) {
      if (num >= 2 && num <= 99999) {
         int max = (int)Math.sqrt(num);

         for (int i = 2; i <= max; i++) {
            if (num % i == 0) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static void intify(Vector2 vector2) {
      vector2.x = (int)vector2.x;
      vector2.y = (int)vector2.y;
   }

   public static Vector2 randomRadial(float length) {
      return radialTmp.set(0.0F, length).rotate(random(999.0F));
   }

   public static int countCharsInString(char[] chars, String toCheck) {
      int result = 0;

      for (int i = 0; i < chars.length; i++) {
         result += countCharsInString(chars[i], toCheck);
      }

      return result;
   }

   public static int countStringsInString(String target, String toCheck) {
      if (!toCheck.contains(target)) {
         return 0;
      } else {
         int count = 0;

         for (int i = 0; i < toCheck.length() - target.length() - 1; i++) {
            if (toCheck.substring(i, i + target.length()).equalsIgnoreCase(target)) {
               count++;
            }
         }

         return count;
      }
   }

   public static int countCharsInString(char target, String toCheck) {
      int count = 0;

      for (int i = 0; i < toCheck.length(); i++) {
         if (toCheck.charAt(i) == target) {
            count++;
         }
      }

      return count;
   }

   public static <U, T> T getOrDefault(Map<U, T> map, U key, T def) {
      T val = map.get(key);
      return val != null ? val : def;
   }

   public static void addBlood(
      Group g, int numBlood, float startX, float randStartX, float startY, float randStartY, float moveX, float randMoveX, float moveY, float randMoveY
   ) {
      for (int i = 0; i < numBlood; i++) {
         Actor blood = new Actor() {
            public void draw(Batch batch, float parentAlpha) {
               batch.setColor(this.getColor());
               Draw.fillEllipse(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
               super.draw(batch, parentAlpha);
            }
         };
         blood.setPosition(startX + random(-randStartX, randStartX), startY + random(-randStartY, randStartY));
         float size = random(3.0F, 5.0F);
         blood.setSize(size, size);
         blood.setColor(Colours.red);
         float speed = random(0.3F, 0.4F);
         blood.addAction(
            Actions.sequence(
               Actions.parallel(
                  Actions.moveBy(moveX + random(-randMoveX, randMoveX), moveY + random(-randMoveY, randMoveY), speed), Actions.sizeTo(0.0F, 0.0F, speed)
               ),
               Actions.removeActor()
            )
         );
         g.addActor(blood);
      }
   }

   public static boolean isOver(Actor actor, float x, float y) {
      if (!(x < -99999.0F) && !(y < -99999.0F)) {
         Actor hit = actor.hit(x, y, true);
         return hit != null && hit.isDescendantOf(actor);
      } else {
         return true;
      }
   }

   public static Tann.TannPosition globalOverX(Actor a, float x, float y, float rightThreshold) {
      Vector2 aPos = getAbsoluteCoordinates(a).cpy();
      if (!(x > aPos.x + a.getWidth()) && !(y > aPos.y + a.getHeight()) && !(x < aPos.x) && !(y < aPos.y)) {
         return x > aPos.x + a.getWidth() * rightThreshold ? Tann.TannPosition.Right : Tann.TannPosition.Left;
      } else {
         return null;
      }
   }

   public static Tann.TannPosition globalOver(Actor a, float x, float y, float topThreshold) {
      Vector2 aPos = getAbsoluteCoordinates(a).cpy();
      if (!(x > aPos.x + a.getWidth()) && !(y > aPos.y + a.getHeight()) && !(x < aPos.x) && !(y < aPos.y)) {
         return y > aPos.y + a.getHeight() * topThreshold ? Tann.TannPosition.Top : Tann.TannPosition.Bot;
      } else {
         return null;
      }
   }

   public static String serialise(List<String> list, String separator) {
      String result = "";

      for (String i : list) {
         if (!result.isEmpty()) {
            result = result + separator;
         }

         result = result + i;
      }

      return result;
   }

   public static List<String> deserialiseStringList(String data, String separator) {
      return new ArrayList<>(Arrays.asList(data.split(separator)));
   }

   public static <T, U> Map<T, U> makeMap(TP<T, U>... input) {
      Map result = new HashMap();

      for (TP<T, U> t : input) {
         if (result.get(t.a) != null) {
            throw new RuntimeException("bad map item:" + t.a);
         }

         result.put(t.a, t.b);
      }

      return result;
   }

   public static int tettHash(long seed, int bound) {
      long var3;
      long var4;
      return (int)(
         bound
               * (
                  (
                        (
                              var4 = (
                                    (var3 = (seed ^ (seed << 39 | seed >>> 25) ^ (seed << 14 | seed >>> 50) ^ -3335678366873096957L) * -5840758589994634535L)
                                       ^ (var3 << 40 | var3 >>> 24)
                                       ^ (var3 << 15 | var3 >>> 49)
                                 )
                                 * -2643881736870682267L
                           )
                           ^ var4 >>> 28
                     )
                     & 4294967295L
               )
            >> 32
      );
   }

   public static <T extends Choosable> List<T> getSelectiveRandom(List<T> all, int count, T missingno, List<T>... excludeLists) {
      List<T> result = new ArrayList<>();

      for (int iterIndex = 0; iterIndex < excludeLists.length; iterIndex++) {
         List<T> currentExclude = new ArrayList<>();

         for (int i = 0; i < excludeLists.length - iterIndex; i++) {
            currentExclude.addAll(excludeLists[i]);
         }

         result = getSelectiveRandomSingleExclude(all, count, currentExclude);
         if (result.size() == count) {
            return result;
         }
      }

      if (missingno != null) {
         while (result.size() < count) {
            result.add(missingno);
         }
      }

      return result;
   }

   private static <T extends Choosable> List<T> getSelectiveRandomSingleExclude(List<T> all, int count, List<T> exclude) {
      int RESET_ATTEMPTS = 3;
      int MOD_ADD_ATTEMPTS = count * 30;
      List<T> copy = new ArrayList<>(all);
      copy.removeAll(exclude);
      List<T> result = new ArrayList<>();

      for (int attemptIndex = 0; attemptIndex < 3; attemptIndex++) {
         result.clear();
         Collections.shuffle(copy);

         for (int i = 0; i < MOD_ADD_ATTEMPTS && i < copy.size(); i++) {
            T c = (T)copy.get(i);
            boolean good = true;

            for (int ri = 0; ri < result.size(); ri++) {
               if (ChoosableUtils.collides(result.get(ri), c)) {
                  good = false;
                  break;
               }
            }

            if (good) {
               result.add(c);
               if (result.size() == count) {
                  return result;
               }
            }
         }
      }

      return result;
   }

   public static int hash(int val, String leaderboardName) {
      val += leaderboardName.length() * 100;
      int result = (val + 234) * 23;
      result += result % 349 * 119;
      result += result % 791 * 13;
      result += result % 2131 * 17;
      result += result % 17 * 100000;
      return result + val;
   }

   public static String parseSeconds(long seconds) {
      return parseSeconds(seconds, true);
   }

   public static String parseSeconds(long seconds, boolean full) {
      if (seconds < 0L) {
         return "?s";
      } else {
         long hours = seconds / 3600L;
         long minutes = seconds / 60L % 60L;
         long actualSeconds = seconds % 60L;
         if (full) {
            String result = "";
            if (hours > 0L) {
               result = result + hours + "h ";
            }

            return result + minutes + "m " + actualSeconds + "s";
         } else {
            return hours > 0L ? hours + "h" + minutes + "m" : minutes + "m" + actualSeconds + "s";
         }
      }
   }

   public static <T> String commaList(List<T> list, String bothSeparator) {
      return commaList(list, bothSeparator, bothSeparator);
   }

   public static <T> String commaList(List<T> list, String midSeparator, String finalSeparator) {
      String result = "";

      for (int i = 0; i < list.size(); i++) {
         result = result + list.get(i);
         if (i < list.size() - 2) {
            result = result + midSeparator;
         } else if (i < list.size() - 1) {
            result = result + finalSeparator;
         }
      }

      return result;
   }

   public static <T> String commaList(List<T> list) {
      return commaList(list, ", ", " & ");
   }

   public static <T> String translatedCommaList(List<T> list) {
      List<String> translated = new ArrayList<>(list.size());

      for (T t : list) {
         translated.add(com.tann.dice.Main.t(t.toString()));
      }

      return "[notranslate]" + commaList(translated, ", ", " & ");
   }

   public static String repeat(String s, int amt) {
      return repeat(s, "", amt);
   }

   public static String repeat(String repeat, String sep, int amt) {
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < amt; i++) {
         sb.append(repeat);
         if (i < amt - 1) {
            sb.append(sep);
         }
      }

      return sb.toString();
   }

   public static boolean allBlank(float[] a) {
      for (float f : a) {
         if (f != 0.0F) {
            return false;
         }
      }

      return true;
   }

   public static float asymptote(float pips, float maxValue, float valueAtOne, float slopePower) {
      float tq = (float) (Math.PI / 2);
      return (float)(Math.atan(Math.pow(pips, slopePower) * valueAtOne * tq / maxValue) / tq) * maxValue;
   }

   public static float effectTierOnlySinTote(float pips, float maxPips, float effectTierAtOne, float maxEffectTier, float curvePower) {
      maxPips = Math.max(1.0F, maxPips);
      float someRatio = (pips - 1.0F) / (maxPips - 1.0F);
      if (Float.isNaN(someRatio)) {
         someRatio = 1.0F;
      }

      return pips < 1.0F
         ? Math.max(0.0F, pips) * effectTierAtOne
         : (float)(Math.sin(Math.pow(Math.min(someRatio, 1.0F), curvePower) * Math.PI / 2.0) * (maxEffectTier - effectTierAtOne) + effectTierAtOne);
   }

   public static float niceTerp(float currentValue, float maxVal, float maxStr, float curvePower) {
      return powApply(curvePower, currentValue / maxVal) * maxStr;
   }

   public static float powApply(float power, float a) {
      return (float)Math.pow(a, power);
   }

   public static boolean isInt(String str) {
      try {
         Integer.parseInt(str);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static int compare(boolean x, boolean y) {
      return x == y ? 0 : (x ? 1 : -1);
   }

   public static ScrollPane makeScrollpane(Actor widget) {
      TannStageUtils.ensureSafeForScrollpane(widget);
      ScrollPane sp = new ScrollPane(widget, makeScrollpaneStyle());
      sp.setFadeScrollBars(false);
      if (widget == null) {
         return sp;
      } else {
         sp.setSize(Math.min(widget.getWidth(), (float)(com.tann.dice.Main.width - 50)), Math.min(widget.getHeight(), (float)(com.tann.dice.Main.height - 50)));
         sp.layout();
         sp.setOverscroll(false, false);
         if (sp.isScrollX()) {
            sp.setHeight(sp.getHeight() + 6.0F);
         }

         if (sp.isScrollY()) {
            sp.setWidth(sp.getWidth() + 6.0F);
         }

         return sp;
      }
   }

   public static ScrollPaneStyle makeScrollpaneStyle() {
      return makeScrollpaneStyle(false);
   }

   public static ScrollPaneStyle makeScrollpaneStyle(boolean borderless) {
      NinePatchDrawable knob = new NinePatchDrawable(borderless ? Images.scrollpaneKnobBorderless : Images.scrollpaneKnob);
      NinePatchDrawable bar = new NinePatchDrawable(borderless ? Images.scrollpanePatchBorderless : Images.scrollpanePatch);
      knob.setMinSize(6.0F, 6.0F);
      return new ScrollPaneStyle(null, bar, knob, bar, knob);
   }

   public static Color getHashColour(int hash) {
      Random r = new Random(hash);

      for (int i = 0; i < 5; i++) {
         r.nextInt();
      }

      return new Color(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1.0F);
   }

   public static Group layoutMinArea(List<Actor> actors, int gap, int maxWidth, int maxHeight) {
      return layoutMinArea(actors, gap, maxWidth, maxHeight, 1);
   }

   public static Group layoutMinArea(List<Actor> actors, int gap, int maxWidth, int maxHeight, int align) {
      return layoutMinArea(actors, gap, maxWidth, maxHeight, false, align);
   }

   private static Group layoutMinArea(List<Actor> actors, int gap, int maxWidth, int maxHeight, boolean sort, int align) {
      if (actors.size() == 0) {
         return new TextWriter("borken layout ??");
      } else if (actors.size() == 1) {
         return makeGroup(actors.get(0));
      } else {
         if (sort) {
            Collections.sort(actors, new Comparator<Actor>() {
               public int compare(Actor o1, Actor o2) {
                  return (int)Math.signum(o1.getHeight() - o2.getHeight());
               }
            });
         }

         int bestArea = 99999999;
         int bestWidth = maxWidth;

         for (int w = maxWidth; w > 5; w -= 10) {
            Group g = layoutGroup(gap, w, actors, align);
            if (g.getHeight() > maxHeight) {
               break;
            }

            int area = calcArea(g, 1.5F);
            if (g.getWidth() > g.getHeight() && area < bestArea) {
               bestArea = area;
               bestWidth = w;
            }
         }

         return layoutGroup(gap, bestWidth, actors, align);
      }
   }

   private static Group layoutGroup(int gap, int w, List<Actor> actors, int align) {
      Pixl p = new Pixl();

      for (int i = 0; i < actors.size(); i++) {
         Actor a = actors.get(i);
         if (p.currentRow.elementList.size() > 0 && p.canHandle(a, w)) {
            p.gap(gap);
         }

         p.actor(a, w, gap);
      }

      return p.pix(align);
   }

   public static int calcArea(Actor a, float factor) {
      return (int)(a.getWidth() * a.getHeight()) + (int)Math.pow(Math.max(a.getWidth(), a.getHeight()), factor);
   }

   public static <T> List<T> asList(T... elements) {
      return new ArrayList<>(Arrays.asList(elements));
   }

   public static <T> int getMaxInList(List<T> l) {
      Map<Object, Integer> map = new HashMap<>();

      for (T o : l) {
         if (map.get(o) == null) {
            map.put(o, 1);
         } else {
            map.put(o, map.get(o) + 1);
         }
      }

      int max = 0;

      for (Entry<Object, Integer> i : map.entrySet()) {
         max = Math.max(max, i.getValue());
      }

      return max;
   }

   public static <T> T[] pack(T... ts) {
      return ts;
   }

   public static <T> List<T> packList(T... ts) {
      return Arrays.asList(ts);
   }

   public static <T extends ChanceHaver> T randomChanced(T[] options) {
      return randomChanced(options, 1).get(0);
   }

   public static <T extends ChanceHaver> List<T> randomChanced(T[] options, int amt) {
      return randomChanced(options, amt, new Random());
   }

   public static <T extends ChanceHaver> List<T> randomChanced(T[] options, int amt, Random random) {
      if (options.length < amt) {
         throw new RuntimeException("not enough to find");
      } else {
         float totalChance = 0.0F;

         for (int i = 0; i < options.length; i++) {
            totalChance += options[i].getChance();
         }

         if (totalChance < 0.0F) {
            TannLog.error("negative chance: " + options[0]);
            return Arrays.asList((T[])(new ChanceHaver[]{options[0], options[1]}));
         } else {
            List<T> result = new ArrayList<>();
            int attempts = 1000;

            for (int att = 0; att < 1000 && result.size() < amt; att++) {
               float r = random.nextFloat() * totalChance;

               for (int i = 0; i < options.length; i++) {
                  T pot = options[i];
                  r -= pot.getChance();
                  if (r <= 0.0F) {
                     if (!result.contains(pot)) {
                        result.add(pot);
                     }
                     break;
                  }
               }
            }

            if (result.size() != amt) {
               TannLog.error("randomChanced error: " + options[0]);
            }

            return result;
         }
      }
   }

   public static <T> T caseInsensitiveValueOf(String name, T[] enumVal) {
      for (int i = 0; i < enumVal.length; i++) {
         T t = enumVal[i];
         if ((t + "").toLowerCase().equalsIgnoreCase(name)) {
            return t;
         }
      }

      return null;
   }

   public static int gcd(int p, int q) {
      return q == 0 ? p : gcd(q, p % q);
   }

   public static char randomChar(String s) {
      return s.charAt(randomInt(s.length()));
   }

   public static String halveString(String input) {
      int num = 0;

      for (int i = 0; i < input.length(); i++) {
         char c = input.charAt(i);
         if (c == ' ') {
            num++;
         }
      }

      int which = num / 2;
      num = 0;

      for (int ix = 0; ix < input.length(); ix++) {
         char c = input.charAt(ix);
         if (c == ' ') {
            if (which == num) {
               return input.substring(0, ix) + "[n]" + input.substring(ix + 1);
            }

            num++;
         }
      }

      return input;
   }

   public static enum TannPosition {
      Left,
      Right,
      Top,
      Bot,
      Center;
   }
}
