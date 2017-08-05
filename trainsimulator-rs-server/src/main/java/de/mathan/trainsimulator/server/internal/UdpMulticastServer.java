package de.mathan.trainsimulator.server.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import de.mathan.trainsimulator.server.Configuration;

public class UdpMulticastServer
  extends Thread
{
  private DatagramSocket socket = null;
  private final InetAddress group ;
  private Configuration configuration;
  
  public UdpMulticastServer(Configuration configuration) throws IOException {
    this.configuration = configuration;
    this.group = InetAddress.getByName(configuration.getMulticastHost());
    this.socket = new DatagramSocket(configuration.getMulticastPort());
  }
  
  public void run()
  {
    for (;;)
    {
      try
      {
        byte[] data = "TrainSimulator".getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, this.group, configuration.getMulticastPort());
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
