package com.jhnuxer.util.json;

import com.jhnuxer.util.parser.AbstractParser;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public final class JSONTools {

  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyyG hh:mm:ss.SSSSSSSSS");
  public static final String REX_UUID = "[0-9ABCDEFabcdef]{8}\\-[0-9ABCDEFabcdef]{4}\\-[0-9ABCDEFabcdef]{4}\\-[0-9ABCDEFabcdef]{4}\\-[0-9ABCDEFabcdef]{12}";
  public static String getDateString(Date date) { return DATE_FORMAT.format(date); }
  
  private static final JSONParser PARSER;
  static {
    JSONParser tParse = null;
    try {
      tParse = new JSONParser();
    } catch (AbstractParser.InvalidExceptionTypeError e) {
      e.printStackTrace();
      System.exit(-1);
    }
    PARSER = tParse;
  }
  private static Map<Class,JSONObjectDeserializer> jsonObjectDeserializers = new HashMap<>();
  public static <T> void registerObjectDeserializer(Class<T> type, JSONObjectDeserializer<T> jsd) {
    jsonObjectDeserializers.put(type, jsd);
  }
  public static <T> JSONObjectDeserializer<T> getObjectDeserializer(Class<T> type) {
    return (JSONObjectDeserializer<T>)jsonObjectDeserializers.get(type);
  }
  
  public static Object parse(String source, Reader reader) throws JSONParserException {
    return PARSER.parse(source, reader);
  }
  public static Object parse(File file) throws JSONParserException {
    return PARSER.parse(file);
  }
  public static Object parse(String str) throws JSONParserException {
    return PARSER.parse(str);
  }
  @Deprecated
  public static Object parse(Reader reader) {
    try {
      return parse("<Unspecified Reader>", reader);
    } catch (JSONParserException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  public static JSONType typeof(Object obj) { return typeOf(obj); }
  public static JSONType typeOf(Object obj) {
    if (obj == null)
      return JSONType.NULL;
    else if (obj instanceof JSONObject || obj instanceof org.json.simple.JSONObject)
      return JSONType.OBJECT;
    else if (obj instanceof JSONArray || obj instanceof org.json.simple.JSONArray)
      return JSONType.ARRAY;
    else if (obj instanceof String)
      return JSONType.STRING;
    else if (obj instanceof Boolean)
      return JSONType.BOOL;
    else if (obj instanceof Byte || obj instanceof Short || obj instanceof Integer || obj instanceof Long || obj instanceof Float || obj instanceof Double)
      return JSONType.NUMBER;
    return JSONType.INVALID;
  }
  
  public static boolean boolCast(Object val) { return boolCast(val, false); }
  public static boolean boolCast(Object val, boolean def) {
    return val != null && val instanceof Boolean ? (boolean)val : def ;
  }
  public static long longCast(Object val) { return longCast(val, 0); }
  public static long longCast(Object val, long def) {
    if (val != null) {
      if (val instanceof Double) return (long)((double)val);
      else if (val instanceof Long) return (long)val;
    }
    return def;
  }
  public static double doubleCast(Object val) { return doubleCast(val, 0d); }
  public static double doubleCast(Object val, double def) {
    if (val != null) {
      if (val instanceof Long) return (double)((long)val);
      else if (val instanceof Double) return (double)val;
    }
    return def;
  }
  public static int intCast(Object val) { return intCast(val, 0); }
  public static int intCast(Object val, int def) { return (int)longCast(val, def); }
  public static short shortCast(Object val) { return shortCast(val, 0); }
  public static short shortCast(Object val, int def) { return (short)longCast(val, def); }
  public static short shortCast(Object val, short def) { return shortCast(val, (int)def); }
  public static short byteCast(Object val) { return byteCast(val, 0); }
  public static short byteCast(Object val, int def) { return (byte)longCast(val, def); }
  public static short byteCast(Object val, short def) { return byteCast(val, (int)def); }
  public static float floatCast(Object val) { return floatCast(val, 0); }
  public static float floatCast(Object val, float def) { return (float)doubleCast(val, def); }
  public static UUID uuidCast(Object val) { return uuidCast(val, null); }
  public static UUID uuidCast(Object val, UUID def) {
    if (val instanceof UUID) return (UUID)val;
    String str = typeCast(String.class, val);
    return str != null && str.matches(REX_UUID) ? UUID.fromString(str) : def ;
  }
  public static Date dateCast(Object val) { return dateCast(val, null); }
  public static Date dateCast(Object val, Date def) {
    if (val instanceof Date) return (Date)val;
    String str = typeCast(String.class, val);
    Date res = def;
    if (str != null)
      try { res = DATE_FORMAT.parse(str); } catch (ParseException e) { }
    return res;
  }
  public static LocalDateTime localDateTimeCast(Object val) {
    return localDateTimeCast(val, null);
  }
  public static LocalDateTime localDateTimeCast(Object val, LocalDateTime def) {
    if (val instanceof LocalDateTime) return (LocalDateTime)val;
    String str = typeCast(String.class, val);
    LocalDateTime res = def;
    try {
      res = LocalDateTime.parse(str);
    } catch (DateTimeParseException dtpe) {
    }
    return res;
  }
  
  public static <T> T typeCast(Class<T> type, Object val) { return typeCast(type, val, null); }
  public static <T> T typeCast(Class<T> type, Object val, T def) {
    if (type.isAssignableFrom(val.getClass()))
      return (T)val;
    else
      return def;
  }
  public static <T> T typeConvert(Class<T> type, Object val) { return typeConvert(type, val, null); }
  public static <T> T typeConvert(Class<T> type, Object val, T def) {
    JSONType jst = typeOf(val);
    if (jst == JSONType.OBJECT) {
      JSONObjectDeserializer<T> des = getObjectDeserializer(type);
      if (des != null)
        return des.deserialize((JSONObject)val);
      else
        return typeCast(type, val);
    }
    return typeCast(type, val);
  }
  public static <K,V> Map<K,V> mapConvert(Class<K> keyType, Class<V> valType, JSONObject json) {
    return mapConvert(keyType, valType, json, (V)null);
  }
  public static <K,V> Map<K,V> mapConvert(Class<K> keyType, Class<V> valType, JSONObject json, V defVal) {
    return mapConvert(keyType, valType, json, null, defVal);
  }
  public static <K,V> Map<K,V> mapConvert(Class<K> keyType, Class<V> valType, JSONObject json, MapKeyGenerator<K> keyGen) {
    return mapConvert(keyType, valType, json, keyGen, null);
  }
  public static <K,V> Map<K,V> mapConvert(Class<K> keyType, Class<V> valType, JSONObject json, MapKeyGenerator<K> keyGen, V defaultVal) {
    return mapConvert(keyType, valType, json, null, keyGen, defaultVal);
  }
  public static <K,V> Map<K,V> mapConvert(Class<K> keyType, Class<V> valType, JSONObject json, Map<K,V> def) {
    return mapConvert(keyType, valType, json, def, null, null);
  }
  public static interface MapKeyGenerator<K> {
    public K generateKey(String key);
  }
  /**
   * Converts a JSONObject into a Map with the given key- and value-types. If a
   * given key cannot be converted to the key-type, the MapKeyGenerator is used
   * to produce a compatible unique key. If a given value cannot be converted to
   * the value-type, the given default value is used. If the JSONObject is null,
   * the default Map (<code>def</code>) is returned.
   * 
   * @param  <K>           The key-type of the Map.
   * @param  <V>           The value-type of the Map.
   * @param  keyType       The Class for the key-type of the Map.
   * @param  valType       The Class for the value-type of the Map.
   * @param  json          The JSONObject to convert.
   * @param  def           The Map to return if the JSONObject is null.
   * @param  defaultKeyGen The MapKeyGenerator to generate keys if the given
   *                       keys cannot be type-converted to the map's key-type.
   * @param  defaultVal    The default value to be used if the actual value for
   *                       the given key cannot be type-converted into the map's
   *                       value-type.
   * @return               A HashMap containing the converted key-value pairs of
   *                       the JSONObject.
   */
  public static <K,V> Map<K,V> mapConvert(Class<K> keyType, Class<V> valType, JSONObject json, Map<K,V> def, MapKeyGenerator<K> defaultKeyGen, V defaultVal) {
    if (json != null) {
      Map<K,V> map = new HashMap<>();
      for (String key : json.keySet()) {
        K newKey = typeConvert(keyType, key);
        if (newKey == null && defaultKeyGen != null) newKey = defaultKeyGen.generateKey(key);
        V newVal = json.get(valType, key, defaultVal);
        map.put(newKey, newVal);
      }
      return map;
    }
    return def;
  }
  public static <T> List<T> listConvert(Class<T> type, JSONArray jsar) {
    return listConvert(new ArrayList<>(), type, jsar);
  }
  public static <T> List<T> listConvert(List<T> list, Class<T> type, JSONArray jsar) {
    if (jsar == null || jsar.isEmpty()) {
      JSONObjectDeserializer<T> jsod = JSONTools.getObjectDeserializer(type);
      if (jsod != null) {
        for (int i = 0; i < jsar.size(); i++)
          list.add((T)jsod.deserialize(jsar.getObject(i)));
      }
    }
    return list;
  }
  public static <T> T[] arrayConvert(Class<T> type, JSONArray jsar) { return arrayConvert(type, jsar, (T[])java.lang.reflect.Array.newInstance(type, 0)); }
  public static <T> T[] arrayConvert(Class<T> type, JSONArray jsar, T[] def) {
    if (jsar == null || jsar.isEmpty())
      return def;
    JSONObjectDeserializer jsod = JSONTools.getObjectDeserializer(type);
    if (jsod != null) {
      T[] arr = (T[])java.lang.reflect.Array.newInstance(type, jsar.size());
      for (int i = 0; i < arr.length; i++) {
        Object res = jsod.deserialize(jsar.getObject(i));
        arr[i] = (T)res;
      }
      return arr;
    }
    return def;
  }
  
  public static String stringify(Object obj) { return stringify(obj, false); }
  public static String stringify(Object obj, boolean nice) { return stringify(obj, nice, 0); }
  public static String stringify(Object obj, boolean nice, int idnt) { return stringify(obj, nice, idnt, "  "); }
  public static String stringify(Object value, boolean nicify, int indentCount, String indentStr) {
    String indent = "";
    String result = "";
    for (int i = 0; i < indentCount; i++) indent += indentStr;
    if (value == null) return indent + "null";
    if (value.getClass().isArray()) {
      Object[] values = (Object[])value;
      JSONArray jsar = new JSONArray();
      for (Object val : values) jsar.add(val);
      result += stringify(jsar, nicify, indentCount);
    } else if (value instanceof JSONSerializable) {
      result += stringify(((JSONSerializable)value).toJSONValue(), nicify, indentCount);
    } else if (value instanceof JSONObject) {
      result += indent + stringifyObject((JSONObject)value, nicify, indentCount + 1);
    } else if (value instanceof JSONArray) {
      result += indent + stringifyArray((JSONArray)value, nicify, indentCount + 1);
    } else if (value instanceof String) {
      String strx = (String)value;
      result += "\"" + strx.replace("\\", "\\\\").replace("\n", "\\n").replace("\t", "\\t").replace("\r", "\\r").replace("\"", "\\\"") + "\"";
    } else {
      result += value.toString();
    }
    return result;
  }
  public static String stringifyArray(JSONArray jsar, boolean nice, int idnt) {
    String str = "[";
    for (int i = 0; i < idnt; i++) str = "  " + str;
    if (nice) str += "\n";
    for (int i = 0; i < jsar.size(); i++) {
      if (nice) str += "  ";
      str += stringify(jsar.get(i), nice);
      if (i < jsar.size() - 1) {
        str += ",";
        if (nice) str += "\n";
      }
    }
    return str + "]";
  }
  public static String stringifyObject(JSONObject json, boolean nice, int idnt) {
    String str = "{";
    if (nice) str += "\n";
    String idnx = "";
    for (int i = 0; i < idnt; i++) {
      if (i < idnt - 1) str = "  " + str;
      idnx += "  ";
    }
    int i = 0;
    for (Map.Entry<String,Object> entry : json.entrySet()) {
      String keyK = stringify(entry.getKey(), nice);
      Object valu = entry.getValue();
      // System.out.println("TESTING...");
      if (valu instanceof JSONObject && ((JSONObject)valu) == json) System.out.println("JSON CONTAINS ITSELF!");
      String tS = keyK + ": " + stringify(valu, nice, idnt + 1);
      if (i++ < json.size() - 1) tS += ",";
      str += (nice && !(valu instanceof JSONObject || valu instanceof JSONArray) ? (idnx + "  ") : "") + tS + (nice ? "\n" : "");
    }
    return str + idnx + "}";
  }

}
