package com.jhnuxer.util.json;

public interface JSONObjectDeserializer<T> {

  public T deserialize(JSONObject json);

}
