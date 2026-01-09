package com.tann.dice.screens.generalPanels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.ClipboardUtils;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;

public class TextUrl {
   public static TextWriter make(String text, String url) {
      return make(text, url, Colours.grey);
   }

   public static TextWriter make(String text, final String url, Color bg) {
      TextWriter tw = new TextWriter("[notranslate]" + text, 100, bg, 3);
      tw.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            com.tann.dice.Main.getCurrentScreen().openUrl(url);
            return true;
         }
      });
      return tw;
   }

   public static TextWriter make(String text, final String url, final Actor extra, Color bg) {
      TextWriter tw = new TextWriter("[notranslate]" + text, 100, bg, 3);
      tw.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            com.tann.dice.Main.getCurrentScreen().openUrl(url, null, extra);
            return true;
         }
      });
      return tw;
   }

   public static Actor getUrlActor(Actor extra, final String url, String description) {
      Pixl p = new Pixl(2, 3).border(Colours.grey);
      if (extra != null) {
         p.actor(extra).row();
      }

      boolean needsOpen = url.startsWith("http");
      String top = "[notranslate]"
         + TextWriter.getTag(ClipboardUtils.URN_COL)
         + (needsOpen ? com.tann.dice.Main.t("URL:") : com.tann.dice.Main.t("Search online:"))
         + " [text]"
         + Tann.makeEllipses(url, TannFont.guessMaxTextLength(0.6666667F));
      p.text(top);
      if (description != null) {
         p.row().text("Description: [text]" + description);
      }

      p.row().actor(ClipboardUtils.makeSimpleCopyButton(url));
      if (needsOpen) {
         StandardButton open = new StandardButton(TextWriter.getTag(ClipboardUtils.URN_COL) + "open");
         open.setRunnable(new Runnable() {
            @Override
            public void run() {
               Gdx.net.openURI(url);
            }
         });
         p.actor(open);
      }

      return p.pix();
   }
}
