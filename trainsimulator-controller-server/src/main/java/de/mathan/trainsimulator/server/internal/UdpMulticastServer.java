package de.mathan.trainsimulator.server.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UdpMulticastServer
  extends Thread
{
  private DatagramSocket socket = null;
  InetAddress group = InetAddress.getByName("224.13.9.13");
  
  public UdpMulticastServer()
    throws IOException
  {
    this.socket = new DatagramSocket(13912);
  }
  
  public void run()
  {
    for (;;)
    {
      try
      {
        String localIP = InetAddress.getLocalHost().getHostAddress();
        byte[] data = "TrainSimulator".getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, this.group, 13912);
        this.socket.send(packet);
      }
      catch (UnknownHostException e)
      {
        e.printStackTrace();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      try
      {
        Thread.sleep(5000L);
      }
      catch (InterruptedException e)
      {
        closeSocket();
      }
    }
  }
  
  public void interrupt()
  {
    super.interrupt();
    closeSocket();
  }
  
  private void closeSocket()
  {
    if (this.socket != null)
    {
      this.socket.close();
      this.socket = null;
    }
  }
}
