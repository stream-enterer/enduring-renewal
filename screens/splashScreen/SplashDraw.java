package com.tann.dice.screens.splashScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SplashDraw {
   Texture loading = new Texture("splash/loading.png");
   Texture resolution = new Texture("splash/resolution.png");
   Texture px = new Texture("splash/darkpx.png");
   SpriteBatch spriteBatch = new SpriteBatch();

   public void draw(SplashDraw.SplashType type) {
      this.spriteBatch.getProjectionMatrix().setToOrtho2D(0.0F, 0.0F, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      this.spriteBatch.begin();
      Texture splash = type == SplashDraw.SplashType.Loading ? this.loading : this.resolution;
      float scale = Math.min(Gdx.graphics.getWidth() / splash.getWidth(), Gdx.graphics.getHeight() / splash.getHeight());
      scale = Math.max(scale, 1.0F);
      float width = splash.getWidth() * scale;
      float height = splash.getHeight() * scale;
      this.spriteBatch.draw(this.px, 0.0F, 0.0F, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      this.spriteBatch.draw(splash, (int)(Gdx.graphics.getWidth() - width) / 2, (int)(Gdx.graphics.getHeight() - height) / 2, width, height);
      this.spriteBatch.end();
   }

   public static enum SplashType {
      Loading,
      InvalidResolution;
   }
}
