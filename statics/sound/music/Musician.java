package com.tann.dice.statics.sound.music;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.screens.generalPanels.TextUrl;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;
import java.util.List;

public class Musician {
   public final String name;
   public final String path;
   public final String url;
   final List<MusicData> songs;

   public Musician(String name, String path, String url, List<MusicData> songs) {
      this.name = name;
      this.path = path;
      this.url = url;
      this.songs = songs;
   }

   public String getPath(String songName) {
      return this.path + "/" + songName;
   }

   public Actor makeCredit() {
      Actor a = TannStageUtils.noListener(
         TextUrl.make(TextWriter.getTag(JukeboxUtils.SOUND_COL) + this.name, this.url, this.makeFullCredit(true, true), Colours.grey)
      );
      a.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            com.tann.dice.Main.getCurrentScreen().pushAndCenter(Musician.this.makeFullCredit(true, true));
            return true;
         }
      });
      return a;
   }

   public Actor makeFullCredit(boolean url, boolean tracks) {
      return this.makeFullCredit(url, tracks, true);
   }

   public Actor makeFullCredit(boolean url, boolean tracks, boolean info) {
      return new Pixl(2).actorRowIf(tracks, JukeboxUtils.makeMusicianActor(this)).actorRowIf(url, TextUrl.getUrlActor(null, this.url, null)).pix();
   }

   public List<MusicData> getSongs() {
      return this.songs;
   }
}
