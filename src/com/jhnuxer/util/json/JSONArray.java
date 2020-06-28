package com.jhnuxer.util.json;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Represents a JSON array.
 * 
 * @author JHNuxer
 */
public class JSONArray extends ArrayList<Object> {

  public JSONArray() { super(); }
  public JSONArray(Object...objects) { for (Object obj : objects) add(obj); }
  public JSONArray(Iterable<?> values) { for (Object obj : values) add(obj); }
  
  public <T> T[] getArray(Class<T> type, int index) { return getArray(type, index, false); }
  @SuppressWarnings("unchecked")
  private static <T> T[] cArray(Class<T> type, int len) {
    return (T[])Array.newInstance(type, 0);
  }
  /**
   * Gets a JSONArray from this array, and converts it into a Java array of the
   * given type, converting array contents to the given Java type as necessary.
   * 
   * @param  <T>      The member-type of the Java Array.
   * @param  type     The Class of the member-type of the Java Array.
   * @param  index    The index of the JSONArray to convert.
   * @param  loopSafe Whether or not the method should use an Empty array as the
   *                  default value to avoid a NullPointerException in a 
   *                  for-each loop.
   * @return          A Java Array for the JSNOArray in this Array.
   */
  public <T> T[] getArray(Class<T> type, int index, boolean loopSafe) {
    T[] def = null;
    if (loopSafe) def = cArray(type, 0);
    return getArray(type, index, def);
  }
  /**
   * Gets a JSONArray from this array, and converts it into a Java array of the
   * given type, converting array contents to the given Java type as necessary.
   * 
   * @param  <T>   The member-type of the Java Array.
   * @param  type  The Class of the member-type of the Java Array.
   * @param  index The index of the JSONArray to convert.
   * @param  def   The default array to return if the value at the given index
   *               is null or not a JSONArray.
   * @return       A Java Array for the JSNOArray in this Array.
   */
  public <T> T[] getArray(Class<T> type, int index, T[] def) {
    Object value = get(index);
    if (value instanceof JSONArray) {
      JSONArray source = (JSONArray)value;
      JSONObjectDeserializer<T> jsod = JSONTools.getObjectDeserializer(type);
      if (jsod != null) {
        T[] arr = cArray(type, source.size());
        for (int i = 0; i < arr.length; i++) arr[i] = jsod.deserialize(source.getObject(i));
        return arr;
      }
      System.err.println("ERROR: No JSONObjectDeserializer found for type \""+type+"\"!");
    }
    return def;
  }
  public <T> T[] toArray(Class<T> type) { return JSONTools.arrayConvert(type, this); }
  public <T> T[] toArray(Class<T> type, T[] def) { return JSONTools.arrayConvert(type, this, def); }
  public <T> T get(Class<T> type, int n) { return get(type, n, null); }
  public <T> T get(Class<T> type,int n, T def) {
    return JSONTools.typeConvert(type, get(n), def);
  }
  public double getDouble(int n, double def) {
    Object val = super.get(n);
    if (val == null || !(val instanceof Double)) return def;
    return (double)val;
  }
  public long getLong(int n) { return (long)super.get(n); }
  public int getInt(int n) { return (int)getLong(n); }
  public short getShort(int n) { return (short)getLong(n); }
  public byte getByte(int n) { return (byte)getLong(n); }
  public boolean getBool(int n) { return (boolean)super.get(n); }
  public String getStr(int n) { return get(String.class, n); }
  public JSONArray getArray(int n) { return get(JSONArray.class, n); }
  public JSONObject getObject(int n) { return get(JSONObject.class, n); }

  @Override
  public String toString() { return JSONTools.stringify(this); }

}
