package com.tann.dice.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.statics.Images;
import com.tann.dice.util.listener.TannListener;

public abstract class Discord {
   public static final String LINK = "https://discord.gg/bB7aSPzVfb";
   public static final String NAME = "discord";

   public static Actor makeBadge() {
      Actor discord = new Pixl(2, 4).border(Colours.dark, Colours.grey, 1).image(Images.discord).text("[notranslate][blurple]discord").pix();
      discord.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            com.tann.dice.Main.getCurrentScreen().openUrl("https://discord.gg/bB7aSPzVfb");
            return true;
         }
      });
      return discord;
   }
}
