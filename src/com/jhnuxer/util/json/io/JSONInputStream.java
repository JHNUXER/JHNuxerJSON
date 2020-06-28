package com.jhnuxer.util.json.io;

import com.jhnuxer.util.json.JSONArray;
import com.jhnuxer.util.json.JSONObject;
import com.jhnuxer.util.json.JSONTools;
import com.jhnuxer.util.json.JSONType;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class JSONInputStream extends DataInputStream {
  
  public JSONInputStream(InputStream in) {
    super(in);
  }
  
  public Object receive() {
    try {
      JSONType type = JSONType.values()[readInt()];
      switch (type) {
        case ARRAY: if (true) {
          JSONArray jsar = new JSONArray();
          int len = readInt();
          for (int i = 0; i < len; i++)
            jsar.add(receive());
          return jsar;
        } break;
        case OBJECT: if (true) {
          JSONObject json = new JSONObject();
          int len = readInt();
          for (int i = 0; i < len; i++)
            json.put(readUTF(), receive());
          return json;
        } break;
        case STRING:
          return readUTF();
        case NUMBER:
          return readDouble();
        case BOOL:
          return readByte() != 0;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  public <T> T receive(Class<T> type) { return receive(type, null); }
  public <T> T receive(Class<T> type, T def) {
    return JSONTools.typeConvert(type, receive(), def);
  }
  public <T> T castReceive(Class<T> type) { return castReceive(type, null); }
  public <T> T castReceive(Class<T> type, T def) {
    return JSONTools.typeCast(type, receive(), def);
  }
  public JSONObject receiveObject() { return receiveObject(null); }
  public JSONObject receiveObject(JSONObject def) { return castReceive(JSONObject.class, def); }
  public JSONArray receiveArray() { return receiveArray(null); }
  public JSONArray receiveArray(JSONArray jsar) {
    return castReceive(JSONArray.class, jsar);
  }
  public String receiveStr() { return receiveStr(null); }
  public String receiveStr(String def) { return castReceive(String.class, def); }
  public double receiveDouble() { return receiveDouble(0D); }
  public double receiveDouble(double def) {
    Object val = receive();
    if (val instanceof Double) return (double)val;
    else if (val instanceof Long) return (double)((long)val);
    return def;
  }
  public float receiveFloat() { return receiveFloat(0f); }
  public float receiveFloat(float def) { return (float)receiveDouble(def); }
  public long receiveLong() { return receiveLong(0); }
  public long receiveLong(long def) {
    Object val = receive();
    if (val instanceof Long) return (long)val;
    else if (val instanceof Double) return (long)((double)val);
    return def;
  }
  public int receiveInt() { return receiveInt(0); }
  public int receiveInt(int n) { return (int)receiveLong(n); }
  public short receiveShort() { return receiveShort(0); }
  public short receiveShort(int n) { return (short)receiveLong(n); }
  public short receiveShort(short s) { return receiveShort((int)s); }
  public byte receiveByte() { return receiveByte(0); }
  public byte receiveByte(int n) { return (byte)receiveLong(n); }
  public byte receiveByte(byte n) { return receiveByte((int)n); }
  public boolean receiveBool() { return receiveBool(false); }
  public boolean receiveBool(boolean def) {
    Object val = receive();
    if (val instanceof Boolean) return (boolean)val;
    return def;
  }
  public boolean receiveBoolean() { return receiveBool(); }
  public boolean receiveBoolean(boolean def) { return receiveBool(def); }
  
}
