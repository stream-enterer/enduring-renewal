package com.tann.dice.statics.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.platform.audio.SoundHandler;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Sounds {
   public static AssetManager am;
   public static Array<Sound> sounds = new Array();
   public static String[] clacks;
   public static String[] clocks;
   public static String[] lock;
   public static String[] unlock;
   public static String[] clangs;
   public static String[] punches;
   public static String[] slash;
   public static String[] blocks;
   public static String[] heals;
   public static String[] magic;
   public static String[] spike;
   public static String[] boost;
   public static String[] deboost;
   public static String[] stealth;
   public static String[] smith;
   public static String[] whistle;
   public static String[] song;
   public static String[] onRoll;
   public static String[] undying;
   public static String[] resurrect;
   public static String[] pip;
   public static String[] pipSmall;
   public static String[] pop;
   public static String[] error;
   public static String[] confirm;
   public static String[] undo;
   public static String[] paper;
   public static String[] impacts;
   public static String[] arrowFly;
   public static String[] arrowWobble;
   public static String[] poison;
   public static String[] poisonImpact;
   public static String[] regen;
   public static String[] regenActivate;
   public static String[] dust;
   public static String[] slice;
   public static String[] swing;
   public static String[] lightning;
   public static String[] beam;
   public static String[] fire;
   public static String[] iceExplode;
   public static String[] bitesmall;
   public static String[] biteReg;
   public static String[] biteBig;
   public static String[] biteHuge;
   public static String[] slimeMovesmall;
   public static String[] slimeMoveBig;
   public static String[] slimeMoveHuge;
   public static String[] slam;
   public static String[] tribolt;
   public static String[] bats;
   public static String[] fireBreath;
   public static String[] poisonBreath;
   public static String[] thwack;
   public static String[] summonGeneric;
   public static String[] summonWolf;
   public static String[] summonBones;
   public static String[] summonImp;
   public static String[] deathHero;
   public static String[] deathPew;
   public static String[] deathReg;
   public static String[] deathBig;
   public static String[] deathExplosion;
   public static String[] deathDragonLong;
   public static String[] deathOof;
   public static String[] deathCute;
   public static String[] deathScream;
   public static String[] deathSqueak;
   public static String[] deathAlien;
   public static String[] deathDemon;
   public static String[] deathHorse;
   public static String[] deathSpawn;
   public static String[] deathWeird;
   public static String[] flap;
   public static String[] flee;
   public static String[] surr;
   public static String[] clink;
   public static String[] slime;
   public static String[] gong;
   public static String[] wail;
   public static String[] chip;
   public static String[] defeat;
   public static String[] victory;
   public static String[] chooseItem;
   public static String[] pickup;
   public static String[] drop;
   public static List<String> allStrings = new ArrayList<>();
   private static SoundHandler soundHandler;
   public static HashMap<String, Sound> soundMap = new HashMap<>();
   static HashMap<String[], Long> timeLastPlayedMap = new HashMap<>();
   private static final long repeatTime = 50L;
   private static boolean soundEnabled = true;

   public static void disposeAll() {
      for (Sound s : soundMap.values()) {
         s.dispose();
      }

      clearCaches();
   }

   public static void clearCaches() {
      soundMap = new HashMap<>();
   }

   public static void setup(SoundHandler soundHandler) {
      Sounds.soundHandler = soundHandler;
      am = new AssetManager();
      clacks = makeSounds("dice/clack");
      clocks = makeSounds("dice/clock");
      lock = makeSounds("dice/fwt");
      unlock = makeSounds("dice/twf");
      clangs = makeSounds("combat/clang");
      impacts = makeSounds("combat/impact");
      slash = makeSounds("combat/slash");
      swing = makeSounds("combat/swing");
      punches = makeSounds("combat/punch");
      blocks = makeSounds("combat/block");
      heals = makeSounds("combat/heal");
      magic = makeSounds("combat/mystic");
      boost = makeSounds("combat/boost");
      deboost = makeSounds("combat/deboost");
      stealth = makeSounds("combat/stealth");
      spike = makeSounds("combat/spike");
      arrowFly = makeSounds("combat/arrowFly");
      arrowWobble = makeSounds("combat/arrowWobble");
      slam = makeSounds("combat/slam");
      thwack = makeSounds("combat/thwack");
      slice = makeSounds("combat/spell/slice");
      lightning = makeSounds("combat/spell/lightning");
      beam = makeSounds("combat/spell/beam");
      fire = makeSounds("combat/spell/fire");
      iceExplode = makeSounds("combat/spell/iceExplode");
      smith = makeSounds("combat/specialSide/smith");
      whistle = makeSounds("combat/specialSide/whistle");
      song = makeSounds("combat/specialSide/song");
      onRoll = makeSounds("combat/specialSide/onRoll");
      undying = makeSounds("combat/specialSide/undying");
      resurrect = makeSounds("combat/specialSide/resurrect");
      tribolt = makeSounds("combat/specialSide/tribolt");
      bats = makeSounds("combat/specialSide/bats");
      fireBreath = makeSounds("combat/specialSide/fireBreath");
      poisonBreath = makeSounds("combat/specialSide/poisonBreath");
      summonWolf = makeSounds("combat/specialSide/summon/wolf");
      summonBones = makeSounds("combat/specialSide/summon/bones");
      summonGeneric = makeSounds("combat/specialSide/summon/generic");
      summonImp = makeSounds("combat/specialSide/summon/imp");
      poison = makeSounds("combat/poison/poison");
      poisonImpact = makeSounds("combat/poison/poisonImpact");
      surr = makeSounds("combat/surr/surr");
      regen = makeSounds("combat/regen/regen");
      regenActivate = makeSounds("combat/regen/regenActivate");
      pip = makeSounds("ui/pip");
      pipSmall = makeSounds("ui/pipSmall");
      pop = makeSounds("ui/pop");
      error = makeSounds("ui/error");
      confirm = makeSounds("ui/confirm");
      undo = makeSounds("ui/undo");
      paper = makeSounds("ui/paper");
      dust = makeSounds("ui/dust");
      bitesmall = makeSounds("combat/bite/biteSmall");
      biteReg = makeSounds("combat/bite/biteReg");
      biteBig = makeSounds("combat/bite/biteBig");
      biteHuge = makeSounds("combat/bite/biteHuge");
      slimeMovesmall = makeSounds("combat/slime/slimeMoveSmall");
      slimeMoveBig = makeSounds("combat/slime/slimeMoveBig");
      slimeMoveHuge = makeSounds("combat/slime/slimeMoveHuge");
      deathHero = makeSounds("combat/death/deathHero");
      deathPew = makeSounds("combat/death/deathPew");
      deathReg = makeSounds("combat/death/deathReg");
      deathBig = makeSounds("combat/death/deathBig");
      deathExplosion = makeSounds("combat/death/deathExplosion");
      deathDragonLong = makeSounds("combat/death/deathDragon");
      deathOof = makeSounds("combat/death/deathOof");
      deathCute = makeSounds("combat/death/deathCute");
      deathScream = makeSounds("combat/death/deathScream");
      deathSqueak = makeSounds("combat/death/deathSqueak");
      deathAlien = makeSounds("combat/death/deathAlien");
      deathDemon = makeSounds("combat/death/deathDemon");
      deathHorse = makeSounds("combat/death/deathHorse");
      deathSpawn = makeSounds("combat/death/deathSpawn");
      deathWeird = makeSounds("combat/death/deathWeird");
      flap = makeSounds("combat/effect/flap");
      flee = makeSounds("combat/effect/flee");
      clink = makeSounds("combat/effect/clink");
      slime = makeSounds("combat/effect/slime");
      gong = makeSounds("combat/effect/gong");
      wail = makeSounds("combat/effect/wail");
      chip = makeSounds("combat/effect/chip");
      defeat = makeSounds("combat/end/defeat");
      victory = makeSounds("combat/end/victory");
      chooseItem = makeSounds("ui/choose/item");
      pickup = makeSounds("ui/inventory/pickup");
      drop = makeSounds("ui/inventory/drop");
      am.finishLoading();
      am.getAll(Sound.class, sounds);
   }

   public static <T> T get(String name, Class<T> type) {
      return (T)am.get(name, type);
   }

   private static String makeSound(String path, Class type) {
      am.load(path, type);
      allStrings.add(path);
      return path;
   }

   private static String[] makeSounds(String path) {
      return makeSounds(path, ".wav");
   }

   private static String[] makeSounds(String path, String extension) {
      List<String> validsPaths = new ArrayList<>();

      for (int i = 0; i < 999; i++) {
         String s = "sfx/" + path + "_" + i + extension;
         if (!Gdx.files.internal(s).exists()) {
            break;
         }

         makeSound(s, Sound.class);
         validsPaths.add(s);
      }

      return validsPaths.toArray(new String[0]);
   }

   public static void playSound(String string, float volume, float pitch) {
      if (soundEnabled && !TestRunner.isTesting() && !OptionLib.sfx.isOff()) {
         Sound s = soundMap.get(string);
         if (s == null) {
            s = get(string, Sound.class);
            soundMap.put(string, s);
         }

         volume *= com.tann.dice.Main.getSettings().getVolumeSFX();
         if (!(volume <= 0.0F)) {
            soundHandler.play(s, volume, pitch);
         }
      }
   }

   public static void playSound(String[] strings) {
      playSound(strings, 1.0F, 1.0F);
   }

   public static void playSound(String[] strings, float volume, float pitch) {
      Long l = timeLastPlayedMap.get(strings);
      long now = System.currentTimeMillis();
      if (l == null || now - l >= 50L) {
         timeLastPlayedMap.put(strings, now);
         playSound(strings[(int)(Math.random() * strings.length)], volume, pitch);
      }
   }

   public static void playSoundDelayed(final String[] sound, final float volume, final float pitch, float delay) {
      if (soundEnabled) {
         Tann.delay(delay, new Runnable() {
            @Override
            public void run() {
               Sounds.playSound(sound, volume, pitch);
            }
         });
      }
   }

   public static void setSoundEnabled(boolean enabled) {
      soundEnabled = enabled;
   }
}
