package de.mathan.trainsimulator.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class TrainSimulatorFactory {

    public static TrainSimulator getInstance(String host, int port)
    {
      return new DefaultTrainSimulator(host, port);
    }
    
    public static TrainSimulator getInstance()
      throws IOException
    {
      System.out.println("waiting for trainsimulator-controller-server");
      MulticastSocket socket = new MulticastSocket(13912);
      InetAddress group = InetAddress.getByName("224.13.9.13");
      socket.joinGroup(group);
      byte[] data = new byte["TrainSimulator".length()];
      DatagramPacket packet = new DatagramPacket(data, data.length);
      socket.receive(packet);
      String serverIp = packet.getAddress().getHostAddress();
      System.out.println("trainsimulator-controller-server found on " + serverIp);
      socket.close();
      return new DefaultTrainSimulator(serverIp, 13913);
    }
}
