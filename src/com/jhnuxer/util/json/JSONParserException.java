package com.jhnuxer.util.json;

import com.jhnuxer.util.parser.AbstractParser;

public class JSONParserException extends AbstractParser.AbstractParserException {
  
//  private final String codeSource ;
//  private final int    line       ;
//  private final int    column     ;

//  private JSONParserException() {
//    this.codeSource = null;
//    this.line = this.column = -1;
//  }
  /**
   * Constructs an instance of <code>JSONParserException</code> with the
   * specified detail message.
   *
   * @param src    the source name.
   * @param line   the line that caused the error.
   * @param column the column at which the error occurred.
   * @param msg    the detail message.
   */
  public JSONParserException(String src, int line, int column, String msg) {
    super(src, line, column, msg);
  }
  
}
