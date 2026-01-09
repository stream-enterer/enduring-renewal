package com.tann.dice.util.saves;

public class EncUtils {
   private static boolean crypCheck(String value) {
      return decrypt(encrypt(value)).equals(value);
   }

   static String encrypt(String value) {
      if (value == null) {
         return value;
      } else {
         StringBuilder sb = new StringBuilder();

         for (int i = 0; i < value.length(); i++) {
            sb.append((char)niceMod(value.charAt(i) + '2' - i % 50));
         }

         return sb.toString();
      }
   }

   static String decrypt(String string) {
      if (string != null && !string.isEmpty()) {
         StringBuilder sb = new StringBuilder();

         for (int i = 0; i < string.length(); i++) {
            sb.append((char)niceMod(string.charAt(i) - '2' + i % 50));
         }

         return sb.toString();
      } else {
         return string;
      }
   }

   private static int niceMod(int i) {
      return niceMod(i, 10000);
   }

   private static int niceMod(int i, int mod) {
      return (i % mod + mod) % mod;
   }
}
