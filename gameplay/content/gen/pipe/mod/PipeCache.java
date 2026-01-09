package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PipeCache<T> extends Pipe<T> {
   private static final int MAX_CACHE = 1000;
   Map<String, T> cache = new HashMap<>();
   Set<String> nulls = new HashSet<>();

   public void cache(T t) {
      if (t != null) {
         if (this.cache.size() >= 1000) {
            this.cache.clear();
         }

         this.cache.put(t.toString(), t);
      }
   }

   @Override
   protected T make(String name) {
      return OptionLib.DISABLE_PIPE_CACHE.c() ? null : this.cache.get(name);
   }

   @Override
   protected boolean nameValid(String name) {
      return true;
   }

   @Override
   public T example() {
      return null;
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }

   public void cacheNull(String val) {
      this.nulls.add(val);
   }

   public boolean willNull(String val) {
      return OptionLib.DISABLE_PIPE_CACHE.c() ? false : this.nulls.contains(val);
   }

   public void cc() {
      this.cache = new HashMap<>();
   }
}
