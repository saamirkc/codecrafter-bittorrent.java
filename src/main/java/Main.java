//import com.dampcake.bencode.Bencode;
//import com.dampcake.bencode.Type;
//import com.google.gson.Gson;
//
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//// import com.dampcake.bencode.Bencode; - available if you need it!
//
//public class Main {
//  private static final Gson gson = new Gson();
//
//  public static void main(String[] args) throws Exception {
//    // You can use print statements as follows for debugging, they'll be visible when running tests.
//    // System.out.println("Logs from your program will appear here!");
//    String command = args[0];
//    if ("decode".equals(command)) {
//      String bencodedValue = args[1];
//      Object decoded;
//      try {
//        decoded = decodeBencode(bencodedValue);
//      } catch (RuntimeException e) {
//        System.out.println(e.getMessage());
//        return;
//      }
//      System.out.println(gson.toJson(decoded));
//
//    } else {
//      System.out.println("Unknown command: " + command);
//    }
//  }
//
//  static String decodeBencode(String bencodedString) {
//    Bencode bencode = new Bencode();
//    char firstChar = bencodedString.charAt(0);
//    Object decoded;
//    if (Character.isDigit(bencodedString.charAt(0))) {
//      int firstColonIndex = 0;
//      for (int i = 0; i < bencodedString.length(); i++) {
//        if (bencodedString.charAt(i) == ':') {
//          firstColonIndex = i;
//          break;
//        }
//      }
//      int length =
//              Integer.parseInt(bencodedString.substring(0, firstColonIndex));
//      decoded = bencodedString.substring(firstColonIndex + 1,
//              firstColonIndex + 1 + length);
//    } else if (firstChar == 'i') {
//      // bencoded number
//      decoded = bencode.decode(bencodedString.getBytes(StandardCharsets.UTF_8),
//              Type.NUMBER);
//    } else if (firstChar == 'l') {
//      // bencoded list
//      decoded = bencode.decode(bencodedString.getBytes(StandardCharsets.UTF_8),
//              Type.LIST);
//    } else {
//      throw new RuntimeException("Unsupported bencoded value");
//    }
//    return gson.toJson(decoded);
//  }
//}
//

import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
  private static final Gson gson = new Gson();

  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: java Main decode <bencodedValue>");
      return;
    }

    String command = args[0];
    if ("decode".equals(command)) {
      String bencodedValue = args[1];
      Object decoded;
      try {
        decoded = decodeBencode(bencodedValue);
      } catch (RuntimeException e) {
        System.out.println(e.getMessage());
        return;
      }
      System.out.println(gson.toJson(decoded));

    } else {
      System.out.println("Unknown command: " + command);
    }
  }

  static Object decodeBencode(String bencodedString) {
    Bencode bencode = new Bencode();
    char firstChar = bencodedString.charAt(0);
    Object decoded;
    if (Character.isDigit(firstChar)) {
      // Handle bencoded string
      int firstColonIndex = bencodedString.indexOf(':');
      if (firstColonIndex == -1) {
        throw new RuntimeException("Invalid bencoded string format");
      }
      int length = Integer.parseInt(bencodedString.substring(0, firstColonIndex));
      decoded = bencodedString.substring(firstColonIndex + 1, firstColonIndex + 1 + length);
    } else if (firstChar == 'i') {
      // Handle bencoded number
      int endIndex = bencodedString.indexOf('e');
      if (endIndex == -1) {
        throw new RuntimeException("Invalid bencoded number format");
      }
      decoded = Long.parseLong(bencodedString.substring(1, endIndex)); // Assuming numbers are Long
    } else if (firstChar == 'l') {
      // Handle bencoded list
      if (bencodedString.length() < 2 || bencodedString.charAt(bencodedString.length() - 1) != 'e') {
        throw new RuntimeException("Invalid bencoded list format");
      }
      decoded = decodeBencodedList(bencodedString.substring(1, bencodedString.length() - 1));
    } else {
      throw new RuntimeException("Unsupported bencoded value");
    }
    return decoded;
  }

  static List<Object> decodeBencodedList(String bencodedListString) {
    List<Object> list = new ArrayList<>();
    if (bencodedListString.isEmpty()) {
      return list;
    }
    String[] elements = bencodedListString.split("(?<=\\d)(?=\\D)");
    for (String element : elements) {
      list.add(decodeBencode(element));
    }
    return list;
  }
}