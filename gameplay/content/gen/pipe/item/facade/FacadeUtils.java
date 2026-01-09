package com.tann.dice.gameplay.content.gen.pipe.item.facade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EnSiBi;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.die.side.HSL;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.book.Book;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

public class FacadeUtils {
   static final String[] folders = new String[]{
      "base",
      "items",
      "spells",
      "alpha",
      "LemonReco",
      "eba",
      "posalla",
      "Eses",
      "kas333",
      "EmeraldFart",
      "dee",
      "harakat",
      "SpikeBerd",
      "Ycarou",
      "BerdExtras",
      "Sefcear",
      "DgKssr",
      "LeoCurry",
      "Coly",
      "OkNano",
      "MutantBreadSlice",
      "Rick",
      "darka",
      "symbols",
      "BallerKiD",
      "Seare",
      "TheBGamer12",
      "alewis",
      "DogKisser",
      "theGreenDigi",
      "Cancelion",
      "Lizaru",
      "Chethumn",
      "AlexGnite",
      "danieldice",
      "PEPPER",
      "Aiden910",
      "EncounterCalix",
      "Ksyon",
      "powercsmdieslol",
      "Fred",
      "MediaMan",
      "Sulyvahn"
   };

   public static TP<String, TextureRegion> indexedFullData(String tag, int index) {
      AtlasRegion ar = getSide(tag, index);
      return ar == null ? null : new TP<>(getNiceTitle(ar), ar);
   }

   private static String getNiceTitle(AtlasRegion ar) {
      if (ar == null) {
         return "";
      } else {
         String[] parts = ar.name.split("/");
         switch (parts.length) {
            case 0:
               return "err";
            case 1:
               return parts[0];
            default:
               return parts[parts.length - 2] + "/" + parts[parts.length - 1];
         }
      }
   }

   public static EntSide maybeWithValue(Object o, int val) {
      if (o instanceof EntSide) {
         return (EntSide)o;
      } else if (o instanceof EnSiBi) {
         EnSiBi esb = (EnSiBi)o;
         return esb.val(val);
      } else {
         return null;
      }
   }

   public static EntSide make(int index, String folder, int val, EntSize size, Eff base) {
      if (val <= 999 && val >= -999) {
         TextureRegion tx = getSide(folder, index);
         return tx == null ? null : new EntSide(tx, base, size);
      } else {
         return null;
      }
   }

   public static String getAttr(String tag, int i) {
      return getAttr(indexedFullData(tag, i), i);
   }

   private static String getAttr(TP<String, TextureRegion> q, int i) {
      String desc = "side " + i;
      if (q != null) {
         desc = q.a;
      }

      return desc;
   }

   public static Actor example(String text, Unlockable type, TextureRegion tx, String facTag) {
      return new Pixl(3, 4).border(Colours.grey).text(text).row().text(facTag).image(tx).row().actor(type.makeUnlockActor(true)).pix();
   }

   private static AtlasRegion getSide(String folder, int index) {
      List<AtlasRegion> lst = getAllFromFolder(folder);
      return index >= lst.size() ? null : lst.get(index);
   }

   private static List<AtlasRegion> getAllFromFolder(String folder) {
      List<AtlasRegion> tr = new ArrayList<>();
      if (!folder.equals("bas")) {
         List<AtlasRegion> regs = Tann.getRegionsStartingWith(com.tann.dice.Main.atlas_3d, "extra/" + lengthen(folder));
         tr.addAll(regs);
         return tr;
      } else {
         for (EntSize sz : new EntSize[]{EntSize.reg, EntSize.big, EntSize.huge, EntSize.small}) {
            for (Object obj : EntSidesLib.getSizedSides(sz)) {
               TextureRegion otr = maybeWithValue(obj, 0).getTexture();
               if (otr instanceof AtlasRegion) {
                  tr.add((AtlasRegion)otr);
               }
            }
         }

         return tr;
      }
   }

   public static String[] folderNames() {
      return folders;
   }

   public static String metacodeFolders() {
      FileHandle[] list = Gdx.files.absolute("C:/code/games/Dicegeon/images/3d/extra").list();
      List<String> names = new ArrayList<>();

      for (FileHandle fileHandle : list) {
         String n = fileHandle.name();
         if (!isFromGame(n)) {
            names.add(n);
         }
      }

      Collections.sort(names, new Comparator<String>() {
         public int compare(String o1, String o2) {
            return Float.compare(FacadeUtils.compFromFolder(o2), FacadeUtils.compFromFolder(o1));
         }
      });
      String midString = "\"" + Tann.arrayToString(names.toArray(new String[0]), "\",\"") + "\"";
      return "static final String[] folders = new String[]{\"base\",\"items\",\"spells\",\"alpha\"," + midString + "};";
   }

   private static boolean isFromGame(String n) {
      return n.equals("items") || n.equals("spells") || n.equals("base") || n.equals("alpha");
   }

   private static float compFromFolder(String folder) {
      return (float)getAllFromFolder(folder).size() / folder.length();
   }

   public static String makeThanks() {
      return "[notranslate]Thanks to " + Tann.commaList(creditArray());
   }

