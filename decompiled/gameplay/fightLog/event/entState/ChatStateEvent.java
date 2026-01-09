package com.tann.dice.gameplay.fightLog.event.entState;

import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.util.Tann;

public class ChatStateEvent extends StateEvent {
   public static final ChatStateEvent Noob = new ChatStateEvent(1.0E-4F, "lol noob", "get rekt");
   public static final ChatStateEvent OtherKillArrow = new ChatStateEvent(0.2F, "nice shot");
   public static final ChatStateEvent SaveThank = new ChatStateEvent(
      0.1F,
      "ty",
      "thanks",
      "thank you",
      "thx",
      "ta",
      "you [yellow]saved[cu] me",
      "tyty",
      "[notranslate]merci",
      "[notranslate]cheers",
      "[notranslate]valeu",
      "[notranslate]danke",
      "[second heart]",
      "phew [red][fullHeart]"
   );
   public static final ChatStateEvent SaveThankKill = new ChatStateEvent(0.05F, "phew", "nice", "sweet");
   public static final ChatStateEvent SelfSave = new ChatStateEvent(0.04F, "sorted", "whew", "I live!", "I'm ok");
   public static final ChatStateEvent MuchPoisonDamage = new ChatStateEvent(0.15F, "[green][sin]urgh", "[green]I don't feel so good", "[green]blergh");
   public static final ChatStateEvent MuchRegen = new ChatStateEvent(0.05F, "I feel immortal", "so much regen");
   public static final ChatStateEvent MuchTime = new ChatStateEvent(
      0.02F, "how long have we been here?", "are we stuck here forever?", "what day is it?", "did I leave the oven on?"
   );
   public static final ChatStateEvent MuchIncomingOverkill = new ChatStateEvent(0.15F, "uhoh", "welp", "help!", "yikes");
   public static final ChatStateEvent TripleOrMoreKill = new ChatStateEvent(0.15F, "too easy", "got 'em", "whoah");
   public static final ChatStateEvent HeroResurrected = new ChatStateEvent(
      0.05F, "huh??", "what happened?", "I saw three frogs", "yawn", "where was I?", "I feel cold", "back again...", "just resting"
   );
   public static final ChatStateEvent HeroResurrectedMulti = new ChatStateEvent(
      0.1F, "guh", "my bones ache", "i need a break", "no more", "the frogs tire of me"
   );
   public static final ChatStateEvent AdjacentSuicide = new ChatStateEvent(0.1F, "rip", "be carefu-");
   public static final ChatStateEvent DodgedAttack = new ChatStateEvent(0.01F, "too slow", "no chance", "[i]ducks");
   public static final ChatStateEvent Redirected = new ChatStateEvent(0.01F, "[i]flinch", "thanks!!", "whew");
   public static final ChatStateEvent AdjacentOverkillMonsterMuchly = new ChatStateEvent(0.1F, "dang", "jeez");
   public static final ChatStateEvent Spiked = new ChatStateEvent(0.1F, "ouch", "oww!");
   public static final ChatStateEvent ZombieGross = new ChatStateEvent(0.3F, "yuk", "ew", "[green][sin]gross");
   public static final ChatStateEvent Z0mbieLaugh = new ChatStateEvent(0.5F, "lol!", "gotcha");
   public static final ChatStateEvent HydraBehead = new ChatStateEvent(1.0F, "agh", "[notranslate]blork");
   public static final ChatStateEvent Provoke = new ChatStateEvent(1.0F, "grr", "roar!", "argh");
   public static final ChatStateEvent WizzResurrect = new ChatStateEvent(1.0F, "arise!", "return!", "animate!", "rouse!", "vitalify!");
   public static final ChatStateEvent Weak = new ChatStateEvent(
      1.0F, "[notranslate]HISSS", "[notranslate]CHKKT", "[notranslate]KRRKT", "[notranslate]TSSRH", "[notranslate]RSSCH"
   );
   public static final ChatStateEvent BansheeWail = new ChatStateEvent(1.0F, "[notranslate]SHRIIU", "[notranslate]CZZHZ", "[notranslate]WRAAU");
   public static final ChatStateEvent JesterFlick = new ChatStateEvent(0.03F, "heh", "haha!", "knock, knock");
   public static final ChatStateEvent BlazeChat = new ChatStateEvent(0.1F, "BURN!", "Flames consume you!");
   public static final ChatStateEvent LudusCheer = new ChatStateEvent(0.3F, "Sweet!", "Lock it");
   public static final ChatStateEvent LudusBoo = new ChatStateEvent(0.5F, "Dang :(", "Oof");
   public static final ChatStateEvent Brother = new ChatStateEvent(1.0F, "Goodbye, brother");
   public static final ChatStateEvent GamblerCheer = new ChatStateEvent(0.3F, "Jackpot!", "100% skill");
   public static final ChatStateEvent GamblerBoo = new ChatStateEvent(0.3F, "Snake eyes", "Out of luck", ":/");
   public static final ChatStateEvent Undizzy = new ChatStateEvent(0.05F, "huh?", "what's happening?", "zwip zwip zwip", "time loop??", "deja vu");
   final float chance;
   final String[] chatter;

   public ChatStateEvent(float chance, String... chatter) {
      this.chance = chance;
      this.chatter = chatter;
   }

   @Override
   public void act(EntPanelCombat panel) {
      if (!OptionLib.DISABLE_CHAT.c()) {
         panel.addSpeechBubble(Tann.random(this.chatter));
      }
   }

   public void actWithChance(EntPanelCombat entPanelCombat) {
      if (this.chance()) {
         this.act(entPanelCombat);
      }
   }

   @Override
   public boolean chance() {
      return cseChance(this.chance);
   }

   public static boolean cseChance(float baseChance) {
      return Tann.chance(baseChance * (OptionLib.TRIPLE_CHAT.c() ? 3 : 1));
   }
}
