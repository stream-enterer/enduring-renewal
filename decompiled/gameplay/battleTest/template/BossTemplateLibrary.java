package com.tann.dice.gameplay.battleTest.template;

import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BossTemplateLibrary {
   public static final String DEER = "Madness";
   private static List<BossTemplate> bossTemplates = makeBossTemplates();

   public static List<BossTemplate> makeBossTemplates() {
      return Arrays.asList(
         new BossTemplate(Zone.Forest, new LevelTemplate(MonsterTypeLib.listName("troll"), MonsterTypeLib.listName("archer"))),
         new BossTemplate(Zone.Forest, new LevelTemplate(MonsterTypeLib.listName("bramble"), MonsterTypeLib.listName("rat"))),
         new BossTemplate(Zone.Forest, new LevelTemplate(MonsterTypeLib.listName("alpha"), MonsterTypeLib.listName("wolf"))),
         new BossTemplate(Zone.Dungeon, new LevelTemplate(MonsterTypeLib.listName("magrat", "gytha", "agnes"), MonsterTypeLib.listName("rat"))),
         new BossTemplate(Zone.Dungeon, new LevelTemplate(MonsterTypeLib.listName("slime queen"), MonsterTypeLib.listName("slimelet"))),
         new BossTemplate(Zone.Dungeon, new LevelTemplate(MonsterTypeLib.listName("bell"), MonsterTypeLib.listName("fanatic"))),
         new BossTemplate(Zone.Dungeon, new LevelTemplate(MonsterTypeLib.listName("sarcophagus"), MonsterTypeLib.listName("gnoll"))),
         new BossTemplate(Zone.Catacombs, new LevelTemplate(MonsterTypeLib.listName("lich"), MonsterTypeLib.listName("bones"))),
         new BossTemplate(Zone.Catacombs, new LevelTemplate(MonsterTypeLib.listName("rotten"), MonsterTypeLib.listName("bones"))),
         new BossTemplate(Zone.Catacombs, new LevelTemplate(MonsterTypeLib.listName("baron"), MonsterTypeLib.listName("ghost"))),
         new BossTemplate(Zone.Catacombs, new LevelTemplate(MonsterTypeLib.listName("Madness"), MonsterTypeLib.listName("thorn"))),
         new BossTemplate(Zone.Lair, new LevelTemplate(MonsterTypeLib.listName("tarantus"), MonsterTypeLib.listName("spider"))),
         new BossTemplate(Zone.Lair, new LevelTemplate(MonsterTypeLib.listName("troll king", "slate"), MonsterTypeLib.listName("archer"))),
         new BossTemplate(Zone.Lair, new LevelTemplate(MonsterTypeLib.listName("basalt"), MonsterTypeLib.listName("quartz"))),
         new BossTemplate(Zone.Pit, new LevelTemplate(MonsterTypeLib.listName("dragon", "caw"), MonsterTypeLib.listName("archer"))),
         new BossTemplate(Zone.Pit, new LevelTemplate(MonsterTypeLib.listName("Inevitable"), MonsterTypeLib.listName("wisp"))),
         new BossTemplate(Zone.Pit, new LevelTemplate(MonsterTypeLib.listName("hexia", "imp", "imp", "imp", "imp"), MonsterTypeLib.listName("demon", "imp"))),
         new BossTemplate(Zone.Pit, new LevelTemplate(MonsterTypeLib.listName("the hand"), MonsterTypeLib.listName("saber")))
      );
   }

   public static List<LevelTemplate> getAllBossTemplates(Zone type, boolean allowLocked) {
      List<LevelTemplate> valids = new ArrayList<>();

      for (BossTemplate lt : bossTemplates) {
         if ((allowLocked || !lt.isLocked()) && (type == null || lt.validFor(type))) {
            valids.add(lt.getLevelTemplate());
         }
      }

      return valids;
   }

   public static LevelTemplate getBossTemplate(Zone type) {
      List<LevelTemplate> temps = getAllBossTemplates(type, false);
      if (!temps.isEmpty()) {
         return Tann.random(temps);
      } else {
         for (BossTemplate bossTemplate : bossTemplates) {
            if (bossTemplate.zone.index == type.index) {
               return bossTemplate.getLevelTemplate();
            }
         }

         return null;
      }
   }

   public static List<MonsterType> getAllBossMonsters(Zone zone) {
      List<MonsterType> result = new ArrayList<>();

      for (BossTemplate bossTemplate : bossTemplates) {
         if (bossTemplate.zone == zone) {
            result.add(bossTemplate.getLevelTemplate().getInitialSetup().get(0));
         }
      }

      return result;
   }
}
