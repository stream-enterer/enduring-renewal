package com.tann.dice.gameplay.battleTest.template;

import com.tann.dice.gameplay.battleTest.Zone;

public class BossTemplate {
   private final LevelTemplate levelTemplate;
   protected final Zone zone;

   public BossTemplate(Zone zone, LevelTemplate levelTemplate) {
      this.levelTemplate = levelTemplate;
      this.zone = zone;
      levelTemplate.markAsBoss();
   }

   public LevelTemplate getLevelTemplate() {
      return this.levelTemplate;
   }

   public boolean validFor(Zone type) {
      return this.zone == type;
   }

   public boolean isLocked() {
      return this.levelTemplate.isLocked();
   }
}
