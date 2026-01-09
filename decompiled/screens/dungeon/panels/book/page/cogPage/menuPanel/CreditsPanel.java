package com.tann.dice.screens.dungeon.panels.book.page.cogPage.menuPanel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.screens.generalPanels.TextUrl;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.statics.sound.music.JukeboxUtils;
import com.tann.dice.statics.sound.music.MusicManager;
import com.tann.dice.statics.sound.music.Musician;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CreditsPanel {
   public static Color CREDITS_COL = Colours.yellow;
   public static final String[] TRANSLATORS = new String[]{"Lorenzo Ruiz", "Gerardo Franco", "Thierry Viguier", "Andrii Pokynchereda", "Douglas Brum"};

   public static Actor make(int contentWidth) {
      contentWidth -= 8;
      String cTag = TextWriter.getTag(CREDITS_COL);
      String aTag = TextWriter.getTag(Colours.red);
      String sTag = TextWriter.getTag(Colours.grey);
      int barW = (int)(contentWidth * 0.8F);
      int topBotGap = 4;
      return new Pixl(8)
         .row(topBotGap)
         .actor(
            new Pixl(6)
               .actor(new Rectactor(barW, 1, CREDITS_COL))
               .row()
               .text(cTag + "Created by")
               .gap(5)
               .actor(TextUrl.make(cTag + "tann", "https://tann.fun"))
               .row()
               .actor(new Rectactor(barW, 1, CREDITS_COL))
               .pix()
         )
         .row()
         .listActor(
            contentWidth,
            2,
            new Pixl().text(aTag + "Pixel art").row(3).actor(TextUrl.make(aTag + "a3um", "https://twitter.com/a3um_pixels")).pix(),
            new Pixl(0).text(sTag + "Software").row(3).actor(new Pixl(2).actor(TextUrl.make(sTag + "libGDX", "https://libgdx.com/")).pix(8)).pix()
         )
         .row()
         .actor(new Pixl(0).text(TextWriter.getTag(JukeboxUtils.SOUND_COL) + "Music").row(3).actor(makeMusicCredits((int)(contentWidth * 0.85F))).pix())
         .row()
         .actor(new Pixl(0).text(TextWriter.getTag(Colours.text) + "Translations").row(3).actor(makeLocalisationCredits((int)(contentWidth * 0.85F))).pix())
         .row()
         .actor(new Rectactor(barW, 1, CREDITS_COL))
         .row(topBotGap)
         .pix(2);
   }

   private static Actor makeInsp(int contentWidth) {
      String tag = "[green]";
      return new Pixl(3)
         .text(tag + "Inspired by")
         .row()
         .listActor(
            contentWidth,
            TextUrl.make(tag + "Slay the Spire", "https://store.steampowered.com/app/646570/Slay_the_Spire/"),
            TextUrl.make(tag + "FtL", "https://store.steampowered.com/app/212680/FTL_Faster_Than_Light"),
            TextUrl.make(tag + "Cinco Paus", "https://smestorp.itch.io/cinco-paus"),
            TextUrl.make(tag + "M:tG", "https://magic.wizards.com/en")
         )
         .pix();
   }

   private static Actor makeTechnicalAssistanceCredits(float w) {
      Actor tett = TextUrl.make("TEttinger", "https://github.com/tommyettinger");
      tett.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            Sounds.playSound(Sounds.pip);
            final Actor sp = Tann.makeScrollpane(new ImageActor(Images.noise_packed));
            sp.setSize(Math.min(sp.getWidth(), com.tann.dice.Main.width * 0.8F), Math.min(sp.getHeight(), com.tann.dice.Main.height * 0.8F));
            com.tann.dice.Main.getCurrentScreen().push(sp, 1.0F);
            Tann.addListenerFirst(sp, new TannListener() {
               @Override
               public boolean action(int button, int pointer, float xx, float yx) {
                  com.tann.dice.Main.getCurrentScreen().pop(sp);
                  Sounds.playSound(Sounds.pop);
                  return true;
               }
            });
            Tann.center(sp);
            return true;
         }
      });
      return new Pixl(2)
         .text("Thanks to")
         .row()
         .actor(tett, w)
         .actor(TextUrl.make("gdx-pay", "https://github.com/libgdx/gdx-pay"), w)
         .actor(TextUrl.make("posalla", "https://github.com/libgdx/gdx-pay"), w)
         .actor(TextUrl.make("blorqus", "https://github.com/libgdx/gdx-pay"), w)
         .actor(TextUrl.make("bistina", "https://github.com/libgdx/gdx-pay"), w)
         .actor(TextUrl.make("clarin", "https://github.com/libgdx/gdx-pay"), w)
         .actor(TextUrl.make("borin", "https://github.com/libgdx/gdx-pay"), w)
         .actor(TextUrl.make("derin", "https://github.com/libgdx/gdx-pay"), w)
         .actor(TextUrl.make("questias", "https://github.com/libgdx/gdx-pay"), w)
         .pix();
   }

   private static Actor makeMusicCredits(int w) {
      List<Actor> list = new ArrayList<>();
      List<Musician> cpy = new ArrayList<>(MusicManager.getMusicianList());
      Collections.sort(cpy, new Comparator<Musician>() {
         public int compare(Musician o1, Musician o2) {
            float m1f = o1.getSongs().size() / o1.makeCredit().getWidth();
            float m2f = o2.getSongs().size() / o2.makeCredit().getWidth();
            return Float.compare(m2f, m1f);
         }
      });

      for (Musician loadMusician : cpy) {
         list.add(loadMusician.makeCredit());
      }

      return new Pixl(2).listActor(list, 2, w).pix(8);
   }

   private static Actor makeLocalisationCredits(int w) {
      List<Actor> list = new ArrayList<>();

      for (String translator : TRANSLATORS) {
         list.add(new TextWriter("[notranslate]" + translator, 100, Colours.grey, 3));
      }

      Collections.shuffle(list);
      return new Pixl(2).listActor(list, 2, w).pix(8);
   }
}
