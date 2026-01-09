package com.tann.dice.statics.sound.music;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.save.settings.option.Option;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.screens.dungeon.panels.book.Book;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.Checkbox;
import com.tann.dice.util.ui.LiveText;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JukeboxUtils {
   public static final Color SOUND_COL = Colours.orange;
   private static String textNone = null;
   private static final int METABOX_MIN = 2;

   public static Actor makeEntryButton() {
      return StandardButton.create("jukebox", new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            Book.openBook("stuff-jukebox");
            return true;
         }
      }).makeTiny();
   }

   public static Actor makeJukebox(int contentWidth) {
      Pixl p = new Pixl(3);
      p.text("[b]Music Info").row();
      p.actor(OptionLib.music.makeCogActor(com.tann.dice.Main.t("volume")))
         .actor(TannStageUtils.noListener(makeCurrentlyPlaying()), contentWidth)
         .actor(makeSongControls(), contentWidth)
         .row();

      for (Option option : Arrays.asList(OptionLib.MUSIC_SELECTION, OptionLib.AFFECT_LOOP)) {
         p.actor(option.makeCogActor(), contentWidth);
      }

      p.row();
      List<Musician> musicians = MusicManager.getMusicianList();
      List<Actor> musicianPanels = new ArrayList<>();

      for (Musician musician : musicians) {
         musicianPanels.add(makeMusicianActor(musician));
      }

      TannStageUtils.sortActorsBySizeForJukebox(musicianPanels, contentWidth);
      Collections.reverse(musicianPanels);
      p.actor(Tann.layoutMinArea(musicianPanels, 4, contentWidth, 999999, 2));
      return p.pix();
   }

   public static Actor makeMusicianActor(final Musician musician) {
      final boolean addMeta = musician.songs.size() >= 2;
      final Checkbox metaBox = new Checkbox(allEnabled(musician));
      final List<Checkbox> boxes = new ArrayList<>();

      for (MusicData md : musician.songs) {
         boxes.add(makeCheckbox(md, new Runnable() {
            @Override
            public void run() {
               if (addMeta) {
                  boolean allOn = true;
                  boolean allOff = true;
                  boolean anyOff = false;

                  for (MusicData song : musician.songs) {
                     boolean d = com.tann.dice.Main.getSettings().isDisabledSong(song);
                     allOn &= !d;
                     allOff &= d;
                     anyOff |= d;
                  }

                  if (allOn) {
                     metaBox.force(true);
                  }

                  if (anyOff) {
                     metaBox.force(false);
                  }
               }
            }
         }));
      }

      metaBox.addDefaultToggleListener();
      metaBox.addToggleRunnable(new Runnable() {
         @Override
         public void run() {
            for (Checkbox box : boxes) {
               box.force(metaBox.isOn());
            }

            JukeboxUtils.setEnabledMusician(musician, metaBox.isOn());
         }
      });
      Group g = new Pixl(2, 2).actorRowIf(addMeta, metaBox).actor(makeSongs(musician, boxes)).pix(8);
      TextWriter tw = new TextWriter("[notranslate]" + musician.name);
      tw.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            com.tann.dice.Main.getCurrentScreen().openUrl(musician.url);
            return true;
         }
      });
      g = DipPanel.makeTopPanelGroup(tw, g, Colours.grey, 2);
      final Color coverDark = Colours.withAlpha(Colours.dark, 0.7F).cpy();
      Actor cover = new Actor() {
         public void draw(Batch batch, float parentAlpha) {
            if (JukeboxUtils.isDisabled(musician)) {
               Draw.fillActor(batch, this, coverDark);
            }

            super.draw(batch, parentAlpha);
         }
      };
      cover.setSize(g.getWidth(), g.getHeight());
      g.addActor(cover);
      cover.setTouchable(Touchable.disabled);
      return g;
   }

   private static boolean allEnabled(Musician musician) {
      for (MusicData song : musician.songs) {
         if (com.tann.dice.Main.getSettings().isDisabledSong(song)) {
            return false;
         }
      }

      return true;
   }

   private static Checkbox makeCheckbox(final MusicData md, final Runnable extraOnToggle) {
      final Checkbox c = new Checkbox(!com.tann.dice.Main.getSettings().isDisabledSong(md), 12);
      c.addDefaultToggleListener();
      c.addToggleRunnable(new Runnable() {
         @Override
         public void run() {
            JukeboxUtils.setEnabledWithoutSaving(md, c.isOn());
            com.tann.dice.Main.getSettings().save();
            if (MusicManager.getCurrentSongData(true) == md && !c.isOn()) {
               MusicManager.userRequestNextSong();
            }

            if (extraOnToggle != null) {
               extraOnToggle.run();
            }
         }
      });
      return c;
   }

   private static void setEnabledMusician(Musician musician, boolean on) {
      for (MusicData song : musician.songs) {
         setEnabledWithoutSaving(song, on);
      }

      if (!on && musician.songs.contains(MusicManager.getCurrentSongData(true))) {
         MusicManager.userRequestNextSong();
      }

      com.tann.dice.Main.getSettings().save();
   }

   private static Actor makeSongs(Musician musician, List<Checkbox> boxes) {
      Pixl p = new Pixl(2);
      List<MusicData> songs = musician.songs;

      for (int i = 0; i < songs.size(); i++) {
         MusicData md = songs.get(i);
         Checkbox c = boxes.get(i);
         p.actor(makeSong(md, c));
         if (i < songs.size() - 1) {
            p.row();
         }
      }

      return p.pix(8);
   }

   private static Actor makeSong(final MusicData md, Actor checkbox) {
      String niceName = MusicFormat.getNiceName(md.path);
      Actor songNameWriter = new TextWriter("[notranslate]" + niceName);
      Group g = Tann.makeGroup(songNameWriter);
      g.setHeight(12.0F);
      Tann.center(songNameWriter);
      TextWriter disab = new TextWriter("[notranslate][grey]" + TextWriter.stripTags(niceName)) {
         public void act(float delta) {
            this.setVisible(com.tann.dice.Main.getSettings().isDisabledSong(md));
         }
      };
      g.addActor(disab);
      disab.setY(songNameWriter.getY());
      disab = new TextWriter("[notranslate][light]" + TextWriter.stripTags(niceName)) {
         public void act(float delta) {
            this.setVisible(md == MusicManager.getCurrentSongData(true) && !MusicManager.isMusicDisabled());
         }
      };
      g.addActor(disab);
      disab.setY(songNameWriter.getY());
      g.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            if (OptionLib.music.isOff()) {
               Sounds.playSound(Sounds.error);
               com.tann.dice.Main.getCurrentScreen().showDialog("[red]music is muted");
               return true;
            } else {
               MusicManager.requestSong(md, true);
               return true;
            }
         }

         @Override
         public boolean info(int button, float x, float y) {
            JukeboxUtils.showInfo(md);
            return true;
         }
      });
      return new Pixl(3).actor(checkbox).actor(g).pix();
   }

   public static Actor makeSimpleDetail(MusicData md) {
      Pixl p = new Pixl(3, 4).border(SOUND_COL);
      int mw = (int)(com.tann.dice.Main.width * 0.8F);
      p.actor(new TextWriter(MusicFormat.getNiceName(md.path)), mw).row();

      for (MusicType musicType : md.musicTypes) {
         p.text("[grey]" + musicType.name());
      }

      if (OptionLib.SHOW_RARITY.c()) {
         p.text("[grey]" + Tann.floatFormat(md.getRarity() * 100.0F));
      }

      p.text(Tann.parseSeconds((int)md.seconds, false));
      if (md.loop != 1) {
         p.text("[blue]" + md.loop + "x loop");
      }

      return p.pix();
   }

   private static Actor makeSongDetail(MusicData md) {
      Musician m = MusicManager.fetchInefficiently(md);
      return makeSimpleDetail(md);
   }

   private static void setEnabledWithoutSaving(MusicData md, boolean on) {
      com.tann.dice.Main.getSettings().setSongEnabled(md, on);
   }

   private static boolean isDisabled(Musician m) {
      for (MusicData song : m.songs) {
         if (!com.tann.dice.Main.getSettings().isDisabledSong(song)) {
            return false;
         }
      }

      return true;
   }

   public static Actor makeSongControls() {
      return new Pixl(2).actor(makeSkipBackwardsButton()).actor(makeSkipForwardsButton()).actor(makeNextSongButton()).pix();
   }

   private static Actor makeSkipBackwardsButton() {
      return StandardButton.create("<-", new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            MusicManager.userRequestSkipBack();
            Sounds.playSound(Sounds.pip);
            return true;
         }
      }).makeTiny();
   }

   private static Actor makeSkipForwardsButton() {
      return StandardButton.create(">", new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            MusicManager.userRequestSkipForwards();
            Sounds.playSound(Sounds.pip);
            return true;
         }
      }).makeTiny();
   }

   private static Actor makeNextSongButton() {
      return StandardButton.create("->", new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            MusicManager.userRequestNextSong();
            Sounds.playSound(Sounds.pip);
            return true;
         }
      }).makeTiny();
   }

   public static String getTextNone() {
      if (textNone == null) {
         textNone = com.tann.dice.Main.t("none");
      }

      return textNone;
   }

   public static Actor makeCurrentlyPlaying() {
      LiveText lt = new LiveText(76) {
         @Override
         public String fetchText() {
            if (MusicManager.hasFailed()) {
               return "[red]err";
            } else {
               return MusicManager.isMusicDisabled() ? JukeboxUtils.getTextNone() : MusicManager.getCurrentSongTitle(true);
            }
         }
      };
      int gap = 3;
      Actor progress = new Actor() {
         public void draw(Batch batch, float parentAlpha) {
            batch.setColor(Colours.withAlpha(JukeboxUtils.SOUND_COL, 0.5F).cpy());
            Draw.drawRectangle(batch, this.getX(), this.getY(), 1.0F, this.getHeight());
            Draw.drawRectangle(batch, this.getX() + this.getWidth() - 1.0F, this.getY(), 1.0F, this.getHeight());
            MusicData md = MusicManager.getCurrentMusicData();
            if (md != null) {
               float time = MusicManager.getCurrentSongPlayedAmt();
               batch.setColor(Colours.withAlpha(JukeboxUtils.SOUND_COL, 0.3F).cpy());
               Draw.fillRectangle(batch, this.getX(), this.getY(), (int)Math.ceil(this.getWidth() * (time / md.seconds)), this.getHeight());
            }

            super.draw(batch, parentAlpha);
         }
      };
      progress.setSize((int)lt.getWidth(), (int)lt.getHeight() + gap * 2);
      Group g = Tann.makeGroup(progress);
      g.addActor(progress);
      g.addActor(lt);
      Tann.center(lt);
      lt.setZIndex(0);
      lt.setX(lt.getX());
      g.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            if (MusicManager.isMusicDisabled()) {
               return true;
            } else {
               MusicData md = MusicManager.getCurrentSongData(true);
               if (md == null) {
                  Sounds.playSound(Sounds.error);
                  return true;
               } else {
                  Musician m = MusicManager.fetchInefficiently(md);
                  if (m != null) {
                     com.tann.dice.Main.getCurrentScreen().pushAndCenter(m.makeFullCredit(true, true, true));
                  }

                  return true;
               }
            }
         }
      });
      return g;
   }

   private static void showInfo(MusicData md) {
      Actor a = makeSongDetail(md);
      com.tann.dice.Main.getCurrentScreen().pushAndCenter(a, 0.8F);
   }

   static Actor getPopupActor(final MusicData musicData) {
      Pixl p = new Pixl(3, 2).border(Colours.grey).text("[grey]now playing:");

      for (String s : musicData.path.split("/")) {
         if (!s.equalsIgnoreCase("music")) {
            p.row().text(Tann.makeEllipses(s, 30));
         }
      }

      Actor a = p.pix(8);
      a.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            JukeboxUtils.showInfo(musicData);
            return true;
         }
      });
      return a;
   }
}
