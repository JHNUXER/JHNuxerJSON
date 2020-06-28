package com.jhnuxer.util.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class DirectJSONObjectDeserializer<T> implements JSONObjectDeserializer<T> {
  
  private final Class<T>       type                     ;
  private final Constructor<T> cons                     ;
  private final Set<Field>     fields = new HashSet<>() ;
  
  public DirectJSONObjectDeserializer(Class<T> type) throws Exception {
    this.type = type                  ;
    this.cons = type.getConstructor() ;
    
    for (Field f : type.getDeclaredFields())
      fields.add(f);
  }
  
  @Override
  public T deserialize(JSONObject json) {
    try {
      T t = cons.newInstance();
      for (Field f : fields)
        f.set(t, json.get(f.getType(), f.getName()));
      return t;
    } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
      e.printStackTrace();
      return null;
    }
  }
  
}
