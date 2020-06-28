package com.jhnuxer.util.json;

public interface JSONSerializable {

  public Object toJSONValue();

  public default JSONObject newJSONObject() { return new JSONObject(); }
  public default JSONArray newJSONArray() { return new JSONArray(); }

}
