package com.tann.dice.util;

import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import java.util.Map;

public class TextureCache {
   Map<String, Texture> map = new HashMap<>();
   boolean disposed = false;

   public Texture get(String key) {
      return this.map.get(key);
   }

   public void put(String key, Texture texture) {
      this.map.put(key, texture);
   }

   public void clear() {
      this.map.clear();
   }

   public void dispose() {
      if (!this.disposed) {
         this.disposed = true;

         for (Texture value : this.map.values()) {
            if (value != null) {
               value.dispose();
            }
         }

         this.map.clear();
      }
   }
}
