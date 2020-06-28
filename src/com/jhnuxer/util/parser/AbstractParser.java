package com.jhnuxer.util.parser;

import com.jhnuxer.util.parser.AbstractParser.AbstractParserException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class AbstractParser<E extends AbstractParserException,R> {
  
  protected final Class<E>       exceptionType        ;
  protected final Constructor<E> exceptionConstructor ;
  protected       Reader         codeReader           ;
  protected       String         source               ;
  protected       int            line                 ;
  protected       int            column               ;
  protected       int            last_read_char       ;
  protected       int            last_last_read_char  ;
  protected       char           lc                   ;
  protected       char           c                    ;
  protected       char           nc                   ;
  
  public AbstractParser(Class<E> eType) throws InvalidExceptionTypeError {
    this.exceptionType        = eType ;
    Constructor<E> tEC        = null  ;
    try {
      tEC = eType.getConstructor(String.class, int.class, int.class, String.class);
    } catch (NoSuchMethodException nsme) {
      nsme.printStackTrace();
      throw new InvalidExceptionTypeError(eType);
    }
    this.exceptionConstructor = tEC   ;
  }
  
  protected final void eatUntil(String pattern) { eatUntil(pattern, false); }
  protected final void eatUntil(String pattern, boolean inclusive) {
    while (!(""+c).matches(pattern)) advance();
    if (inclusive) advance();
  }
  /**
   * Grabs an entire block of code as a String for recursive block parsing.
   * 
   * @param startDelim the character that delineates the start of a block.
   * @param endDelim   the character that delineates the end of a block.
   * @return           the entire block of code as a String.
   */
  protected final String readBlock(char startDelim, char endDelim) {
    int open = 1;
    String str = "";
    advance();
    while (open > 0) {
      if (c == startDelim) open++;
      else if (c == endDelim) open--;
      if (open > 0) str += c;
      advance();
    }
    return str;
  }
  protected final String readNumber() {
    String str = "";
    boolean needsDot = true;
    while (Character.isDigit(nc) || (needsDot && nc == '.')) {
      str += advance();
      if (c == '.') needsDot = false;
    }
    return str;
  }
  protected final boolean isWhiteSpace(char c) { return ("" + c).matches("[\\s]"); }
  /** Skips whitespace characters. */
  protected final void eatWhiteSpace() { while (("" + c).matches("[\\s]")) advance(); }
  /**
   * Does the actual work of parsing the data.
   * 
   * @return 
   * @throws E 
   */
  protected abstract R doParsing() throws E;
  public R parseFile(String path) throws E { return parse(new File(path)); }
  public R parse(String code) throws E {
    return parse("Unspecified String", new StringReader(code));
  }
  public R parse(File file) throws E {
    if (file.exists()) {
      try (FileReader r = new FileReader(file)) {
        return parse(file.getName(), r);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
  public R parse(String source, InputStream in) throws E {
    return parse(source, new InputStreamReader(in));
  }
  public final R parse(String source, Reader r) throws E {
    load(source, r);
    return doParsing();
  }
  protected final String advance(int n) {
    String str = "";
    for (int _i = 0; _i < n && last_read_char >= 0; _i++)
      str += advance();
    return str;
  }
  protected char advance() {
    try {
      last_last_read_char = last_read_char;
      last_read_char = codeReader.read();
    } catch (IOException e) {
      e.printStackTrace();
      last_read_char = -1;
    }
    
    if (last_last_read_char != -1) {
      lc = c  ;
      c  = nc ;
      nc = last_read_char == -1 ? '\0' : (char)last_read_char;
      
      if (c == '\n') {
        column = 0;
        line++;
      } else {
        column++;
      }
    }
    
    return c;
  }
  protected void error(String msg) throws E {
    try {
      throw (E)exceptionConstructor.newInstance(source, line, column, msg);
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException iae) {
      iae.printStackTrace();
      System.exit(-1);
    }
  }
  protected void load(String source, Reader reader) {
    this.codeReader = reader ;
    this.source     = source ;
  }
  
  public static final class InvalidExceptionTypeError extends Exception {
    
    private final Class<? extends AbstractParserException> type ;
    
    public InvalidExceptionTypeError(Class<? extends AbstractParserException> type) {
      super(type + "; Make sure your exception class takes a source name, a line number, a column number, and an error message.");
      this.type = type ;
    }
    
  }
  public static abstract class AbstractParserException extends Exception {
    
    private final int    line   ;
    private final int    column ;
    private final String src    ;
    
    public AbstractParserException(String src, int line, int col, String msg) {
      super(String.format("<%s:%d:%d> \"%s\"", src, line, col, msg));
      
      this.column = col  ;
      this.line   = line ;
      this.src    = src  ;
    }
    
  }
  
}
