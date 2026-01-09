package com.tann.dice.gameplay.content.gen.pipe;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PipeMaster<T> extends Pipe<T> {
   Map<String, T> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
   final List<T> master;

   public PipeMaster(List<T> masterList) {
      for (T t : masterList) {
         this.map.put(t.toString(), t);
      }

      this.master = masterList;
   }

   @Override
   protected T make(String name) {
      return this.map.get(name);
   }

   @Override
   protected boolean nameValid(String name) {
      return true;
   }

   @Override
   public T example() {
      return Tann.random(this.master);
   }

   @Override
   public String document() {
      if (this.master.size() < 30) {
         Object first = this.master.get(0);
         if (!(first instanceof MonsterType)) {
            return "hidden " + Words.plural(first.getClass().getSimpleName().toLowerCase());
         }
      }

      return super.document();
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
