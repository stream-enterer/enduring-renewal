package com.tann.dice.screens.dungeon.panels.book.page.helpPage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.screens.generalPanels.TextUrl;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.SpeechBubble;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YoutuberPanel {
   private static final TP<String, Color> YOUTUBE = new TP<>("youtube", Colours.red);
   private static final TP<String, Color> BILLBILLI = new TP<>("bilibili", Colours.blue);

   public static List<Actor> makeAll() {
      List<Actor> result = new ArrayList<>();

      for (YoutuberPanel.Language l : YoutuberPanel.Language.values()) {
         result.addAll(l.makeYoutuberPanels());
      }

      return result;
   }

   public static Actor make(String name, YoutuberPanel.Language language, TP<String, Color> platform, String url) {
      return make(new TextWriter("[notranslate][text]" + name), language, platform, url);
   }

   public static Actor make(Actor name, YoutuberPanel.Language language, TP<String, Color> platform, String url) {
      Pixl p = new Pixl(2, 2).border(Colours.grey);
      SpeechBubble sb = new SpeechBubble(language.getColourTaggedName());
      sb.setColor(language.col);
      p.actor(name).row().text(language.getColourTaggedName()).row().actor(TextUrl.make(TextWriter.getTag(platform.b) + platform.a, url, platform.b));
      Actor a = p.pix();
      if (language == YoutuberPanel.Language.Chinese) {
         a.setName("ch");
      }

      return a;
   }

   public static enum Language {
      English("en", Colours.light),
      Portuguese("pt", Colours.green),
      Spanish("es", Colours.orange),
      Russian("ru", Colours.blue),
      German("de", Colours.yellow),
      French("fr", Colours.BLURPLE),
      Polish("pl", Colours.z_white),
      Japanese("jp", Colours.pink),
      Korean("ko", Colours.purple),
      Chinese("cn", Colours.red);

      private final String code;
      private final Color col;

      private Language(String code, Color col) {
         this.code = code;
         this.col = col;
      }

      public List<Actor> makeYoutuberPanels() {
         switch (this) {
            case English:
               return Arrays.asList(
                  YoutuberPanel.make(
                     "Retromation", English, YoutuberPanel.YOUTUBE, "https://www.youtube.com/watch?v=MwUdK16cT8U&list=PLAwWN1A7O-ORo4RMo5VxVfg36aihN6ofP"
                  ),
                  YoutuberPanel.make(
                     "Rhapsody", English, YoutuberPanel.YOUTUBE, "https://www.youtube.com/watch?v=jQQGokhoYDw&list=PLvHIlDyLzr_XGBrUKSvr6jZNo7xiumKNT"
                  )
               );
            case French:
               return Arrays.asList(YoutuberPanel.make("LeBabier", French, YoutuberPanel.YOUTUBE, "https://www.youtube.com/watch?v=1KQMiAUMvdc"));
            case Japanese:
               return Arrays.asList(YoutuberPanel.make("JIN", Japanese, YoutuberPanel.YOUTUBE, "https://www.youtube.com/watch?v=FhUjDZR40Qs"));
            case German:
               return Arrays.asList(YoutuberPanel.make("DarkHunter", German, YoutuberPanel.YOUTUBE, "https://www.youtube.com/watch?v=pt6dWtcLqck"));
            case Chinese:
               return Arrays.asList(
                  YoutuberPanel.make(new ImageActor(Images.mv, Colours.grey), Chinese, YoutuberPanel.BILLBILLI, "https://www.bilibili.com/video/BV163411B7BU/")
               );
            case Portuguese:
               return Arrays.asList(YoutuberPanel.make("ArthurLipe", Portuguese, YoutuberPanel.YOUTUBE, "https://www.youtube.com/watch?v=Gi6qpi4SMQc"));
            case Spanish:
               return Arrays.asList(
                  YoutuberPanel.make("Semaniel", Spanish, YoutuberPanel.YOUTUBE, "https://www.youtube.com/playlist?list=PLydzegdk7gFH9j1M1qVDKQhHjek0YhTdi")
               );
            case Polish:
               return Arrays.asList(YoutuberPanel.make("kantal", Polish, YoutuberPanel.YOUTUBE, "https://www.youtube.com/watch?v=usjH3ppRvIk#t=5m47s"));
            case Russian:
               return Arrays.asList(YoutuberPanel.make("Gamer's Glance", Russian, YoutuberPanel.YOUTUBE, "https://www.youtube.com/watch?v=q-57eN47KFg"));
            case Korean:
               return Arrays.asList(
                  YoutuberPanel.make("Pararang", Korean, YoutuberPanel.YOUTUBE, "https://www.youtube.com/playlist?list=PLpn24m9RsVDpyC8MO_Z5_ZCteEzW7AWJ7")
               );
            default:
               return new ArrayList<>();
         }
      }

      public String getCode() {
         return this.code;
      }

      public Color getCol() {
         return this.col;
      }

      public String getColourTaggedCode() {
         return TextWriter.getTag(this.col) + this.code + "[cu]";
      }

      public String getColourTaggedName() {
         return TextWriter.getTag(this.col) + this + "[cu]";
      }
   }
}
