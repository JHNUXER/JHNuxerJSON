package com.jhnuxer.util.json;

import java.io.IOException;
import java.util.*;

public class JSONObject extends HashMap<String,Object> {

  public JSONObject() { super(); }

  public <T> T castGet(Class<T> type, String key) { return castGet(type, key, null); }
  public <T> T castGet(Class<T> type, String key, T def) {
    return JSONTools.typeCast(type, get(key), def);
  }
  public <T> T get(Class<T> type,String key) { return get(type, key, null); }
  public <T> T[] getArray(Class<T> type, String key) {
    JSONArray jsar = get(JSONArray.class, key);
    return JSONTools.arrayConvert(type, jsar);
  }
  public <T> T to(Class<T> type) { return to(type, null); }
  public <T> T to(Class<T> type, T t) { return JSONTools.typeConvert(type, this, t); }
  public <T> T get(Class<T> type, String key, T def) {
    return JSONTools.typeConvert(type, get(key), def);
  }
  public UUID getUUID(String key) { return getUUID(key, null); }
  public UUID getUUID(String key, UUID def) {
    String str = getStr(key);
    if (str == null) return def;
    return UUID.fromString(str);
  }
  public long getLong(String key) { return getLong(key, 0); }
  public int getInt(String key) { return getInt(key, 0); }
  public short getShort(String key) { return getShort(key, (short)0); }
  public byte getByte(String key) { return getByte(key, (byte)0); }
  public boolean getBool(String key) { return getBool(key, false); }
  public double getDouble(String key) { return getDouble(key, 0); }
  public float getFloat(String key) { return getFloat(key, 0); }
  public String getStr(String key) {
    return getStr(key, null);
    // return (String)super.get(key);
  }
  public String getString(String key) { return getStr(key); }
  public JSONArray getArray(String key) { return get(JSONArray.class,key); }
  public JSONObject getObject(String key) {
    return get(JSONObject.class,key);
  }
  public java.awt.Image loadImageFromPath(String key) {
    String path = getStr(key);
    if (path == null) return null;
    try {
      return javax.imageio.ImageIO.read(new java.io.File(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  public java.util.UUID parseUUID(String key) {
    String val = getStr(key);
    return val != null ? UUID.fromString(val) : null ;
  }
  public long getLong(String key,long def) {
    return JSONTools.longCast(get(key), def);
  }
  public int getInt(String key,int def) {
    return (int)getLong(key, def);
  }
  public short getShort(String key,short def) {
    return (short)getLong(key, def);
  }
  public byte getByte(String key,byte def) {
    return (byte)getLong(key, def);
  }
  public boolean getBool(String key,boolean def) {
    return JSONTools.boolCast(get(key), def);
  }
  public double getDouble(String key,double def) {
    return JSONTools.doubleCast(get(key), def);
  }
  public float getFloat(String key,float def) {
    return (float)getDouble(key, def);
  }
  public String getStr(String key, String def) { return getString(key, def); }
  public String getString(String key,String def) {
    return get(String.class, key, def);
  }
  public UUID parseUUID(String key, boolean randDef) {
    UUID res = parseUUID(key);
    return res == null ? (randDef ? UUID.randomUUID() : null) : res ;
  }
  
  public <K,V> Map<K,V> toMap(Class<K> keyType, Class<V> valType) {
    return JSONTools.mapConvert(keyType, valType, this);
  }
  public <K,V> Map<K,V> toMap(Class<K> keyType, Class<V> valType, Map<K,V> map) {
    return JSONTools.mapConvert(keyType, valType, this, map);
  }
  public <K,V> Map<K,V> toMap(Class<K> keyType, Class<V> valType, JSONTools.MapKeyGenerator<K> keyGen) {
    return JSONTools.mapConvert(keyType, valType, this, keyGen);
  }
  public <K,V> Map<K,V> toMap(Class<K> keyType, Class<V> valType, V defVal) {
    return JSONTools.mapConvert(keyType, valType, this, defVal);
  }
  public <K,V> Map<K,V> toMap(Class<K> keyType, Class<V> valType, JSONTools.MapKeyGenerator<K> keyGen, V defVal) {
    return JSONTools.mapConvert(keyType, valType, this, keyGen, defVal);
  }
  public <K,V> Map<K,V> toMap(Class<K> keyType, Class<V> valType, Map<K,V> def, JSONTools.MapKeyGenerator<K> keyGen, V defVal) {
    return JSONTools.mapConvert(keyType, valType, this, def, keyGen, defVal);
  }
  
  public <K,V> Map<K,V> getMap(Class<K> keyType, Class<V> valType, String key) {
    return JSONTools.mapConvert(keyType, valType, getObject(key));
  }
  public <K,V> Map<K,V> getMap(Class<K> keyType, Class<V> valType, String key, Map<K,V> map) {
    return JSONTools.mapConvert(keyType, valType, getObject(key), map);
  }
  public <K,V> Map<K,V> getMap(Class<K> keyType, Class<V> valType, String key, JSONTools.MapKeyGenerator<K> keyGen) {
    return JSONTools.mapConvert(keyType, valType, getObject(key), keyGen);
  }
  public <K,V> Map<K,V> getMap(Class<K> keyType, Class<V> valType, String key, V defVal) {
    return JSONTools.mapConvert(keyType, valType, getObject(key), defVal);
  }
  public <K,V> Map<K,V> getMap(Class<K> keyType, Class<V> valType, String key, JSONTools.MapKeyGenerator<K> keyGen, V defVal) {
    return JSONTools.mapConvert(keyType, valType, getObject(key), keyGen, defVal);
  }
  public <K,V> Map<K,V> getMap(Class<K> keyType, Class<V> valType, String key, Map<K,V> def, JSONTools.MapKeyGenerator<K> keyGen, V defVal) {
    return JSONTools.mapConvert(keyType, valType, getObject(key), def, keyGen, defVal);
  }
  
  public <T> List<T> getList(Class<T> type, String key) { return getList(type, key, new ArrayList<>()); }
  public <T> List<T> getList(Class<T> type, String key, List<T> list) {
    return JSONTools.listConvert(list, type, getArray(key));
  }

  public Object putJSONOnly(String key, Object val) {
    Object jsonVal = val instanceof JSONSerializable ? ((JSONSerializable) val).toJSONValue() : val ;
    return super.put(key, jsonVal);
  }

  @Override
  public String toString() { return JSONTools.stringify(this); }

}