   private static List<String> creditArray() {
      List<String> res = new ArrayList<>(Arrays.asList(folders));

      for (int i = res.size() - 1; i >= 0; i--) {
         String s = res.get(i);
         if (isFromGame(s)) {
            res.remove(i);
         } else {
            res.set(i, authorCol(s));
         }
      }

      return res;
   }

   private static String authorCol(String s) {
      Color c = authC(s);
      return "[col" + Colours.toHex(c) + "]" + s + "[cu]";
   }

   private static Color authC(String s) {
      return Colours.shiftedTowards(Colours.grey, Colours.randomHashed(s.hashCode()), 0.6F).cpy();
   }

   public static Actor makeCommunityEntryWithThanks(int textWidth) {
      return new Pixl(3)
         .text(makeThanks(), textWidth)
         .row()
         .actor(new StandardButton("[notranslate][blue][b]community side art").makeTiny().setRunnable(new Runnable() {
            @Override
            public void run() {
               FacadeUtils.showTag(TextWriter.stripTags(Tann.pick(FacadeUtils.creditArray())));
            }
         }))
         .pix();
   }

   private static Actor makeFacadeNew(@Nonnull String tag) {
      return new Pixl(5, 4).border(Colours.grey).actor(makeTop(tag)).row().actor(makeBot(shorten(tag))).pix();
   }

   private static String shorten(String folderName) {
      return folderName == null ? null : folderName.substring(0, 3);
   }

   private static String lengthen(String tag) {
      if (tag == null) {
         return null;
      } else {
         for (String folder : folders) {
            if (folder != null && shorten(folder).equals(tag)) {
               return folder;
            }
         }

         return tag;
      }
   }

   private static int mw() {
      return (int)(com.tann.dice.Main.width * 0.85F);
   }

   private static Actor makeTop(String tag) {
      Pixl p = new Pixl(3);

      for (final String folder : folders) {
         boolean highlit = Objects.equals(folder, tag);
         String n;
         if (highlit) {
            n = "[white]" + folder;
         } else if (isFromGame(folder)) {
            n = "[grey]" + folder;
         } else {
            n = authorCol(folder);
         }

         TextWriter tw = new TextWriter("[notranslate]" + n);
         tw.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               FacadeUtils.showTag(folder);
               return true;
            }
         });
         p.actor(tw, mw());
      }

      return p.pix();
   }

   public static void showTag(@Nonnull String tag) {
      Screen s = com.tann.dice.Main.getCurrentScreen();
      s.popAllLight();
      if (!(s.getTopActualActor() instanceof Book)) {
         s.popSingleMedium();
      }

      Actor a = Tann.makeScrollpaneIfNecessary(makeFacadeNew(tag));
      com.tann.dice.Main.getCurrentScreen().pushAndCenter(a, 0.5F, false);
      int GAP = (int)(com.tann.dice.Main.height * 0.1F);
      a.setY(com.tann.dice.Main.height - a.getHeight() - GAP);
   }

   private static Actor makeBot(final String tag) {
      Pixl p = new Pixl(1);
      List<AtlasRegion> allDebug = getAllFromFolder(tag);

      for (int i = 0; i < allDebug.size(); i++) {
         final AtlasRegion tr = allDebug.get(i);
         int w = tr.getRegionWidth();
         final int h = tr.getRegionHeight();
         if (!tag.equals("bas") && !tag.equals("spe") && !tag.equals("ite") && (w != 12 && w != 16 && w != 22 && w != 28 || w != h)) {
            TannLog.error("wrongsize: " + tr.name);
         }

         Actor a = new ImageActor(tr);
         h = i;
         a.addListener(
            new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  com.tann.dice.Main.getCurrentScreen()
                     .pushAndCenter(FacadeUtils.example(FacadeUtils.getAttr(tag, h), FacadeUtils.ex2Item(tag, h), tr, tag + h), 0.5F);
                  return true;
               }
            }
         );
         p.actor(a, com.tann.dice.Main.width * 0.7F);
      }

      return p.pix();
   }

   public static EntSide copyTex(Eff effectSrc, EntSide texSrc, HSL hsl) {
      effectSrc.innatifyKeywords();
      return new EntSide(texSrc.getTexture(), effectSrc, EntSize.reg, hsl);
   }

   public static boolean isFacaded(Ent et) {
      DungeonScreen ds = DungeonScreen.get();
      if (ds != null) {
         try {
            if (ds.getDungeonContext().getContextConfig().mode.getFolderType() == FolderType.creative) {
               return true;
            }
         } catch (Exception var4) {
         }

         return SaveState.lastSaveFacade;
      } else {
         String facade = "facade";
         List<Item> items = et.getItems();

         for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().contains(facade)) {
               return true;
            }
         }

         return et.getName(false).contains(facade);
      }
   }

   private static Item ex2Item(String tag, int i) {
      String n = "left2.facade." + tag + i + ":" + Tann.randomInt(99);
      return ItemLib.byName(n);
   }

   public static boolean matches(String somethingName) {
      return Pattern.compile("[a-zA-Z]{3}\\d+").matcher(somethingName).matches();
   }
}
