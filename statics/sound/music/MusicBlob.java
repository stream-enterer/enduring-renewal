package com.tann.dice.statics.sound.music;

import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicBlob {
   static final String SUFFIX = com.tann.dice.Main.self().control.getMusicExtension();

   static List<MusicData> getData() {
      return Arrays.asList(
         new MusicData("louigi verona/Ancient Books of Magic" + SUFFIX, 84.789F, MusicType.Title),
         new MusicData("louigi verona/Dark Enchantments" + SUFFIX, 93.607F, MusicType.Lair, MusicType.Catacombs, MusicType.Pit),
         new MusicData("louigi verona/Deadly Encounter" + SUFFIX, 57.935F, MusicType.Dungeon, MusicType.Forest),
         new MusicData("louigi verona/Bounty Hunters" + SUFFIX, 127.05F, MusicType.Forest, MusicType.Lair),
         new MusicData("louigi verona/Spellcasters Galore" + SUFFIX, 90.948F, MusicType.Forest, MusicType.Dungeon),
         new MusicData("louigi verona/War Machines" + SUFFIX, 68.999F, MusicType.Pit, MusicType.Dungeon),
         new MusicData("cold sanctum/LINGERING DESOLATE GLOOM" + SUFFIX, 87.797F, MusicType.Title),
         new MusicData("cold sanctum/CONJURING SINISTER WIZARDRY" + SUFFIX, 94.119995F, MusicType.Catacombs, MusicType.Pit),
         new MusicData("roho/Swiftsoles v3" + SUFFIX, 124.8F, MusicType.Forest, MusicType.Dungeon, MusicType.Lair).loop(2, 6.85F),
         new MusicData("ziggurath/Assassins_ Dirge" + SUFFIX, 132.0F, MusicType.Lair, MusicType.Catacombs).loop(2, 0.2F),
         new MusicData("ziggurath/Gemstones and Stratagems" + SUFFIX, 95.999F, MusicType.Lair, MusicType.Dungeon).loop(2, 21.4F),
         new MusicData("ziggurath/Steel Wins Battles" + SUFFIX, 128.0F, MusicType.Forest, MusicType.Dungeon).loop(2, 18.55F),
         new MusicData("ziggurath/Veteran of 1000 Rolls" + SUFFIX, 121.29F, MusicType.Forest, MusicType.Dungeon).loop(2, 4.5F),
         new MusicData("aleksander/Into The Depths" + SUFFIX, 64.931F, MusicType.Dungeon, MusicType.Catacombs, MusicType.Pit).loop(2, 0.0F),
         new MusicData("aleksander/Next Battle Awaits" + SUFFIX, 25.352F, MusicType.Dungeon, MusicType.Lair, MusicType.Catacombs),
         new MusicData("aleksander/Dicing Opponents" + SUFFIX, 88.393F, MusicType.Forest, MusicType.Dungeon).loop(2, 0.0F),
         new MusicData("aleksander/Defense Ready" + SUFFIX, 95.065F, MusicType.Lair, MusicType.Pit).loop(2, 0.2F).fadeSpeed(0.5F),
         new MusicData("andrew goodwin/What The Smoke Conceals" + SUFFIX, 121.14F, MusicType.Forest, MusicType.Lair).loop(2, 0.0F),
         new MusicData("andrew goodwin/Black Castle" + SUFFIX, 94.913F, MusicType.Dungeon, MusicType.Catacombs, MusicType.Pit).loop(2, 0.0F),
         new MusicData("andrew goodwin/Withering Thoughts" + SUFFIX, 145.043F, MusicType.Lair, MusicType.Catacombs, MusicType.Title).loop(2, 0.0F),
         new MusicData("andrew goodwin/No Turning Back" + SUFFIX, 109.71F, MusicType.Dungeon, MusicType.Catacombs).loop(2, 1.62F),
         new MusicData("andrew goodwin/The Witches Castle" + SUFFIX, 93.595F, MusicType.Dungeon, MusicType.Catacombs).loop(2, 0.0F)
      );
   }

   public static List<Musician> loadMusicians(List<MusicData> dataLust) {
      Map<String, List<MusicData>> musicianToTrack = new HashMap<>();

      for (MusicData musicDatum : dataLust) {
         String path = musicDatum.path;
         String[] parts = path.split("/");
         String author = parts[0];
         String track = parts[1];
         if (musicianToTrack.get(author) == null) {
            musicianToTrack.put(author, new ArrayList<>());
         }

         musicianToTrack.get(author).add(musicDatum);
      }

      List<Musician> result = new ArrayList<>();
      List<String> keys = new ArrayList<>(musicianToTrack.keySet());
      Collections.sort(keys, new Comparator<String>() {
         public int compare(String o1, String o2) {
            return o1.length() - o2.length();
         }
      });

      for (String musicianFolderName : keys) {
         String name = musicianFolderName;
         String url = "unset";
         switch (musicianFolderName) {
            case "andrew goodwin":
               url = "https://www.andrewgoodwincomposer.com/";
               break;
            case "aleksander":
               url = "https://www.aleksanderzablocki.com/";
               name = "Aleksander Zabłocki";
               break;
            case "tann":
               url = "https://tann.fun";
               break;
            case "ziggurath":
               url = "https://www.youtube.com/watch?v=T4f_Nf17MbY&list=PLisbFJAT312VD5qpsjOcEe58ObfEt0NQ1";
               break;
            case "louigi verona":
               url = "https://louigiverona.com/?page=about";
               break;
            case "roho":
               url = "https://rosoe.xyz/";
               name = "ro'sø";
               break;
            case "cold sanctum":
               url = com.tann.dice.Main.self().control.stupidAboutLinks() ? "cold sanctum music" : "https://coldsanctum.bandcamp.com/";
         }

         name = Words.capitaliseWords(name);
         result.add(new Musician(name, musicianFolderName, url, musicianToTrack.get(musicianFolderName)));
      }

      return result;
   }

   public static String getMusicianFeature() {
      List<String> msc = new ArrayList<>();

      for (Musician loadMusician : MusicManager.getMusicianList()) {
         msc.add(loadMusician.name);
      }

      return Tann.commaList(msc);
   }
}
