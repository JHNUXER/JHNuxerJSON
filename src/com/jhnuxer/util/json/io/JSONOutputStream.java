package com.jhnuxer.util.json.io;

import com.jhnuxer.util.json.JSONArray;
import com.jhnuxer.util.json.JSONObject;
import com.jhnuxer.util.json.JSONSerializable;
import com.jhnuxer.util.json.JSONTools;
import static com.jhnuxer.util.json.JSONTools.typeOf;
import com.jhnuxer.util.json.JSONType;
import static com.jhnuxer.util.json.JSONType.INVALID;
import static com.jhnuxer.util.json.JSONType.NULL;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class JSONOutputStream extends DataOutputStream {
  
  public JSONOutputStream(OutputStream out) {
    super(out);
  }
  
  public boolean send(Object obj) {
    try {
      JSONType jsonType = typeOf(obj);
      if (jsonType != INVALID) {
        writeInt(jsonType.ordinal());
        if (jsonType != NULL && obj != null) {
          if (obj instanceof JSONSerializable)
            return send(((JSONSerializable) obj).toJSONValue());
          else if (obj instanceof UUID || obj instanceof LocalDateTime)
            return send(obj.toString());
          else if (obj instanceof Date)
            return send(JSONTools.DATE_FORMAT.format((Date)obj));
          
          switch (jsonType) {
            case ARRAY: if (true) {
              JSONArray jsar = (JSONArray)obj;
              writeInt(jsar.size());
              for (Object o : jsar)
                if (!send(o)) return false;
            } break;
            case OBJECT: if (true) {
              JSONObject json = (JSONObject)obj;
              writeInt(json.size());
              for (String key : json.keySet()) {
                writeUTF(key);
                if (!send(json.get(key))) return false;
              }
            } break;
            case STRING:
              writeUTF((String)obj);
              break;
            case NUMBER:
              writeDouble((double)obj);
              break;
            case BOOL:
              writeByte((byte)((boolean)obj ? 1 : 0));
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
  
}
