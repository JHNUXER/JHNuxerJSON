package com.jhnuxer.util.json.net;

import com.jhnuxer.util.json.JSONArray;
import com.jhnuxer.util.json.JSONObject;
import com.jhnuxer.util.json.JSONSerializable;
import com.jhnuxer.util.json.JSONTools;
import com.jhnuxer.util.json.JSONType;
import static com.jhnuxer.util.json.JSONTools.typeof;
import static com.jhnuxer.util.json.JSONType.INVALID;
import static com.jhnuxer.util.json.JSONType.NULL;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class JSONSocket implements Closeable {
  
  private final Socket           socket ;
  private final DataInputStream  in     ;
  private final DataOutputStream out    ;
  
  public JSONSocket(String addr, int port) throws IOException {
    this(InetAddress.getByName(addr), port);
  }
  public JSONSocket(InetAddress addr, int port) throws IOException {
    this(new Socket(addr, port));
  }
  public JSONSocket(Socket sock) {
    DataInputStream tIN   = null ;
    DataOutputStream tOUT = null ;
    
    try {
      tIN  = new DataInputStream(sock.getInputStream())   ;
      tOUT = new DataOutputStream(sock.getOutputStream()) ;
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    
    this.socket           = sock ;
    this.in               = tIN  ;
    this.out              = tOUT ;
  }
  
  public Object receive() {
    try {
      JSONType type = JSONType.values()[in.readInt()];
      switch (type) {
        case ARRAY: if (true) {
          JSONArray jsar = new JSONArray();
          int len = in.readInt();
          for (int i = 0; i < len; i++)
            jsar.add(receive());
          return jsar;
        } break;
        case OBJECT: if (true) {
          JSONObject json = new JSONObject();
          int len = in.readInt();
          for (int i = 0; i < len; i++)
            json.put(in.readUTF(), receive());
          return json;
        } break;
        case STRING:
          return in.readUTF();
        case NUMBER:
          return in.readDouble();
        case BOOL:
          return in.readByte() == 1;
//        case NULL:
//        case INVALID:
//          return null;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  public boolean send(Object obj) {
    try {
      JSONType jsonType = typeof(obj);
      if (jsonType != INVALID) {
        out.writeInt(jsonType.ordinal());
        if (jsonType != NULL && obj != null) { // If NULL, just sending the type as NULL will suffice.
          if (obj instanceof JSONSerializable)
            return send(((JSONSerializable) obj).toJSONValue());
          else if (obj instanceof UUID || obj instanceof LocalDateTime)
            return send(obj.toString());
          else if (obj instanceof Date)
            return send(JSONTools.getDateString((Date)obj));
          
          switch (jsonType) {
            case ARRAY: if (true) {
              JSONArray jsar = (JSONArray)obj;
              out.writeInt(jsar.size());
              for (Object o : jsar)
                if (!send(o)) return false;
            } break;
            case OBJECT: if (true) {
              JSONObject json = (JSONObject)obj;
              out.writeInt(json.size());
              for (String key : json.keySet()) {
                out.writeUTF(key);
                if (!send(json.get(key))) return false;
              }
            } break;
            case STRING:
              out.writeUTF((String)obj);
              break;
            case NUMBER:
              out.writeDouble((double)obj);
              break;
            case BOOL:
              out.writeByte((byte)((boolean)obj ? 1 : 0));
              break;
          }
        }
        return true;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }
  public JSONObject receiveObject() { return receiveObject(null); }
  public JSONObject receiveObject(JSONObject def) {
    return JSONTools.typeCast(JSONObject.class, receive(), def);
  }
  public JSONArray receiveArray() { return receiveArray(null); }
  public JSONArray receiveArray(JSONArray def) {
    return JSONTools.typeCast(JSONArray.class, receive(), def);
  }
  public String receiveStr() { return receiveStr(null); }
  public String receiveStr(String def) { return JSONTools.typeCast(String.class, receive(), def); }
  public String receiveString() { return receiveStr(); }
  public String receiveString(String def) { return receiveStr(def); }
  public double receiveDouble() { return receiveDouble(0.0); }
  public double receiveDouble(double def) {
    Object val = receive();
    if (val instanceof Long) return (double)((long)val);
    else if (val instanceof Double) return (double)val;
    return def;
  }
  public float receiveFloat() { return receiveFloat(0f); }
  public float receiveFloat(float def) { return (float)receiveDouble(def); }
  public long receiveLong() { return receiveLong(0); }
  public long receiveLong(long def) {
    Object val = receive();
    if (val instanceof Double) return (long)((double)val);
    else if (val instanceof Long) return (long)val;
    return def;
  }
  public int receiveInt() { return receiveInt(0); }
  public int receiveInt(int def) { return (int)receiveLong(def); }
  public short receiveShort() { return receiveShort(0); }
  public short receiveShort(int def) { return (short)receiveLong(def); }
  public short receiveShort(short def) { return receiveShort((int)def); }
  public byte receiveByte() { return receiveByte(0); }
  public byte receiveByte(int def) { return (byte)receiveLong(def); }
  public byte receiveByte(byte def) { return receiveByte((int)def); }
  public boolean receiveBool() { return receiveBool(false); }
  public boolean receiveBool(boolean def) {
    Object val = receive();
    if (val instanceof Boolean) return (boolean)val;
    return def;
  }
  public boolean receiveBoolean() { return receiveBool(); }
  public boolean receiveBoolean(boolean def) { return receiveBool(def); }
  @Override
  public void close() throws IOException { socket.close(); }
  
}
