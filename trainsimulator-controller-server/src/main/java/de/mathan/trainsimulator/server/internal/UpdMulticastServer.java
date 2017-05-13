package de.mathan.trainsimulator.server.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UpdMulticastServer extends Thread{

  private static final byte[] DATA = "TrainSimulator".getBytes();
  private DatagramSocket socket = null;
  private InetAddress group;
  
  public UpdMulticastServer() throws IOException {
    socket = new DatagramSocket(13912);
    group = InetAddress.getByName("224.13.9.13");
  }
  
  public void run() {
    while(true) {
      DatagramPacket packet =new DatagramPacket(DATA, DATA.length, group, 19312);
      try {
        socket.send(packet);
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        break;
      }
    }
    closeSocket();
  }
  
  @Override
  public void interrupt() {
    super.interrupt();
    closeSocket();
  }
  
  private void closeSocket() {
    if(socket!=null) {
      socket.close();
      socket=null;
    }
  }
}
