package com.tann.dice.gameplay.content.gen.pipe;

import java.util.ArrayList;
import java.util.Map;

public class PipeNickname<T> extends PipeMaster<T> {
   public PipeNickname(Map<String, T> data) {
      super(new ArrayList<>(data.values()));

      for (String s : data.keySet()) {
         if (!s.equals(s.toLowerCase())) {
            throw new RuntimeException("nickname cap");
         }
      }

      this.map = data;
   }

   @Override
   public boolean isHiddenAPI() {
      return true;
   }
}
