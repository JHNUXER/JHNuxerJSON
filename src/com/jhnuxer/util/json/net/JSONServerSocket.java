package com.jhnuxer.util.json.net;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;

public class JSONServerSocket implements Closeable, AutoCloseable {
  
  private final ServerSocket socket ;
  
  public JSONServerSocket(int port) throws IOException { this(new ServerSocket(port)); }
  public JSONServerSocket(ServerSocket socket) { this.socket = socket ; }
  
  public JSONSocket accept() throws IOException {
    return new JSONSocket(socket.accept());
  }
  @Override
  public void close() throws IOException { socket.close(); }
  
}
