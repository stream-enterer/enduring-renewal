package com.tann.dice.gameplay.trigger.global.level;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementRange;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlobalSetMonsters extends Global {
   final MonsterType[] types;

   public GlobalSetMonsters(MonsterType... types) {
      this.types = types;
   }

   @Override
   public String describeForSelfBuff() {
      if (this.types.length == 0) {
         return "err";
      } else {
         String start = "Fight";
         String end = "instead";
         if (onlyOneType(Arrays.asList(this.types))) {
            String middle = "a " + this.types[0].getName(true).toLowerCase();
            if (this.types.length > 1) {
               middle = this.types.length + "x " + this.types[0].getName(true).toLowerCase();
            }

            return start + " " + middle + " " + end;
         } else {
            String result = start + " ";

            for (int i = 0; i < this.types.length; i++) {
               String monName = this.types[i].getName(true).toLowerCase();
               result = result + monName;
               if (i < this.types.length - 2) {
                  result = result + ", ";
               } else if (i < this.types.length - 1) {
                  result = result + " and ";
               }
            }

            return result + " " + end;
         }
      }
   }

   @Override
   public void affectStartMonsters(List<Monster> monsters) {
      monsters.clear();

      for (MonsterType t : this.types) {
         monsters.add(t.makeEnt());
      }
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return this.makeBaseActor(big);
   }

   protected Actor makeBaseActor(boolean big) {
      int gap = 2;
      if (big) {
         Pixl p = new Pixl(0);
         Actor a;
         if (this.types.length == 1) {
            a = new EntPanelInventory(this.types[0].makeEnt()).withoutDice().getFullActor();
         } else {
            a = multiMonsterPortraits(Arrays.asList(this.types));
         }

         p.actor(a, com.tann.dice.Main.width / 2);
         return p.pix();
      } else {
         Pixl p = new Pixl(0);
         p.image(Images.equalsBig, Colours.green).gap(2);
         p.actor(multiMonsterPortraits(Arrays.asList(this.types)));
         return p.pix();
      }
   }

   public static boolean onlyOneType(List<MonsterType> types) {
      List<MonsterType> var1 = new ArrayList<>(types);
      Tann.uniquify(var1);
      return var1.size() == 1;
   }

   private static Actor multiMonsterPortraits(List<MonsterType> types) {
      if (types.size() == 0) {
         return new Actor();
      } else if (types.size() == 1) {
         return new ImageActor(types.get(0).portrait, true);
      } else if (onlyOneType(types)) {
         return new Pixl().text(types.size() + "x").gap(1).image(types.get(0).portrait).pix();
      } else {
         List<String> names = new ArrayList<>();

         for (int i = 0; i < types.size(); i++) {
            names.add(types.get(i).getName(false));
         }

         Tann.uniquify(names);
         int gap = 2;
         Pixl p = new Pixl();

         for (int i = 0; i < types.size(); i++) {
            p.image(types.get(i).portrait, true);
            if (i < types.size() - 1) {
               p.gap(2);
            }
         }

         return p.pix();
      }
   }

   @Override
   public void onPick(DungeonContext context) {
      this.resetFightLogOnPick();
   }

   @Override
   public boolean skipTest() {
      return true;
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }

   public static List<Modifier> makeDesigned() {
      List<Modifier> result = new ArrayList<>();
      result.addAll(
         Arrays.asList(
            new Modifier(
               -1.0F,
               "Bossilisk",
               new GlobalLevelRequirement(new LevelRequirementRange(4), new GlobalSetMonsters(MonsterTypeLib.byNames("basilisk", "basilisk")))
            ),
            new Modifier(
                  -2.0F,
                  "Rude Awakening",
                  new GlobalLevelRequirement(new LevelRequirementRange(1), new GlobalSetMonsters(MonsterTypeLib.byNames("alpha", "wolf")))
               )
               .rarity(Rarity.HALF),
            new Modifier(
                  -2.0F,
                  "RNG Nest",
                  new GlobalLevelRequirement(
                     new LevelRequirementRange(3), new GlobalSetMonsters(MonsterTypeLib.byNames("dragon egg", "dragon egg", "dragon egg"))
                  )
               )
               .rarity(Rarity.HUNDREDTH)
         )
      );
      return result;
   }
}
