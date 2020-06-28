package com.jhnuxer.util.json;

import static com.jhnuxer.util.json.JSONTools.typeof;
import java.util.Stack;

import static com.jhnuxer.util.json.JSONType.ARRAY;
import static com.jhnuxer.util.json.JSONType.BOOL;
import static com.jhnuxer.util.json.JSONType.NULL;
import static com.jhnuxer.util.json.JSONType.NUMBER;
import static com.jhnuxer.util.json.JSONType.OBJECT;
import static com.jhnuxer.util.json.JSONType.STRING;
import com.jhnuxer.util.parser.AbstractParser;
import java.io.IOException;
import java.io.Reader;

public class JSONParser extends AbstractParser<JSONParserException,Object> {
  
  private static final String REX_WSC = "[\\s]";
  private static final String REX_WSS = "^[\\s]+$";
  
  private final Stack<Object> stack    = new Stack<>() ;
  private       JSONObject    currObj  = null          ;
  private       JSONArray     currArr  = null          ;
  private       JSONType      currType = null          ;
  private       String        currStr  = null          ;
  private       String        currKey  = null          ;
  private       int           i                        ;
  private       boolean       is_value  = true         ;
  private       double        currNumber               ;
  private       boolean       currBool                 ;
  
  public JSONParser() throws InvalidExceptionTypeError {
    super(JSONParserException.class);
  }
  
//  public boolean isWhiteSpace(char c) { return ("" + c).matches(REX_WSC); }
  public boolean isWhiteSpace(String str) { return str.matches(REX_WSS); }
  
  public void stackPush(JSONType newType) {
    if (currType != null) {
      switch (currType) {
        case ARRAY:
          stack.push(currArr);
          break;
        case OBJECT:
          stack.push(currKey);
          stack.push(currObj);
          break;
      }
    }
    
    currType = newType;
    switch (newType) {
      case ARRAY:
        currArr = new JSONArray();
        break;
      case OBJECT:
        currObj = new JSONObject();
        currKey = null;
        break;
    }
  }
  public boolean stackPop(Object value) {
    if (stack.isEmpty()) return false;
    Object cD = stack.pop();
    if (cD instanceof JSONObject) {
      String k = (String)stack.pop();
      JSONObject json = (JSONObject)cD;
      json.put(k, value);
      currObj = json;
      currType = OBJECT;
    } else if (cD instanceof JSONArray) {
      JSONArray jsar = (JSONArray)cD;
      jsar.add(value);
      currArr = jsar;
      currType = ARRAY;
    } else {
      return false;
    }
    return true;
  }
  @Override
  protected void load(String source, Reader codeReader) {
    super.load(source, codeReader);
    this.source = source;
    this.column = this.line = 0;
//    this.chars  = code.toCharArray();
    this.currArr = null;
    this.currObj = null;
    this.currStr = null;
    this.currType = null;
    this.currKey = null;
    this.stack.clear();
    this.codeReader = codeReader;
  }
  public boolean addValue(Object val) throws JSONParserException {
    if (currType == null) return true;
    boolean wasAdded = false;
    switch (currType) {
      case ARRAY:
        currArr.add(val);
        wasAdded = true;
        break;
      case OBJECT:
        if (!is_value) {
          if (val instanceof String) {
            currKey = (String)val;
            wasAdded = true;
          } else {
            error("Expected STRING, got " + typeof(val));
          }
        } else {
          currObj.put(currKey, val);
          wasAdded = true;
        }
        break;
    }
    return wasAdded;
  }
  protected String readKeyword() {
    String str = "" + c;
    while ((str + nc).matches("^[a-zA-Z][a-zA-Z0-9\\-_]*$"))
      str += advance();
    return str;
  }
  protected void errUnexpected(String str) throws JSONParserException {
    error(String.format("ERROR: Unexpected symbol: \"" + str + "\""));
  }
  protected final String parseString(char delim) { return parseString(delim, '\\',  false); }
  protected final String parseString(char delim, char esc, boolean includeDelims) {
    String str = includeDelims ? delim + "" : "" ;
    while (!(nc == delim && c != esc))
      str += advance();
    if (includeDelims) str += delim;
    advance();
    return str;
  }
  @Override
  protected Object doParsing() throws JSONParserException {
    for (; last_read_char >= 0; advance()) {
      if (isWhiteSpace(c)) continue;
      switch (c) {
        // Enable comments, because only a retard wouldn't allow that.
        case '/':
          if (nc == '/') {
            while (c != '\n' && last_last_read_char != -1) advance();
          } else if (nc == '*') {
            while (!(lc == '*' && c == '/')) advance();
          }
          break;
        case '{':
          is_value = false;
          stackPush(OBJECT);
          break;
        case '[':
          stackPush(ARRAY);
          break;
        case ']':
          if (!stackPop(currArr))
            return currArr;
          break;
        case '}':
          if (!stackPop(currObj))
            return currObj;
          break;
        case ':':
          if (!is_value && currType == OBJECT)
            is_value = true;
          break;
        case ',':
          if (is_value) {
            if (currType == OBJECT)
              is_value = false;
            else if (currType == ARRAY)
              is_value = true;
          }
          break;
        case '"':
          currStr = parseString('"');
          is_value = false;
          if (!addValue(currStr))
            return currStr;
          break;
        case 't':
        case 'f':
        case 'n':
          if (true) {
            String keywd = readKeyword();
            switch (keywd) {
              case "true":
                if (!addValue(true))
                  return true;
                break;
              case "false":
                if (!addValue(false))
                  return false;
                break;
              case "null":
                if (!addValue(null))
                  return null;
                break;
            }
          }
          break;
//        case 't':
//          if (nc == 'r') {
//            String str = "t";
//            for (i = 0; i < 3 && last_read_char >= 0; i++)
//              str += advance();
//            if (str.equals("true")) {
//              currBool = true;
////              stackPush(BOOL);
//              is_value = false;
//              if (!addValue(currBool))
//                return currBool;
//            } else errUnexpected(str);
//          }
//          break;
//        case 'f':
//          if (nc == 'a') {
//            String str = "f";
//            for (i = 0; i < 4 && last_read_char >= 0; i++)
//              str += advance();
//            if (str.equals("false")) {
//              currBool = true;
//              stackPush(BOOL);
//              is_value = false;
//              if (!stackPop(currBool))
//                return currBool;
//            } else errUnexpected(str);
//          }
//          break;
//        case 'n':
//          if (nc == 'u') {
//            String str = "n";
//            for (i = 0; i < 3 && last_read_char >= 0; i++)
//              str += advance();
//            if (str.equals("null")) {
//              currBool = true;
//              stackPush(NULL);
//              is_value = false;
//              if (!stackPop(currBool))
//                return currBool;
//            } else errUnexpected(str);
//          }
//          break;
        default:
          if (Character.isDigit(c) || (c == '-' && Character.isDigit(nc))) {
            String str = "" + c;
            while ((str + nc).matches("^[\\-]?[0-9]+(.[0-9]+)?$") || (nc == '.' && !str.contains(".")))
              str += advance();
            currNumber = Double.parseDouble(str);
            if (!addValue(currNumber))
              return currNumber;
          }
          break;
      }
    }
    
    switch (currType) {
      case OBJECT:
      case ARRAY:
        return stack.pop();
      case STRING:
        return currStr;
      case NUMBER:
        return currNumber;
      case BOOL:
        return currBool;
    }
    return null;
  }
  
}
