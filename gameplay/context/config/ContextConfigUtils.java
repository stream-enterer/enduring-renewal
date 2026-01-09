package com.tann.dice.gameplay.context.config;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.cursed.BlursedConfig;
import com.tann.dice.gameplay.context.config.cursed.CurseConfig;
import com.tann.dice.gameplay.context.config.cursed.CurseHyperConfig;
import com.tann.dice.gameplay.context.config.cursed.CurseUltraConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.AlternateHeroesConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.ChoosePartyConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.ClassicConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.DemoConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.GenerateHeroesConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.LootConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.RaidConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.ShortcutConfig;
import com.tann.dice.gameplay.context.config.misc.BalanceConfig;
import com.tann.dice.gameplay.context.config.misc.DebugConfig;
import com.tann.dice.gameplay.context.config.misc.InstantConfig;
import com.tann.dice.gameplay.context.config.misc.PickConfig;
import com.tann.dice.gameplay.mode.creative.WishMode;
import com.tann.dice.gameplay.mode.creative.custom.CustomMode;
import com.tann.dice.gameplay.mode.creative.pastey.PasteConfig;
import com.tann.dice.gameplay.mode.cursey.BlurtraMode;
import com.tann.dice.gameplay.mode.cursey.BlyptraMode;
import com.tann.dice.gameplay.mode.debuggy.CustomFightMode;
import com.tann.dice.gameplay.mode.general.DreamMode;
import com.tann.dice.gameplay.mode.general.nightmare.NightmareConfig;
import com.tann.dice.util.TannLog;

public class ContextConfigUtils {
   public static ContextConfig fromJson(String className, String serial) {
      if (className == null) {
         return new PasteConfig();
      } else {
         switch (className) {
            case "ClassicConfig":
               return new ClassicConfig(serial);
            case "DreamConfig":
               return new DreamMode.DreamConfig(Difficulty.valueOf(serial));
            case "ChoosePartyConfig":
               return ChoosePartyConfig.fromString(serial);
            case "DebugConfig":
               return new DebugConfig();
            case "CurseConfig":
               return new CurseConfig();
            case "CurseHyperConfig":
               return new CurseHyperConfig();
            case "BlursedConfig":
               return new BlursedConfig();
            case "InstantConfig":
               return new InstantConfig(serial);
            case "DemoConfig":
               return new DemoConfig(serial);
            case "ShortcutConfig":
               return new ShortcutConfig(serial);
            case "LootConfig":
               return new LootConfig(serial);
            case "GenerateHeroesConfig":
               return new GenerateHeroesConfig(serial);
            case "AlternateHeroesConfig":
               return new AlternateHeroesConfig(serial);
            case "RaidConfig":
               return new RaidConfig(serial);
            case "PasteConfig":
               return new PasteConfig();
            case "BalanceConfig":
               return new BalanceConfig(serial);
            case "PickConfig":
               return new PickConfig();
            case "CustomFightConfig":
               return new CustomFightMode.CustomFightConfig();
            case "NightmareConfig":
               return new NightmareConfig(serial);
            case "CustomConfig":
               return new CustomMode.CustomConfig();
            case "CurseUltraConfig":
               return new CurseUltraConfig();
            case "BlurtraConfig":
               return new BlurtraMode.BlurtraConfig();
            case "WishConfig":
               return new WishMode.WishConfig();
            case "BlyptraConfig":
               return new BlyptraMode.BlyptraConfig();
            default:
               TannLog.log("Unable to deserialise context: " + className + ":" + serial, TannLog.Severity.error);
               return null;
         }
      }
   }
}
